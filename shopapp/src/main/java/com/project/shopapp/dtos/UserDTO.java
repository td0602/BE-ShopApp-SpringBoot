package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Date;

@Data // toString(
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserDTO {
    @JsonProperty("fullname")
    private String fullName;
    @JsonProperty("phone_number")
    @NotBlank(message = "Phone number is required")
    private String phoneNumber;
    @NotBlank(message = "Address is required")
    private String address;
    @NotBlank(message = "Password cannot be blank")
    private String password;
    @JsonProperty("retype_password")
    private String retypePassword;
    @JsonProperty("date_of_birth")
    private Date dateOfBirth;
    @JsonProperty("facebook_account_id")
    private int facebookAccountId;
    @JsonProperty("role_id")
    @NotNull(message = "Role ID is required")
    private int roleId;
}
