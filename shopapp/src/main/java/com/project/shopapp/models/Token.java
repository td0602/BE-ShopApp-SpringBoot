package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tokens")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 255)
    private String token;
    @Column(name = "token_type", length = 50)
    private String tokenType;
//    Ngay gio phut giay ao het han
    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;
    private boolean revoked; // bi huy ?
    private boolean expired; // het han ?
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
