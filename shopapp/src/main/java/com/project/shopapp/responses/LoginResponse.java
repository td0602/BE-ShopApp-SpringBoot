package com.project.shopapp.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class LoginResponse {
    @JsonProperty("message")
    private String message;
    @JsonProperty("token")
    private String token;
}
