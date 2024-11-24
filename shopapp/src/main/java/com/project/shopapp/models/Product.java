package com.project.shopapp.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "products")
@Builder //ham khoi tao tung thanh phan
public class Product extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 350)
    private String name;
    private Float price;
    @Column(length = 300)
    private String thumbnail;
    private String description;
    @ManyToOne
    @JsonProperty("category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<ProductImage> productImages;

//    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
//    @JsonManagedReference
//    private List<Comment> comments = new ArrayList<>();
}
