package com.project.shopapp.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api.prefix}/categories")
//@Validated
public class CategoryController {
    // Hiển thị tất cả các categories
//    @GetMapping("")
}
