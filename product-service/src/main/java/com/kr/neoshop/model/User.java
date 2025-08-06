package com.kr.neoshop.model;

import jakarta.persistence.Id;

public class User {



  @Id

  private Long id;

  public void setSeller(Long id) {

    this.id = id;

  }
}