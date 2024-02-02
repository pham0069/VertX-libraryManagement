package com.diep.libraryManagement.router;

import com.diep.libraryManagement.handler.BooksHandler;
import io.vertx.ext.web.Router;

public class BooksRouter {

  public static void attach(Router parent) {
    parent.get("/books").handler(new BooksHandler());
  }

}

