package com.diep.libraryManagement.handler;

import com.diep.libraryManagement.data.DataService;
import com.diep.libraryManagement.pojo.Borrow;
import com.diep.libraryManagement.router.BorrowAction;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BorrowsHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(BorrowsHandler.class);
  private final DataService dataService = DataService.getDataService();
  @Override
  public void handle(final RoutingContext context) {
    HttpServerRequest request = context.request();
    var json = context.getBodyAsJson();
    var borrowAction = json.mapTo(BorrowAction.class);

    int userId = borrowAction.getUserId();
    int bookId = borrowAction.getBookId();
    String action = borrowAction.getAction();

    try {
      Borrow borrow = takeAction(action, userId, bookId);
      JsonObject response = JsonObject.mapFrom(borrow);
      LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
      context.response()
        .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
        .end(response.toBuffer());
    } catch (RestHandlingException e) {
        LOG.info("Path {} responds with {}", context.normalizedPath(), e.getMessage());
        context.response()
          .end(e.getMessage());
    }
  }

  private Borrow takeAction(String action, int userId, int bookId) throws RestHandlingException {
    switch (action) {
      case "borrow":
        return borrowBook(userId, bookId);
      case "extend":
        return extendBook(userId, bookId);
      case "return":
        return returnBook(userId, bookId);
      default:
        throw new RestHandlingException("Not handling action " + action);
    }
  }

  private Borrow borrowBook(int userId, int bookId) throws RestHandlingException {
    return toBorrowPojo(dataService.borrowBook(userId, bookId));
  }

  private Borrow extendBook(int userId, int bookId) throws RestHandlingException {
    return toBorrowPojo(dataService.extendBook(userId, bookId));
  }

  private Borrow returnBook(int userId, int bookId) throws RestHandlingException {
    return toBorrowPojo(dataService.returnBook(userId, bookId));
  }

  public Borrow toBorrowPojo(com.diep.libraryManagement.data.model.Borrow dbBorrow) {
    if (dbBorrow == null) {
      return null;
    }
    return new Borrow(dbBorrow.getUser().getId(), dbBorrow.getBook().getId(),
      dbBorrow.getBorrowedDate(), dbBorrow.getExpiredDate(), dbBorrow.getReturnedDate());
  }
}

