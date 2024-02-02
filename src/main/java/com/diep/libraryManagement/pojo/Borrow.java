package com.diep.libraryManagement.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Borrow {
  private int userId;
  private int bookId;
  private Date borrowedDate;
  private Date expiredDate;
  private Date returnedDate;
}
