package com.diep.libraryManagement.verticle;

import com.diep.libraryManagement.router.BooksRouter;
import com.diep.libraryManagement.router.BorrowsRouter;
import com.diep.libraryManagement.router.UsersRouter;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestApiVerticle extends AbstractVerticle {

  private static final Logger LOG = LoggerFactory.getLogger(RestApiVerticle.class);

  @Override
  public void start(final Promise<Void> startPromise) throws Exception {
    // One pool for each Rest Api Verticle
    ///final Pool db = DBPools.createPgPool(configuration, vertx);
    // Alternatively use MySQL
    // final Pool db = DBPools.createMySQLPool(configuration, vertx);

    final Router restApi = Router.router(vertx);
    restApi.route()
      .handler(BodyHandler.create())
      .failureHandler(handleFailure());

    BooksRouter.attach(restApi);
    UsersRouter.attach(restApi);
    BorrowsRouter.attach(restApi);

    vertx.createHttpServer()
      .requestHandler(restApi)
      .exceptionHandler(error -> LOG.error("HTTP Server error: ", error))
      .listen(8888, http -> {
        if (http.succeeded()) {
          startPromise.complete();
          LOG.info("HTTP server started!");
        } else {
          startPromise.fail(http.cause());
        }
      });
  }

  private Handler<RoutingContext> handleFailure() {
    return errorContext -> {
      if (errorContext.response().ended()) {
        // Ignore completed response
        return;
      }
      LOG.error("Route Error:", errorContext.failure());
      errorContext.response()
        .setStatusCode(500)
        .end(new JsonObject().put("message", "Something went wrong :(").toBuffer());
    };
  }
}
