package com.diep.libraryManagement.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
  private final int id;
  private final String account;
  private final String name;
}
