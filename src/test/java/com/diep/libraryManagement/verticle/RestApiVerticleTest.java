package com.diep.libraryManagement.verticle;

import com.diep.libraryManagement.pojo.Borrow;
import com.diep.libraryManagement.router.BorrowAction;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;


@ExtendWith(VertxExtension.class)
public class RestApiVerticleTest {

  private static final Logger LOG = LoggerFactory.getLogger(RestApiVerticleTest.class);
  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext context) {
    vertx.deployVerticle(new RestApiVerticle(), context.succeeding(id -> context.completeNow()));
  }

  @Test
  void return_all_books(Vertx vertx, VertxTestContext context) throws Throwable {
    var client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(8888));
    client.get("/books")
      .send()
      .onComplete(context.succeeding(response -> {
        var json = response.bodyAsJsonArray();
        LOG.info("Response: {}", json);
        assertEquals("[{\"id\":1,\"code\":\"a1\",\"isbn\":\"1338878921\"},{\"id\":2,\"code\":\"a2\",\"isbn\":\"1338878921\"},{\"id\":3,\"code\":\"b1\",\"isbn\":\"9780439358064\"},{\"id\":4,\"code\":\"b2\",\"isbn\":\"9780439358064\"},{\"id\":5,\"code\":\"b3\",\"isbn\":\"9780439358064\"}]", json.encode());
        assertEquals(200, response.statusCode());
        assertEquals(HttpHeaderValues.APPLICATION_JSON.toString(),
          response.getHeader(HttpHeaders.CONTENT_TYPE.toString()));
        context.completeNow();
      }));
  }

  @Test
  void return_all_users(Vertx vertx, VertxTestContext context) throws Throwable {
    var client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(8888));
    client.get("/users")
      .send()
      .onComplete(context.succeeding(response -> {
        var json = response.bodyAsJsonArray();
        LOG.info("Response: {}", json);
        assertEquals("[{\"id\":1,\"account\":\"1234\",\"name\":\"Anne\"},{\"id\":2,\"account\":\"2345\",\"name\":\"Bob\"},{\"id\":3,\"account\":\"2468\",\"name\":\"Chai\"}]", json.encode());
        assertEquals(200, response.statusCode());
        assertEquals(HttpHeaderValues.APPLICATION_JSON.toString(),
          response.getHeader(HttpHeaders.CONTENT_TYPE.toString()));
        context.completeNow();
      }));
  }

  @Test
  void borrow_book_successful_if_book_is_not_borrowed(Vertx vertx, VertxTestContext context) throws Throwable {
    var client = WebClient.create(vertx, new WebClientOptions().setDefaultPort(8888));
    client.post("/borrows")
      .sendJsonObject(borrowRequest())
      .onComplete(context.succeeding(response -> {
        var json = response.bodyAsJsonObject();
        Borrow borrow = json.mapTo(Borrow.class);
        LOG.info("Response: {}", json);
        assertEquals(borrow.getUserId(), 1);
        assertEquals(borrow.getBookId(), 2);
        assertNull(borrow.getReturnedDate());
        assertEquals(200, response.statusCode());
        assertEquals(HttpHeaderValues.APPLICATION_JSON.toString(),
          response.getHeader(HttpHeaders.CONTENT_TYPE.toString()));
        context.completeNow();
      }));
  }

  private static JsonObject borrowRequest() {
    return JsonObject.mapFrom(new BorrowAction("borrow", 1, 2));
  }

}
