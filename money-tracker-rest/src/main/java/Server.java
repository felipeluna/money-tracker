/**
 * Created by felipe on 19/12/17.
 */

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.Router;

public class Server extends AbstractVerticle {

    JDBCClient client;

    @Override
    public void start(Future<Void> future) {

        client = JDBCClient.createShared(vertx, new JsonObject()
                .put("url", "jdbc:hsqldb:mem:test?shutdown=true")
                .put("driver_class", "org.hsqldb.jdbcDriver"));

        setUpInitialData(ready -> {

            Router router = Router.router(vertx);


            router.route("/api/expenses*").handler(routingContext -> client.getConnection(res -> {
                if (res.failed()) {
                    routingContext.fail(res.cause());
                } else {
                    SQLConnection conn = res.result();

                    // save the connection on the context
                    routingContext.put("conn", conn);

                    // we need to return the connection back to the jdbc pool. In order to do that we need to close it, to keep
                    // the remaining code readable one can add a headers end handler to close the connection.
                    routingContext.addHeadersEndHandler(done -> conn.close(v -> {
                    }));

                    routingContext.next();
                }
            })).failureHandler(routingContext -> {
                SQLConnection conn = routingContext.get("conn");
                if (conn != null) {
                    conn.close(v -> {
                    });
                }
            });


            router.route("/").handler(routingContext -> {
                HttpServerResponse response = routingContext.response();
                response
                        .putHeader("content-type", "text/html")
                        .end("<h1>Hello from my first Vert.x 4 application</h1>");
            });

            router.get("/api/expenses").handler(Routes::getAll);
            router.get("/api/expenses/:date").handler(Routes::getAllExpensesByDate);

            int port = Integer.parseInt(System.getenv("http.port"));
            
            vertx.createHttpServer()
                    .requestHandler(router::accept)
                    .listen(port, result -> {
                        if (result.succeeded()) {
                            future.complete();
                        } else {
                            future.fail(result.cause());
                        }
                    });

        });
    }

    private void setUpInitialData(Handler<Void> done) {
        client.getConnection(res -> {
            if (res.failed()) {
                throw new RuntimeException(res.cause());
            }

            final SQLConnection conn = res.result();

            conn.execute("CREATE TABLE IF NOT EXISTS expenses(id INT IDENTITY, date DATE, description VARCHAR(255), amount INT, currency VARCHAR(3))", ddl -> {
                if (ddl.failed()) {
                    throw new RuntimeException(ddl.cause());
                }

                conn.execute("INSERT INTO expenses (date, description, amount, currency) VALUES ('2015-05-20', 'Chocolate', 150, 'SEK')", fixtures -> {
                    if (fixtures.failed()) {
                        throw new RuntimeException(fixtures.cause());
                    }

                    done.handle(null);
                });
            });
        });
    }

}

