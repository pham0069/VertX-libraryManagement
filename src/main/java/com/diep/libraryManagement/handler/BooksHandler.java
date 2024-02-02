package com.diep.libraryManagement.handler;

import com.diep.libraryManagement.data.DataService;
import com.diep.libraryManagement.pojo.Book;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BooksHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(BooksHandler.class);
  private final DataService dataService = DataService.getDataService();
  @Override
  public void handle(final RoutingContext context) {
    final JsonArray response = new JsonArray();
    dataService.getBooks().stream().map(this::toBookPojo).forEach(response::add);
    LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
    context.response()
      .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
      .putHeader("my-header", "my-value")
      .end(response.toBuffer());
  }

  public Book toBookPojo(com.diep.libraryManagement.data.model.Book dbBook) {
    return new Book(dbBook.getId(), dbBook.getCode(), dbBook.getIsbn());
  }
}
