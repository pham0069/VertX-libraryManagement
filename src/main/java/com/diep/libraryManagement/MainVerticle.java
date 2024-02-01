package com.diep.libraryManagement;

import com.diep.libraryManagement.verticle.ApiVerticle;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);

  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    LOGGER.warn("Start verticle ", MainVerticle.class.getName());
    vertx.createHttpServer().requestHandler(req -> {
      req.response()
        .putHeader("content-type", "text/plain")
        .end("Hello from Vert.x!");
    }).listen(8888, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }
}
