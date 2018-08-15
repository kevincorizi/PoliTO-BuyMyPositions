package it.polito.gruppo2.mamk.lab3;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TestUtils {

    public static void print(String message) {
        StringBuilder sb = new StringBuilder();
        sb.append("[ <-- TEST --> ] [ ");
        sb.append(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime()));
        sb.append(" ] ");
        sb.append(message);
        System.out.println(sb.toString());
    }

    public static String getCodeFromLocation(String location) {
        final String beginString = "code=";
        return location.substring(location.indexOf(beginString) + beginString.length());
    }

    public static String getTokenFor(String username, String password, MockMvc mvc) {

        TestUtils.print("Performing login as " + username);

        // Retrieving token code
        String code;
        try {
            String location = mvc.perform(get("/oauth/authorize?response_type=code&client_id=sampleClientId")
                    .with(httpBasic(username, password)))
                    .andExpect(status().is(302))
                    .andReturn()
                    .getResponse()
                    .getHeader("Location");

            code = getCodeFromLocation(location);
            assertThat(code).isNotEmpty();

        } catch (Exception e) {
            TestUtils.print("Something went wrong retrieving token code for " + username);
            return null;
        }

        TokenResponse tokenResponse;
        try {
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
            tokenResponse = mapper.readValue(response.toString(), TokenResponse.class);
            assertThat(tokenResponse.access_token).isNotEmpty();
        } catch (Exception e) {
            TestUtils.print("Something went wrong retrieving the access token...");
            return null;
        }

        TestUtils.print("Token for user " + username + " retrieved successfully!");
        return tokenResponse.access_token;
    }

    static String readFileAsString(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }


}