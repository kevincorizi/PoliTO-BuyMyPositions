package it.polito.gruppo2.mamk.lab3;

import it.polito.gruppo2.mamk.lab3.models.RegisteringUser;
import org.codehaus.jackson.map.ObjectMapper;
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

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
// Speed up tests avoiding creating a real servlet environment for testing
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@ContextConfiguration
@WebAppConfiguration
public class UserFunctionalitiesTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;
    private boolean doCleanUp = false;


    @Before
    public void setup() {
        this.mvc = MockMvcBuilders
                .webAppContextSetup(this.context)
                .apply(springSecurity())
                .build();
    }


    @Test
    public void userRegisterTest() throws Exception {

        RegisteringUser mockRegUser;
        TestUtils.print("+++ Testing Registration endpoint +++");
        TestUtils.print("Attempting to register a user without providing a password...");
        // Missing password attempt
        mockRegUser = new RegisteringUser("mockUsername", null);
        this.mvc.perform(post("/api/user/registration")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(mockRegUser)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("You must provide both a Username and a Password!"));


        TestUtils.print("Attempting to register a user with invalid password...");
        // Invalid password attempt
        mockRegUser.setPassword("invalid password");
        this.mvc.perform(post("/api/user/registration")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(mockRegUser)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The provided password is not valid!"));

        // Invalid password attempt
        TestUtils.print("Attempting to register a user with invalid username...");
        mockRegUser.setPassword("correctpassword123");
        mockRegUser.setUsername("<script>malicious</script>");
        this.mvc.perform(post("/api/user/registration")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(mockRegUser)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("The username may contain only letters, numbers, and the symbols . - _"));


        // Correct registration
        this.doCleanUp = true;
        TestUtils.print("Attempting to register a user the right way...");
        mockRegUser.setUsername("mock");
        mockRegUser.setPassword("newpwd123");
        this.mvc.perform(post("/api/user/registration")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(mockRegUser)))
                .andExpect(status().isCreated());

        // Attempting to register again the same user
        TestUtils.print("Attempting to register a user with an existing username...");
        mockRegUser.setUsername("mock");
        mockRegUser.setPassword("newpwd123");
        this.mvc.perform(post("/api/user/registration")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(new ObjectMapper().writeValueAsString(mockRegUser)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Username already exists!"));

        TestUtils.print("+++ Registration endpoint tests completed! +++\n\n");

    }


    @Test
    public void userCheckExistTest() throws Exception {
        TestUtils.print("+++ Testing user existence endpoint  +++");
        TestUtils.print("Checking profile of an existing user...");

        this.mvc.perform(get("/api/user/checkExistence")
                .param("user", "max"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        TestUtils.print("Attempting to check profile of a non existing user...");

        this.mvc.perform(get("/api/user/checkExistence")
                .param("user", "nonexisting"))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));


        TestUtils.print("+++ User existence endpoint tests completed! +++\n\n");

    }

    @Test
    public void userGetProfileTests() throws Exception {
        TestUtils.print("+++ Testing profile retrieval functionalities +++");
        String accToken = TestUtils.getTokenFor("max", "max", this.mvc);
        this.mvc.perform(get("/api/user")
                .header("Authorization", "Bearer " + accToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("max"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.balance").exists())
                .andExpect(jsonPath("$.boughtArchives").exists());

        TestUtils.print("+++ Profile retrieval tests completed! +++\n\n");

    }

    @After
    public void cleanup() throws Exception {
        if (doCleanUp) {
            TestUtils.print("Performing cleanup!");
            String accToken = TestUtils.getTokenFor("mock", "newpwd123", this.mvc);
            this.mvc.perform(delete("/api/user/registration")
                    .header("Authorization", "Bearer " + accToken))
                    .andExpect(status().isNoContent());
            TestUtils.print("Deleted mock user, cleanup completed!");
            doCleanUp = false;
        }

    }
}
