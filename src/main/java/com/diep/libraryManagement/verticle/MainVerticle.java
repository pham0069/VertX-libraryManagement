package com.diep.libraryManagement.verticle;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(MainVerticle.class);

  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    vertx.deployVerticle(new MainVerticle());
  }
  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    LOG.warn("Start verticle ", MainVerticle.class.getName());
    vertx.deployVerticle(new RestApiVerticle());
    startPromise.complete();
  }
}
