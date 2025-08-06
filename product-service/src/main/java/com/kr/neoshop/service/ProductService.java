package com.kr.neoshop.service;



import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.kr.neoshop.DTO.ProductDto;
import com.kr.neoshop.DTO.UserDTO;
import com.kr.neoshop.client.UserClient;
import com.kr.neoshop.model.Product;
import com.kr.neoshop.repository.ProductRepository;

import jakarta.servlet.http.HttpServletRequest;
@Service
public class ProductService {
 @Autowired
 private UserClient userClient;

  private final ProductRepository productRepo;
  private final RestTemplate restTemplate;

  @Value("${user.service.url}")
  private String userServiceUrl; // e.g., http://localhost:8081/api/users

  public ProductService(ProductRepository productRepo, RestTemplate restTemplate) {
    this.productRepo = productRepo;
    this.restTemplate = restTemplate;
  }
  public Product addProduct(Product product) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();
    UserDTO user = userClient.getUserByUsername(username);
    System.out.println("User roles from UserDTO: " + user.getRoles());
    Set<String> roles = user.getRoles();
    if (roles == null) {

      throw new RuntimeException("User roles not available");
    }
    boolean isSeller = roles.contains("ROLE_SELLER");
    boolean isAdmin = roles.contains("ROLE_ADMIN");

    if (isAdmin) {
      if (product.getSellerId() == null) {
        throw new RuntimeException("Admin must provide sellerId when adding product");
      }
    } else if (isSeller) {
      product.setSellerId(user.getId()); // ensure seller can only assign themselves
    } else {
      throw new RuntimeException("Unauthorized to add products");
    }

    product.setCreatedAt(LocalDateTime.now());
    product.setUpdatedAt(LocalDateTime.now());

    return productRepo.save(product);
  }
  public List<Product> getAllProducts() {
    return productRepo.findAll();
  }

  public Product getProductById(Long id) {
    return productRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("Product not found"));
  }

  public Product updateProductById(Long id, Product newProductData) {
    Product existingProduct = productRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("Product not found"));

    // Get the current user details (including roles)
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName(); // This should be the username from JWT
    UserDTO user = userClient.getUserByUsername(username); // Assuming you fetch via RestTemplate
    Set<String> roles = user.getRoles();
    if (roles == null) {
      throw new RuntimeException("User roles not available");
    }
    boolean isAdmin = user.getRoles().contains("ROLE_ADMIN");
    boolean isSellerOwner = existingProduct.getSellerId().equals(user.getId());

    if (!(isSellerOwner || isAdmin)) {
      throw new RuntimeException("You can only update your own product");
    }

    // Proceed to update product
    existingProduct.setName(newProductData.getName());
    existingProduct.setBrand(newProductData.getBrand());
    existingProduct.setCategory(newProductData.getCategory());
    existingProduct.setPrice(newProductData.getPrice());
    existingProduct.setStock(newProductData.getStock());
    existingProduct.setUpdatedAt(LocalDateTime.now());

    return productRepo.save(existingProduct);
  }

  public void deleteProductById(Long id) {
    Product product = productRepo.findById(id)
        .orElseThrow(() -> new RuntimeException("Product not found"));

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();

    UserDTO user = userClient.getUserByUsername(username);
    Set<String> roles = user.getRoles();
    if (roles == null) {
      throw new RuntimeException("User roles not available");
    }
    boolean isAdmin = user.getRoles().contains("ROLE_ADMIN");
    boolean isSellerOwner = product.getSellerId().equals(user.getId());

    if (!(isSellerOwner || isAdmin)) {
      throw new RuntimeException("You can only delete your own product");
    }

    productRepo.deleteById(id);
  }
  public List<Product> getProductsBySellerId(Long sellerId) {
    return productRepo.findBySellerId(sellerId);
  }

  public String getCurrentUsername() {
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }
  private Long getCurrentSellerIdFromUserService() {
    String jwtToken = extractTokenFromRequest();
    if (jwtToken == null) {
      throw new RuntimeException("JWT token not found in request");
    }

    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    String username = auth.getName();

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(jwtToken);
    headers.setContentType(MediaType.APPLICATION_JSON);
    HttpEntity<String> entity = new HttpEntity<>(headers);

    ResponseEntity<UserDTO> response = restTemplate.exchange(
        userServiceUrl + "/username/" + username,
        HttpMethod.GET,
        entity,
        UserDTO.class
    );

    if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
      throw new RuntimeException("Failed to fetch user from User Service");
    }

    return response.getBody().getId();
  }

  // ✅ Extract JWT token from current HTTP request header
  private String extractTokenFromRequest() {
    ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (attr == null) return null;

    HttpServletRequest request = attr.getRequest();
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7); // remove "Bearer "
    }
    return null;
  }
  public ProductDto toDto(Product product, String sellerUsername) {
    ProductDto dto = new ProductDto();
    dto.setId(product.getId());
    dto.setName(product.getName());
    dto.setBrand(product.getBrand());
    dto.setCategory(product.getCategory());
    dto.setPrice(product.getPrice());
    dto.setStock(product.getStock());
    dto.setCreatedAt(product.getCreatedAt());
    dto.setModifiedAt(product.getUpdatedAt());
    dto.setSellerUsername(sellerUsername); // pass this from UserService
    return dto;
  }
  public List<ProductDto> getProductsForCurrentSeller() {
    // 1. Get username from Security Context
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String username = authentication.getName();

    // 2. Call UserService via RestTemplate to get seller ID
    UserDTO seller = userClient.getUserByUsername(username); // assumes your UserClient works
    Long sellerId = seller.getId();

    // 3. Fetch products by sellerId
    List<Product> products = productRepo.findBySellerId(sellerId);

    // 4. Convert to DTOs
    return products.stream()
      .map(product -> toDto(product, username))
      .collect(Collectors.toList());
  }
  public List<Product> getProductsByCurrentSeller() {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    String username = auth.getName();
	    UserDTO user = userClient.getUserByUsername(username);
	    return productRepo.findBySellerId(user.getId());
	}
  
  public List<Product> getProductsBySellerId() {
	    return getProductsByCurrentSeller();
	}

}