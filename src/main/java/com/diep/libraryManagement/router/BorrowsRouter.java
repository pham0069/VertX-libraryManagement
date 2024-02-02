package com.diep.libraryManagement.router;

import com.diep.libraryManagement.handler.BorrowsHandler;
import io.vertx.ext.web.Router;

public class BorrowsRouter {
  public static void attach(Router parent) {
    parent.post("/borrows").handler(new BorrowsHandler());
  }

}
