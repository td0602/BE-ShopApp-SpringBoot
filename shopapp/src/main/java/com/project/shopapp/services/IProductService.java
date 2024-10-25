package com.project.shopapp.services;

import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

public interface IProductService {
    Product createProduct(ProductDTO productDTO) throws DataNotFoundException;
    Product getProductById(long id) throws Exception;
    Page<Product> getAllProducts(PageRequest pageRequest);
    Product updateProduct(long id, ProductDTO productDTO) throws Exception;
    void deleteProduct(long id);
    boolean existsByName(String name);
}
