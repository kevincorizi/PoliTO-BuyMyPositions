package it.polito.gruppo2.mamk.lab3;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TokenResponse {

    String access_token;
    String token_type;
    Integer expires_in;
    String scope;
    String jti;

    @JsonCreator
    public TokenResponse(@JsonProperty(value = "access_token") String access_token,
                         @JsonProperty(value = "token_type") String token_type,
                         @JsonProperty(value = "expires_in") Integer expires_in,
                         @JsonProperty(value = "scope") String scope,
                         @JsonProperty(value = "jti") String jti) {
        this.access_token = access_token;
        this.token_type = token_type;
        this.expires_in = expires_in;
        this.scope = scope;
        this.jti = jti;
    }
}