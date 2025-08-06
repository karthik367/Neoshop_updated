package com.kr.neoshop.admin_service.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
public class SellerDto {
    private Long sellerId;
    private String sellerUsername;
    private Set<ProductDto> products;
	public Long getSellerId() {
		return sellerId;
	}
	public void setSellerId(Long sellerId) {
		this.sellerId = sellerId;
	}
	public String getSellerUsername() {
		return sellerUsername;
	}
	public void setSellerUsername(String sellerUsername) {
		this.sellerUsername = sellerUsername;
	}
	public Set<ProductDto> getProducts() {
		return products;
	}
	public void setProducts(Set<ProductDto> products) {
		this.products = products;
	}
	public SellerDto(Long id, String username, Set<ProductDto> products) {
	    this.sellerId = id;
	    this.sellerUsername = username;
	    this.products = products;
	}

}
