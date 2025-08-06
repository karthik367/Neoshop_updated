package com.kr.neoshop.admin_service.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.kr.neoshop.admin_service.DTO.ProductDto;
import com.kr.neoshop.admin_service.DTO.SellerWithProductsDTO;
import com.kr.neoshop.admin_service.DTO.UserDto;

@Component
public class UserClient {

    private final RestTemplate restTemplate;
    private final String userServiceUrl;
    private final String productServiceUrl;

    public UserClient(RestTemplate restTemplate,
                      @Value("${user.service.url}") String userServiceUrl,
                      @Value("${product.service.url}") String productServiceUrl) {
        this.restTemplate = restTemplate;
        this.userServiceUrl = userServiceUrl;
        this.productServiceUrl = productServiceUrl;
    }

    public List<SellerWithProductsDTO> getAllSellersWithProducts(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<UserDto[]> response = restTemplate.exchange(
                userServiceUrl + "/role/SELLER",
                HttpMethod.GET,
                request,
                UserDto[].class
        );

        List<UserDto> sellers = Arrays.asList(response.getBody());
        List<SellerWithProductsDTO> result = new ArrayList<>();

        for (UserDto seller : sellers) {
            SellerWithProductsDTO dto = new SellerWithProductsDTO();
            dto.setId(seller.getId());
            dto.setUsername(seller.getUsername());
            dto.setEmail(seller.getEmail());
            dto.setPhoneNumber(seller.getPhoneNumber());
            Set<String> roles = seller.getRoles() != null
            	    ? seller.getRoles()           // ✅ Wrap single string into a Set
            	    : Collections.emptySet();

            	dto.setRoles(roles);


            String productUrl = productServiceUrl + "/api/products/seller/" + seller.getId();
            HttpEntity<Void> productRequest = new HttpEntity<>(headers);

            ResponseEntity<ProductDto[]> productResponse = restTemplate.exchange(
                    productUrl,
                    HttpMethod.GET,
                    productRequest,
                    ProductDto[].class
            );

            dto.setProducts(Arrays.asList(productResponse.getBody()));
            result.add(dto);
        }

        return result;
    }

    public void deleteUser(Long userId) {
        restTemplate.delete(userServiceUrl + "/api/users/" + userId);
    }
}
