package com.kr.neoshop.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kr.neoshop.DTO.ProductDto;
import com.kr.neoshop.model.Product;
import com.kr.neoshop.service.ProductService;


@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping("/add")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productService.addProduct(product));
    }
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProductById(id, product));
    }

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/seller")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<List<Product>> getProductsBySeller() {
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	if (auth != null) {
    	    System.out.println("Authenticated user: " + auth.getName());
    	    System.out.println("Authorities: " + auth.getAuthorities());
    	}

        return ResponseEntity.ok(productService.getProductsByCurrentSeller());
    }
    @GetMapping("/seller/{id}")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<List<Product>> getProductsBySellerId(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductsBySellerId(id));
    }

    @GetMapping("/my-products")
    @PreAuthorize("hasAuthority('ROLE_SELLER')")
    public ResponseEntity<List<ProductDto>> getMyProducts() {
        return ResponseEntity.ok(productService.getProductsForCurrentSeller());
    }
    @GetMapping("/get")
    public ResponseEntity<?> getSellerProducts() {
        // Just call service method that uses current auth context to get seller id internally
        List<Product> products = productService.getProductsBySellerId();

        if (products.isEmpty()) {
            return ResponseEntity.ok("No products available for the logged-in seller");
        }

        return ResponseEntity.ok(products);
    }
}
