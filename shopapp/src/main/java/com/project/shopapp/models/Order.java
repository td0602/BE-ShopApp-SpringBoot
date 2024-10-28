package com.project.shopapp.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder //ham khoi tao tung thanh phan
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "fullname", length = 100)
    private String fullName;
    @Column(length = 100)
    private String email;
    @Column(name = "phone_number", nullable = false, length = 100)
    private String phoneNumber;
    @Column(length = 100)
    private String address;
    @Column(length = 100)
    private String note;
    @Column(name = "order_date")
    private Date orderDate;
    private String status;
    @Column(name = "total_money")
     private Float totalMoney;
    @Column(name = "shipping_method")
    private String shippingMethod;
    @Column(name = "shipping_address")
    private String shippingAddress;
    @Column(name = "shipping_date")
    private LocalDate shippingDate;
    @Column(name = "tracking_number")
    private String trackingNumber;
    @Column(name = "payment_method")
    private String paymentMethod;
//    @Column(name = "payment_status")
//    private String paymentStatus;
//    @Column(name = "payment_date")
//    private Date paymentDate;
    private Boolean active;
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
}
