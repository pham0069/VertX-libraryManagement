package com.diep.libraryManagement.handler;

import com.diep.libraryManagement.pojo.Book;
import com.diep.libraryManagement.pojo.Borrow;
import com.diep.libraryManagement.pojo.User;

public class DataTransformer {
  public Book toBookPojo(com.diep.libraryManagement.data.model.Book dbBook) {
    return new Book(dbBook.getId(), dbBook.getCode(), dbBook.getIsbn());
  }

  public User toUserPojo(com.diep.libraryManagement.data.model.User dbUser) {
    return new User(dbUser.getId(), dbUser.getAccount(), dbUser.getName());
  }

  public Borrow toBorrowPojo(com.diep.libraryManagement.data.model.Borrow dbBorrow) {
    return new Borrow(dbBorrow.getUser().getId(), dbBorrow.getBook().getId(),
      dbBorrow.getBorrowedDate(), dbBorrow.getExpiredDate(), dbBorrow.getReturnedDate());
  }


}
