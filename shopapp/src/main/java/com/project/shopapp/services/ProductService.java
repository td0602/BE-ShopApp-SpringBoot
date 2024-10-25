package com.project.shopapp.services;

import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.Category;
import com.project.shopapp.models.Product;
import com.project.shopapp.repositories.CategoryRepository;
import com.project.shopapp.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService implements IProductService{
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Product createProduct(ProductDTO productDTO) throws DataNotFoundException {
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(
                        () -> new DataNotFoundException(
                                "Cannot find category with id: "
                                        + productDTO.getCategoryId())
                );
        Product newProduct = Product.builder()
                .name(productDTO.getName())
                .price(productDTO.getPrice())
//                .description(productDTO.getDescription())
                .thumbnail(productDTO.getThumbnail())
                .category(category)
                .build();
        return productRepository.save(newProduct);
    }

    @Override
    public Product getProductById(long productId) throws Exception {
        return productRepository.findById(productId)
                .orElseThrow(() ->
                        new DataNotFoundException("Cannot find product with id: " + productId)
                );
    }

    @Override
    public Page<Product> getAllProducts(PageRequest pageRequest) {
//        lay danh sach san pham theo trang (page) va gioi han (limit)
        return productRepository.findAll(pageRequest);
    }

    @Override
    public Product updateProduct(long id, ProductDTO productDTO) throws Exception {
        Product product = getProductById(id);
         if(product != null) {
//             copy cac thuoc tinh tu DTO -> model
//             co the su dung ModelMapper
             Category category = categoryRepository.findById(productDTO.getCategoryId()).orElseThrow(
                     () -> new DataNotFoundException(
                             "Cannot find category with id: " + productDTO.getCategoryId()
                     )
             );
             product.setName(productDTO.getName());
             product.setCategory(category);
             product.setDescription(product.getDescription());
             product.setPrice(product.getPrice());
             product.setThumbnail(product.getThumbnail());
             return productRepository.save(product);
         }
         return  null;
    }

    @Override
    public void deleteProduct(long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        optionalProduct.ifPresent(productRepository::delete);
    }

    @Override
    public boolean existsByName(String name) {
        return productRepository.existsByName(name);
    }
}
