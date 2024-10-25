package com.project.shopapp.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
@Data
@AllArgsConstructor
@Setter
@Getter
public class OrderDTO {
    @JsonProperty("user_id")
    @Min(value = 1, message = "User's ID must be > 0")
    private int userId;
    @JsonProperty("fullname")
    private String fullName;
    private String email;
    @JsonProperty("phone_number")
    @NotBlank(message = "Phone number is reuquired")
    @Size(min = 5, message = "Phone number must be at least 5 characters")
    private String phoneNumber;
    private String address;
    private String note;
    @JsonProperty("total_money")
    @Min(value = 0, message = "Total money must be >= 0")
    private Float totalMoney;
    @JsonProperty("shipping_address")
    private String shippingAddress;
    @JsonProperty("payment_method")
    private String paymentMethod;
}
