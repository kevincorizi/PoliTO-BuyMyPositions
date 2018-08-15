package it.polito.gruppo2.mamk.lab3;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
/**
 * Manually performs the OAuth2 process to retrieve the JWT token.
 */
public class Oauth2AuthNTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;


    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }


    /**
     * Performs a successful retrieval of a Token.
     * 1. Asks for the code to oauth/authorize with basic authentication of the user
     * 2. Asks for the JWT token to oauth/token with basic authentication of the client
     */
    @Test
    public void successfulTokenRetrieval() throws Exception {
        TestUtils.print("+++ Starting OAuth2 successful login. +++");
        TestUtils.print("Retrieving the code...");
        String location = mvc.perform(get("/oauth/authorize?response_type=code&client_id=sampleClientId")
                .with(httpBasic("max", "max")))
                .andExpect(status().is(302))
                .andReturn()
                .getResponse()
                .getHeader("Location");

        String code = TestUtils.getCodeFromLocation(location);
        TestUtils.print("Code: " + code);
        assertThat(code).isNotEmpty();

        TestUtils.print("Retrieving the token...");
        String response = mvc
                .perform(post("/oauth/token")
                        .with(httpBasic("sampleClientId", "secret"))
                        .content("code=" + code + "&grant_type=authorization_code&client_id=sampleClientId")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is(200))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ObjectMapper mapper = new ObjectMapper();
        TokenResponse token_response = mapper.readValue(response.toString(), TokenResponse.class);
        assertThat(token_response.access_token).isNotEmpty();
        TestUtils.print("JWT token: " + token_response.access_token);

        TestUtils.print("+++ Token Retrieved successfully. Test completed! +++\n\n");

    }

    /**
     * Performs a failing retrieval of a JWT Token, using wrong parameters first for the code, then for the token.
     */
    @Test
    public void failingTokenRetrieval() throws Exception {
        TestUtils.print("+++ Starting OAuth2 failing token retrieval. +++");
        TestUtils.print("Retrieving the code with wrong credentials");

        mvc.perform(get("/oauth/authorize?response_type=code&client_id=WRONGClientId")
                .with(httpBasic("massimo", "massimo")))
                .andExpect(status().is4xxClientError());

        mvc.perform(get("/oauth/authorize?response_type=code&client_id=sampleClientId")
                .with(httpBasic("WRONG", "massimo")))
                .andExpect(status().is4xxClientError());

        mvc.perform(get("/oauth/authorize?response_type=code&client_id=sampleClientId")
                .with(httpBasic("massimo", "WRONG")))
                .andExpect(status().is4xxClientError());

        TestUtils.print("Retrieving the correct code");
        String location = mvc.perform(get("/oauth/authorize?response_type=code&client_id=sampleClientId")
                .with(httpBasic("max", "max")))
                .andExpect(status().is(302))
                .andReturn()
                .getResponse()
                .getHeader("Location");

        String code = TestUtils.getCodeFromLocation(location);

        TestUtils.print("Retrieving the token with wrong parameters");
        mvc.perform(post("/oauth/token")
                .with(httpBasic("WRONGClientId", "secret"))
                .content("code=" + code + "&grant_type=authorization_code&client_id=sampleClientId")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is4xxClientError());

        mvc.perform(post("/oauth/token")
                .with(httpBasic("sampleClientId", "secret"))
                .content("code=WRONG&grant_type=authorization_code&client_id=sampleClientId")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is4xxClientError());

        mvc.perform(post("/oauth/token")
                .with(httpBasic("sampleClientId", "secret"))
                .content("code=" + code + "grant_type=authorization_code&client_id=WRONGClientId")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is4xxClientError());

        mvc.perform(post("/oauth/token")
                .with(httpBasic("sampleClientId", "WRONG"))
                .content("code=" + code + "&grant_type=authorization_code&client_id=sampleClientId")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is4xxClientError());

        TestUtils.print("+++ Failing login scenarios tested! +++\n\n");

    }

}