package com.kr.neoshop.admin_service.controller;

import com.kr.neoshop.admin_service.DTO.ProductDto;
import com.kr.neoshop.admin_service.DTO.SellerWithProductsDTO;
import com.kr.neoshop.admin_service.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/sellers/products")
    public ResponseEntity<List<SellerWithProductsDTO>> getAllSellersWithProducts(@RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        return ResponseEntity.ok(adminService.getAllSellersWithProducts(jwtToken));
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/products/add")
    public ResponseEntity<ProductDto> addProduct(@RequestBody ProductDto productDTO, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(adminService.addProduct(productDTO, token));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductDto productDto,
            @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(adminService.updateProduct(id, productDto, token));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        adminService.deleteProduct(id, token);
        return ResponseEntity.noContent().build();
    }
}
