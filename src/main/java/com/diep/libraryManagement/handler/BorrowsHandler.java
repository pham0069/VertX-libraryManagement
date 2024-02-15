package com.diep.libraryManagement.handler;

import com.diep.libraryManagement.data.DataService;
import com.diep.libraryManagement.data.model.Book;
import com.diep.libraryManagement.pojo.Borrow;
import com.diep.libraryManagement.router.BorrowAction;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerRequest;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;

public class BorrowsHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(BorrowsHandler.class);
  private final DataService dataService = DataService.getDataService();

  private final DataExecutor dataExecutor = DataExecutor.getDataExecutor();
  @Override
  public void handle(final RoutingContext context) {
    HttpServerRequest request = context.request();
    var json = context.getBodyAsJson();
    var borrowAction = json.mapTo(BorrowAction.class);

    int userId = borrowAction.getUserId();
    int bookId = borrowAction.getBookId();
    String action = borrowAction.getAction();

    LOG.info("Received borrow request with user id {}, book id {}, action {}", userId, bookId, action);

    ExecutorService executor = dataExecutor.getExecutor(bookId);
    CompletableFuture<Borrow> f1 = CompletableFuture
      .supplyAsync(() -> {
        try {
          return takeAction(action, userId, bookId);
        } catch (RestHandlingException e) {
          throw new CompletionException(e.getMessage(), e);
        }
      }, executor);

    CompletableFuture<Future<Void>> f2 = f1
      .handle((result, exception) -> {
        if (exception == null) {
          JsonObject response = JsonObject.mapFrom(result);
          LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
          return context.response()
            .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
            .end(response.toBuffer());
        } else {
          LOG.info("Path {} responds with {}", context.normalizedPath(), exception.getMessage());
          return context.response()
            .end(exception.getMessage());
        }
      });

    executor.submit(() -> f2.get());
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

