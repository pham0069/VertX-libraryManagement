package com.diep.libraryManagement.router;

import com.diep.libraryManagement.handler.BooksHandler;
import com.diep.libraryManagement.handler.UsersHandler;
import io.vertx.ext.web.Router;

public class UsersRouter {

  public static void attach(Router parent) {
    parent.get("/users").handler(new UsersHandler());
  }

}

