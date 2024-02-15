package com.diep.libraryManagement.handler;

import com.diep.libraryManagement.data.DataService;
import com.diep.libraryManagement.pojo.Book;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class BooksHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(BooksHandler.class);
  private final DataService dataService = DataService.getDataService();
  private final DataExecutor dataExecutor = DataExecutor.getDataExecutor();
  @Override
  public void handle(final RoutingContext context) {
    LOG.info("Received books request");

    ExecutorService executor = dataExecutor.getRandomExecutor();

    CompletableFuture<List<com.diep.libraryManagement.data.model.Book>> f1 = CompletableFuture
      .supplyAsync(() -> dataService.getBooks(), executor);

    CompletableFuture<Future<Void>> f2 = f1
      .handle((result, exception) -> {
        final JsonArray response = new JsonArray();
        result.stream().map(this::toBookPojo).forEach(response::add);
        LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
        return context.response()
          .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
          .end(response.toBuffer());
      });

    executor.submit(() -> f2.get());
  }

  public Book toBookPojo(com.diep.libraryManagement.data.model.Book dbBook) {
    return new Book(dbBook.getId(), dbBook.getCode(), dbBook.getIsbn());
  }
}
