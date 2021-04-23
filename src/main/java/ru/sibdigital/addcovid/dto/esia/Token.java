package ru.sibdigital.addcovid.dto.esia;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Token {

    @JsonAlias("access_token")
    private String accessToken;
    @JsonAlias("expires_in")
    private Long expiresIn;
    @JsonAlias("state")
    private String state;
    @JsonAlias("token_type")
    private String tokenType;

    public boolean isEmpty() {
        return accessToken == null;
    }
}
