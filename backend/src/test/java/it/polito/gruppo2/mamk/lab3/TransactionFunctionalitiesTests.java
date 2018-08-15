package it.polito.gruppo2.mamk.lab3;

import com.jayway.jsonpath.JsonPath;
import org.junit.After;
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
import java.util.HashMap;
import java.util.Iterator;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
// Speed up tests avoiding creating a real servlet environment for testing
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ContextConfiguration
@WebAppConfiguration
public class TransactionFunctionalitiesTests {
    @Autowired
    private WebApplicationContext context;

    private final String basePath = System.getProperty("user.dir") + "/src/test/resources/";

    private MockMvc mvc;


    private final String May1 = "1525173241000";
    private final String July22 = "1532258041000";
    private final String Jan2017 = "1483269241000";
    private final String Dec2017 = "1514718841000";

    // Stores mapping between usernames and uploaded archives
    private HashMap<String, String> userToken = new HashMap<>();
    private HashMap<String, String> userArchiveId = new HashMap<>();
    private HashMap<String, Long> userBalance = new HashMap<>();
    private HashMap<String, Long> userPosCount = new HashMap<>();

    @Before
    public void setup() {
        this.mvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();
    }

    @After
    public void cleanup() throws Exception {
        TestUtils.print("Performing cleanup...");
        for (String username : userToken.keySet()) {
            if (userArchiveId.get(username) != null) {
                TestUtils.print("Deleting archives for user " + username + "...");
                this.mvc.perform(delete("/api/archives/uploaded/" + userArchiveId.get(username))
                        .header("Authorization", "Bearer " + userToken.get(username)))
                        .andExpect(status().isNoContent());
            }
        }
    }


    @Test
    public void executeTransaction() throws Exception {
        TestUtils.print("+++ Testing Transactions functionalities +++");

        // Uploading archives and retrieving balnce of sellers
        uploadFileForUser("/positions/valid/cron-ord1.json", "max");
        uploadFileForUser("/positions/valid/cron-ord2.json", "mich");
        retrieveUserBalanceForUser("max");
        retrieveUserBalanceForUser("mich");

        // Angelo will be our buyer we retrieve its balance
        String accTokenAng = TestUtils.getTokenFor("angelo", "angelo", this.mvc);
        userToken.put("angelo", accTokenAng);
        retrieveUserBalanceForUser("angelo");

        TestUtils.print("Attempting to get approximated positions: invalid polygon...");
        this.mvc.perform(get("/api/archives")
                .header("Authorization", "Bearer " + accTokenAng)
                .param("area", TestUtils.readFileAsString(basePath + "/positions/polygons/InvalidBrazilPolygon", Charset.defaultCharset()))
                .param("from", May1)
                .param("to", July22))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The provided object is not a valid GeoJsonPolygon!"));

        TestUtils.print("Attempting to get approximated positions: missing mandatory parameter...");
        this.mvc.perform(get("/api/archives")
                .header("Authorization", "Bearer " + accTokenAng)
                .param("area", TestUtils.readFileAsString(basePath + "/positions/polygons/ValidBrazilPolygon", Charset.defaultCharset()))
                .param("to", July22))
                .andExpect(status().isBadRequest());

        TestUtils.print("Attempting to get approximated positions: valid parameters, empty result...");
        this.mvc.perform(get("/api/archives")
                .header("Authorization", "Bearer " + accTokenAng)
                .param("area", TestUtils.readFileAsString(basePath + "/positions/polygons/ValidBrazilPolygon", Charset.defaultCharset()))
                .param("from", May1)
                .param("to", July22))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(0)));

        TestUtils.print("Attempting to get approximated positions: valid parameters, empty result...");
        String result = this.mvc.perform(get("/api/archives")
                .header("Authorization", "Bearer " + accTokenAng)
                .param("area", TestUtils.readFileAsString(basePath + "/positions/polygons/ValidMilanTurinPolygon", Charset.defaultCharset()))
                .param("from", May1)
                .param("to", July22))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2))).andReturn().getResponse().getContentAsString();


        TestUtils.print("Computing expected balance variations for seller and buyer...");
        userPosCount.put(JsonPath.parse(result).read("$[0].ownerUsername"),
                Long.valueOf(JsonPath.parse(result).read("$[0].realPositions").toString()));
        userPosCount.put(JsonPath.parse(result).read("$[1].ownerUsername"),
                Long.valueOf(JsonPath.parse(result).read("$[1].realPositions").toString()));


        TestUtils.print("The retrieved archives contain:");
        Long buyerBalance = 0L;
        for (String username : userPosCount.keySet()) {
            TestUtils.print("User - " + username + " | Positions: " + userPosCount.get(username));
            buyerBalance -= userPosCount.get(username);
        }

        // Setting up amount to pay for buyer
        userPosCount.put("angelo", buyerBalance);


        // Building the purchase request we know the set contains at least one position
        StringBuilder builder = new StringBuilder("[");
        Iterator<String> archiveIds = userArchiveId.keySet().iterator();
        while (true) {
            builder.append("\"" + userArchiveId.get(archiveIds.next()) + "\"");
            if (archiveIds.hasNext()) builder.append(",");
            else {
                builder.append("]");
                break;
            }
        }

        TestUtils.print("Performing a transaction as user angelo..." + builder.toString());
        this.mvc.perform(post("/api/transactions")
                .header("Authorization", "Bearer " + accTokenAng)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(builder.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(userArchiveId.get("mich")))
                .andExpect(jsonPath("$[1].id").value(userArchiveId.get("max")));


        TestUtils.print("Checking users balance after the transaction...");
        TestUtils.print("New balance for: ");
        for (String user : userBalance.keySet()) {
            checkBalanceForUser(user, userBalance.get(user));
        }


        TestUtils.print("+++ Transactions functionalities tests completed! +++\n\n");
    }


    public void retrieveUserBalanceForUser(String username) throws Exception {
        String profile = this.mvc.perform(get("/api/user")
                .header("Authorization", "Bearer " + userToken.get(username)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        userBalance.put(username, Long.valueOf(JsonPath.parse(profile).read("$.balance").toString()));
        TestUtils.print("USER - " + username + " | Balance: " + JsonPath.parse(profile).read("$.balance"));
    }

    public void checkBalanceForUser(String username, Long oldbalance) throws Exception {
        String res = this.mvc.perform(get("/api/user")
                .header("Authorization", "Bearer " + userToken.get(username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(oldbalance + userPosCount.get(username)))
                .andReturn().getResponse().getContentAsString();
        TestUtils.print("USER - " + username + " | Balance: " + JsonPath.parse(res).read("$.balance"));
    }

    public void uploadFileForUser(String relPath, String username) throws Exception {
        this.userToken.put(username, TestUtils.getTokenFor(username, username, this.mvc));
        TestUtils.print("Uploading archive as " + username + "...");
        this.mvc.perform(post("/api/archives/uploaded")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .header("Authorization", "Bearer " + userToken.get(username))
                .content(TestUtils.readFileAsString(basePath + relPath, Charset.defaultCharset())))
                .andExpect(content().string(""))
                .andExpect(status().isCreated());

        String result = this.mvc.perform(get("/api/archives/uploaded")
                .header("Authorization", "Bearer " + userToken.get(username)))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        this.userArchiveId.put(username, JsonPath.parse(result).read("$[0].id"));
    }


}
