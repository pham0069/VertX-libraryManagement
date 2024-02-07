package com.diep.libraryManagement.handler;

import com.diep.libraryManagement.data.DataService;
import com.diep.libraryManagement.pojo.User;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UsersHandler implements Handler<RoutingContext> {

  private static final Logger LOG = LoggerFactory.getLogger(UsersHandler.class);
  private final DataService dataService = DataService.getDataService();
  @Override
  public void handle(final RoutingContext context) {
    final JsonArray response = new JsonArray();
    dataService.getUsers().stream().map(this::toUserPojo).forEach(response::add);
    LOG.info("Path {} responds with {}", context.normalizedPath(), response.encode());
    context.response()
      .putHeader(HttpHeaders.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON)
      .putHeader("my-header", "my-value")
      .end(response.toBuffer());
  }

  public User toUserPojo(com.diep.libraryManagement.data.model.User dbUser) {
    return new User(dbUser.getId(), dbUser.getAccount(), dbUser.getName());
  }

}
