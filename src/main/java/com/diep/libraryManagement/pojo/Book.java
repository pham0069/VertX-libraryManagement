package com.diep.libraryManagement.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class Book {
  private final int id;
  private final String code;
  private final String isbn;
}
