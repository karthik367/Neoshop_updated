package com.kr.neoshop.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductDto {
    private Long id;
    private String name;
    private String brand;
    private String category;
    private BigDecimal price;
    private int stock;
    private Long sellerId;
    private String sellerUsername;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // No-arg constructor
    public ProductDto() {}

    // All-args constructor
    public ProductDto(Long id, String name, String category, String brand, BigDecimal price, Integer stock,Long sellerId, String sellerUsername, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.brand = brand;
        this.price = price;
        this.stock = stock;
        this.sellerUsername = sellerUsername;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.sellerId=sellerId;
    }


    // Getters and setters for all fields
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int quantity) { this.stock = quantity; }

    public String getSellerUsername() { return sellerUsername; }
    public void setSellerUsername(String sellerUsername) { this.sellerUsername = sellerUsername; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getModifiedAt() { return updatedAt; }
    public void setModifiedAt(LocalDateTime modifiedAt) { this.updatedAt = modifiedAt; }

    public Long getSellerId() {
		return sellerId;
	}

	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	@Override
    public String toString() {
        return "ProductDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", quantity=" + stock +
                ", sellerId=" + sellerId +
                ", sellerUsername='" + sellerUsername + '\'' +
                ", createdAt=" + createdAt +
                ", modifiedAt=" + updatedAt +
                '}';
    }
}
