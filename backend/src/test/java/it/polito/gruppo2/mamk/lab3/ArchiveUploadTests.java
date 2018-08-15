package it.polito.gruppo2.mamk.lab3;

import com.jayway.jsonpath.JsonPath;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
// Speed up tests avoiding creating a real servlet environment for testing
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ContextConfiguration
@WebAppConfiguration
/**
 *   Performs an integration test evaluating functionalities related to Archives upload
 *   and enforcing the related constraints as well as functionalities allowing to users to retrieve and delete
 *   their own Archives
 * */
public class ArchiveUploadTests {

    @Autowired
    private WebApplicationContext context;

    private final String basePath = System.getProperty("user.dir") + "/src/test/resources/positions/";

    private MockMvc mvc;

    @Before
    public void setup() {
        this.mvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();
    }


    @Test
    public void uploadTest() throws Exception {
        TestUtils.print("+++ Testing Archive Upload functionalities +++");

        String accToken = TestUtils.getTokenFor("max", "max", this.mvc);

        TestUtils.print("Attempting to upload Archive exceeding speed threshold....");
        String body = TestUtils.readFileAsString(basePath + "/invalid/too-fast.json", Charset.defaultCharset());
        this.mvc.perform(post("/api/archives/uploaded")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + accToken)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Speed threshold exceeded!"));

        TestUtils.print("Attempting to archive with invalid positions...");
        body = TestUtils.readFileAsString(basePath + "/invalid/future.json", Charset.defaultCharset());
        this.mvc.perform(post("/api/archives/uploaded")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + accToken)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("It is not allowed to post a position in the future!"));


        TestUtils.print("Uploading valid archive A ...");
        body = TestUtils.readFileAsString(basePath + "/invalid/invalid_partA.json", Charset.defaultCharset());
        this.mvc.perform(post("/api/archives/uploaded")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + accToken)
                .content(body))
                .andExpect(status().isCreated());

        TestUtils.print("Uploading another valid archive B ...");
        body = TestUtils.readFileAsString(basePath + "/invalid/invalid_partB.json", Charset.defaultCharset());
        this.mvc.perform(post("/api/archives/uploaded")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + accToken)
                .content(body))
                .andExpect(status().isCreated());

        TestUtils.print("Attempting to upload archive C conflicting with B ...");
        body = TestUtils.readFileAsString(basePath + "/invalid/invalid-partC.json", Charset.defaultCharset());
        this.mvc.perform(post("/api/archives/uploaded")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + accToken)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Provided timestamp overlapping with previous ones!"));


        TestUtils.print("Retrieving all archives uploaded by the user...");
        String result = this.mvc.perform(get("/api/archives/uploaded")
                .header("Authorization", "Bearer " + accToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].ownerUsername").value("max"))
                .andExpect(jsonPath("$[0].canBeBought").value("true"))
                .andExpect(jsonPath("$[0].timesBought").value(0))
                .andReturn().getResponse().getContentAsString();

        // Retrieving ids of archives for cleanup
        String id = JsonPath.parse(result).read("$[0].id");
        String idB = JsonPath.parse(result).read("$[1].id");

        TestUtils.print("Retrieving a specific archive...");
        this.mvc.perform(get("/api/archives/uploaded/" + id)
                .header("Authorization", "Bearer " + accToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ownerUsername").value("max"))
                .andExpect(jsonPath("$.canBeBought").value("true"))
                .andExpect(jsonPath("$.timesBought").value(0))
                .andExpect(jsonPath("$.id").value(id));

        TestUtils.print("Performing cleanup...");
        TestUtils.print("Deleting uploaded archives...");
        this.mvc.perform(delete("/api/archives/uploaded/" + id)
                .header("Authorization", "Bearer " + accToken))
                .andExpect(status().isNoContent());
        this.mvc.perform(delete("/api/archives/uploaded/" + idB)
                .header("Authorization", "Bearer " + accToken))
                .andExpect(status().isNoContent());


        TestUtils.print("+++  Archive Upload functionalities tests completed  +++\n\n");

    }


}
