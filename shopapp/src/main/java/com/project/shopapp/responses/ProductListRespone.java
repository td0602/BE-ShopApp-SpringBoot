package com.project.shopapp.responses;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProductListRespone {
    private List<ProductResponse> products;
    private int totalPage;
}
