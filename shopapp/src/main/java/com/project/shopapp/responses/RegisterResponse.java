package com.project.shopapp.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.shopapp.models.User;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RegisterResponse {
    @JsonProperty("message")
    private String message;
    @JsonProperty("user")
    private User user;

}
