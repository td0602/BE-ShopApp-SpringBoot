package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "social_accounts")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder //ham khoi tao tung thanh phan
public class SocialAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 20)
    private String provider;
    @Column(name = "provider_id", nullable = false, length = 50)
    private String providerId;
    @Column(length = 150)
    private String name;
    @Column(length = 150)
    private String email;
}
