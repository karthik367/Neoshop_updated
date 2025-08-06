package com.kr.neoshop.admin_service.client;

import com.kr.neoshop.admin_service.DTO.ProductDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ProductClient {

    private final RestTemplate restTemplate;
    private final String productServiceUrl;

    public ProductClient(RestTemplate restTemplate,
                         @Value("${product.service.url}") String productServiceUrl) {
        this.restTemplate = restTemplate;
        this.productServiceUrl = productServiceUrl;
    }

    public ProductDto addProduct(ProductDto productDTO, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<ProductDto> request = new HttpEntity<>(productDTO, headers);
        ResponseEntity<ProductDto> response = restTemplate.postForEntity(
                productServiceUrl + "/api/products/add",
                request,
                ProductDto.class
        );

        return response.getBody();
    }

    public ProductDto updateProduct(Long productId, ProductDto product, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        HttpEntity<ProductDto> request = new HttpEntity<>(product, headers);
        ResponseEntity<ProductDto> response = restTemplate.exchange(
                productServiceUrl + "/api/products/update/" + productId,
                HttpMethod.PUT,
                request,
                ProductDto.class
        );
        return response.getBody();
    }

    public void deleteProduct(Long productId, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        restTemplate.exchange(
                productServiceUrl + "/api/products/delete/" + productId,
                HttpMethod.DELETE,
                request,
                Void.class
        );
    }
}
