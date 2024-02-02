package com.diep.libraryManagement.router;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BorrowAction {
  String action;
  int userId;
  int bookId;
}
