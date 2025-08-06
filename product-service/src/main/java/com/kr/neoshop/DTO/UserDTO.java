package com.kr.neoshop.DTO;
import java.util.Set;

public class UserDTO {

  private Long id;

  private String username;

  private String email;

  private Set<String> roles;

 public Set<String> getRoles() {

 return roles;

 }

 public void setRole(Set<String> roles) {

 this.roles = roles;

 }

 public Long getId() {

 return id;

 }

 public void setId(Long id) {

 this.id = id;

 }

 public String getUsername() {

 return username;

 }

 public void setUsername(String username) {

 this.username = username;

 }

 public String getEmail() {

 return email;

 }

 public void setEmail(String email) {

 this.email = email;

 }

}





