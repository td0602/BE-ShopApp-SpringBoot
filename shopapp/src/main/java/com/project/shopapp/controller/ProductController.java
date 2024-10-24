package com.project.shopapp.controller;

import ch.qos.logback.core.util.StringUtil;
import com.project.shopapp.dtos.ProductDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/products")
public class ProductController {
    @GetMapping("")
    public ResponseEntity<String> getAllProducts() {
        return ResponseEntity.status(HttpStatus.OK).body("OK");
    }

    // MULTIPART_FORM_DATA_VALUE khi upload file lên sẽ được chuyển từng phần
    // dung lượng
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
            MultipartFile file = productDTO.getFile();
            if(file != null) {
                // kiểm tra kích thước file
                if(file.getSize() > 10 * 1024 * 1024) {//kích thước  > 10MB
                    return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                            .body("File is too large! Maximum size is 10MB");
                }
                // check định dạng file
                String contentType = file.getContentType();
                if(contentType == null || contentType.startsWith("image/")) {
                    return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                            .body("File must be an image");
                }
                // lưu file và cập nhật thumbnail trong DTO
                String filename = storeFile(file);
                // lưu vào đối tượng product trong DB
            }

            return ResponseEntity.ok("Product created successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    private String storeFile(MultipartFile file) throws IOException {
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

}
