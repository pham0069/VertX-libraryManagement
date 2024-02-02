package com.diep.libraryManagement.client;

import com.diep.libraryManagement.router.BorrowAction;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BorrowClient {

  private static final Logger LOG = LoggerFactory.getLogger(BorrowClient.class);
  public static void main(String[] args) {
    var vertx = Vertx.vertx();
    WebClient client = WebClient.create(vertx);
    client.post(8888, "localhost", "/borrows")
      .sendJsonObject(borrowRequest())
      .onSuccess(response -> LOG.info("Received response: " + response.bodyAsString()))
      .onFailure(err -> System.out.println("Something went wrong: " + err.getMessage()));
  }
  private static JsonObject borrowRequest() {
    return JsonObject.mapFrom(new BorrowAction("borrow", 1, 2));
  }

}
