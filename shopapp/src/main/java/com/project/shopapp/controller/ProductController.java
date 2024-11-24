package com.project.shopapp.controller;

import ch.qos.logback.core.util.StringUtil;
import com.github.javafaker.Faker;
import com.project.shopapp.components.LocalizationUtils;
import com.project.shopapp.dtos.ProductDTO;
import com.project.shopapp.dtos.ProductImageDTO;
import com.project.shopapp.models.Product;
import com.project.shopapp.models.ProductImage;
import com.project.shopapp.responses.ProductListRespone;
import com.project.shopapp.responses.ProductListResponse;
import com.project.shopapp.responses.ProductResponse;
import com.project.shopapp.services.IProductService;
import com.project.shopapp.services.ProductService;
import com.project.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("${api.prefix}/products")
public class ProductController {
    private final IProductService productService;
    private final LocalizationUtils localizationUtils;

    // MULTIPART_FORM_DATA_VALUE khi upload file lên sẽ được chuyển từng phần
    // dung lượng
    @PostMapping(value = "")
    public ResponseEntity<?> createProduct(
            @Valid @RequestBody ProductDTO productDTO,
//            @RequestPart("file") MultipartFile file,
            BindingResult result
            ) {
        try {
            if(result.hasErrors()) {
                List<String> errorMessages = result.getFieldErrors()
                        .stream()
                        .map(FieldError::getDefaultMessage)
                        .toList();
                return ResponseEntity.badRequest().body(errorMessages);
            }
            Product newProduct = productService.createProduct(productDTO);

            return ResponseEntity.ok(newProduct);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PostMapping(value = "uploads/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    private ResponseEntity<?> uploadImages(
            @PathVariable("id") Long productId,
            @RequestParam("files") List<MultipartFile> files
    ) {
        try {
            Product existingProduct = productService.getProductById(productId);
            files = files != null ? files : new ArrayList<MultipartFile>(); // tranh null
            if(files.size() > ProductImage.MAXIMUM_IMAGES_PER_PRODUCT) {
                return ResponseEntity.badRequest().body(localizationUtils.getLocalizedMessage(
                        MessageKeys.UPLOAD_IMAGES_MAX_5
                ));
            }
            List<ProductImage> productImages = new ArrayList<>();
            for(MultipartFile file: files) {
                if(file != null) {
                    if(file.getSize() == 0) continue;
                    // kiểm tra kích thước file
                    if(file.getSize() > 10 * 1024 * 1024) {//kích thước  > 10MB
                        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                                .body(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_IMAGES_FILE_LARGE));
                    }
                    // check định dạng file
                    String contentType = file.getContentType();
                    if(contentType == null || !contentType.startsWith("image/")) {
                        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                                .body(localizationUtils.getLocalizedMessage(MessageKeys.UPLOAD_FILE_MUST_BE_IMAGE));
                    }
                    // lưu file và cập nhật thumbnail trong DTO
                    String filename = storeFile(file);
                    // lưu vào đối tượng product trong DB product_images
                    ProductImage productImage = productService.createProductImage(
                            existingProduct.getId(),
                            ProductImageDTO.builder()
                                    .productId(existingProduct.getId())
                                    .imageUrl(filename)
                                    .build()
                    );
                    productImages.add(productImage);
                }
            }
            return ResponseEntity.ok().body(productImages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
//    hàm xem nội dung của ảnh
    @GetMapping("/images/{imageName}")
    public ResponseEntity<?> viewImage(@PathVariable String imageName) {
        try {
            Path imagePath = Paths.get("uploads/"+imageName);
            UrlResource resource = new UrlResource(imagePath.toUri());

            if(resource.exists()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            } else {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(new UrlResource((Paths.get("uoloads/notfound.jpg").toUri())));
//                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    private String storeFile(MultipartFile file) throws IOException {
        if(!isImageFile(file) || file.getOriginalFilename() == null) {
            throw new IOException("Invalid image format");
        }
        // lấy tên file gốc
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        // đổi tên tránh trùng
        String uniqueFilename = UUID.randomUUID().toString() + "_" + filename;
        // đường dẫn tới thư mục chứa file
        Path uploadDir = Paths.get("uploads");
        // nếu không tồn tại thư mục thì tạo mới
        if(!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        // đường dẫn đầy đủ đến file
        Path destination = Paths.get(uploadDir.toString(), uniqueFilename);
        // sao chép file vào thư mục đích: nếu có thì sẽ thay thế
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFilename;
    }
    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }

    @GetMapping("")
    public ResponseEntity<ProductListResponse> getProducts(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "0", name = "category_id") Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit
    ) {
        // Tạo Pageable từ thông tin trang và giới hạn
        PageRequest pageRequest = PageRequest.of(
                page, limit,
                //Sort.by("createdAt").descending()
                Sort.by("id").ascending()
        );
        Page<ProductResponse> productPage = productService.getAllProducts(keyword, categoryId, pageRequest);
        // Lấy tổng số trang
        int totalPages = productPage.getTotalPages();
        List<ProductResponse> products = productPage.getContent();
        return ResponseEntity.ok(ProductListResponse
                .builder()
                .products(products)
                .totalPages(totalPages)
                .build());
    }

//    @PostMapping("/generateFakerProducts")
    public ResponseEntity<?> generateFakerProducts() {
        Faker faker = new Faker();
        for(int i=0; i < 1000000; i++) {
            String productName = faker.commerce().productName();
            if(productService.existsByName(productName)) {
                continue;
            }
            ProductDTO productDTO = ProductDTO.builder()
                    .name(productName)
                    .price((float)faker.number().numberBetween(10, 90000000))
                    .description(faker.lorem().sentence())
                    .thumbnail("")
                    .categoryId((long) faker.number().numberBetween(2, 5))
                    .build();
            try {
                productService.createProduct(productDTO);
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
        return ResponseEntity.ok("Fake products created successfully");
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(
            @PathVariable("id") Long id
    ) {
        try {
            Product existingProduct = productService.getProductById(id);
            return ResponseEntity.ok(ProductResponse.fromProduct(existingProduct));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(
            @PathVariable Long id
    ) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(String.format("Product with id = %d deleted successfully", id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDTO productDTO
    ) {
        try {
            Product updateProduct = productService.updateProduct(id, productDTO);
            return ResponseEntity.ok(updateProduct);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/by-ids")
    public ResponseEntity<?> getProductsByIds(@RequestParam("ids") String ids) {
        //eg: 1,3,5,7
        try {
            // Tách chuỗi ids thành một mảng các số nguyên
            List<Long> productIds = Arrays.stream(ids.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            List<Product> products = productService.findProductsByIds(productIds);
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
//    public ResponseEntity<String> deleteProduct(@PathVariable long id) {
//        try {
//            productService.deleteProduct(id);
//            return ResponseEntity.ok(String.format("Product with id = %d deleted successfully", id));
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body(e.getMessage());
//        }
//    }

    //@PostMapping("/generateFakeProducts")
//    private ResponseEntity<String> generateFakeProducts() {
//        Faker faker = new Faker();
//        for (int i = 0; i < 1_000_000; i++) {
//            String productName = faker.commerce().productName();
//            if (productService.existsByName(productName)) {
//                continue;
//            }
//            ProductDTO productDTO = ProductDTO.builder()
//                    .name(productName)
//                    .price((float) faker.number().numberBetween(10, 90_000_000))
//                    .description(faker.lorem().sentence())
//                    .thumbnail("")
//                    .categoryId((long) faker.number().numberBetween(2, 5))
//                    .build();
//            try {
//                productService.createProduct(productDTO);
//            } catch (Exception e) {
//                return ResponseEntity.badRequest().body(e.getMessage());
//            }
//        }
//        return ResponseEntity.ok("Fake Products created successfully");
//    }

    //update a product
//    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
//    //@SecurityRequirement(name="bearer-key")
//    @Operation(security = {@SecurityRequirement(name = "bearer-key")})
//    public ResponseEntity<?> updateProduct(
//            @PathVariable long id,
//            @RequestBody ProductDTO productDTO) {
//        try {
//            Product updatedProduct = productService.updateProduct(id, productDTO);
//            return ResponseEntity.ok(updatedProduct);
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
//        }
//    }
}
