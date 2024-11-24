package com.project.shopapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_images")
@Data //toString
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder //ham khoi tao tung thanh phan
public class ProductImage {
    public static final int MAXIMUM_IMAGES_PER_PRODUCT = 5;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonIgnore // đưa 1 đối tượng về json
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "image_url", length = 300)
    @JsonProperty("image_url") // khi truyền data dạng json với field name = image_ủl
    private String imageUrl;
}
