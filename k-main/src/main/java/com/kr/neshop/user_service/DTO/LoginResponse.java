package com.kr.neshop.user_service.DTO;

public class LoginResponse {
    private String token;

    public LoginResponse(String token) {
        this.token = token;
    }
    // getter and setter

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
