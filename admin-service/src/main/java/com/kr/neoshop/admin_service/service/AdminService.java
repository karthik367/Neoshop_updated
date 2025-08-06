package com.kr.neoshop.admin_service.service;

import com.kr.neoshop.admin_service.DTO.ProductDto;
import com.kr.neoshop.admin_service.DTO.SellerWithProductsDTO;
import com.kr.neoshop.admin_service.client.ProductClient;
import com.kr.neoshop.admin_service.client.UserClient;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {

    private final UserClient userClient;
    private final ProductClient productClient;

    public AdminService(UserClient userClient, ProductClient productClient) {
        this.userClient = userClient;
        this.productClient = productClient;
    }

    public List<SellerWithProductsDTO> getAllSellersWithProducts(String token) {
        return userClient.getAllSellersWithProducts(token);
    }

    public void deleteUser(Long userId) {
        userClient.deleteUser(userId);
    }

    public ProductDto addProduct(ProductDto productDTO, String token) {
        return productClient.addProduct(productDTO, token);
    }

    public ProductDto updateProduct(Long productId, ProductDto productDto, String token) {
        return productClient.updateProduct(productId, productDto, token);
    }

    public void deleteProduct(Long productId, String token) {
        productClient.deleteProduct(productId, token);
    }
}
