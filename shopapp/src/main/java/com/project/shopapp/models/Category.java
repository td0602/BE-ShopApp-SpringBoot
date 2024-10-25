package com.project.shopapp.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ValueGenerationType;

@Entity
@Data //toString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "categories")
@Builder //ham khoi tao tung thanh phan
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;

}
