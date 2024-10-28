package com.project.shopapp.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
//@Getter
//@Setter
@Builder
public class OrderResponse extends BaseResponse{
    private Long id;
    @JsonProperty("user_id")
    private Long uerId;
    @JsonProperty("full_name")
    private String fullName;
    @JsonProperty("phone_number")
    private String phoneNumber;
    protected String address;
    private String note;
    @JsonProperty("order_date")
    private LocalDateTime orderDate;
    @JsonProperty("shipping_method")
    private String status;
    @JsonProperty("total_money")
    private Float totalMoney;
    private String shippingMethod;
    @JsonProperty("shipping_address")
    private String shippingAddress;
    @JsonProperty("shipping_date")
    private Data shippingDate;
    @JsonProperty("tracking_number")
    private String trackingNumber;
}
