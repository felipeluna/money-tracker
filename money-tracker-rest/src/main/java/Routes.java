import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.RoutingContext;

/**
 * Created by felipe on 19/12/17.
 */
public class Routes {
    public static void getAll(RoutingContext routingContext) {
    	HttpServerResponse response = routingContext.response();

        SQLConnection conn = routingContext.get("conn");

        conn.query("SELECT * FROM expenses", query -> {
            if (query.failed()) {
                sendError(500, response);
            } else {
                if (query.result().getNumRows() == 0) {
                    sendError(404, response);
                } else {
                	 JsonArray arr = new JsonArray();
					 query.result().getRows().forEach(arr::add);
                     routingContext.response().putHeader("content-type", "application/json").end(arr.encode());                }
            }
        });
    }

    public static void getAllExpensesByDate(RoutingContext routingContext) {
        String date = routingContext.request().getParam("date");
        HttpServerResponse response = routingContext.response();
        System.out.println(date);
        SQLConnection conn = routingContext.get("conn");
//        DateTimeFormatter formatter = DateTimeFormatter.BASIC_ISO_DATE;

        conn.queryWithParams("SELECT id, date, description, amount, currency FROM expenses where date = ?", new JsonArray().add(date), query -> {
        if (query.failed()) {
                sendError(500, response);
            } else {
                if (query.result().getNumRows() == 0) {
                    sendError(404, response);
                } else {
                    response.putHeader("content-type", "application/json").end(query.result().getRows().get(0).encode());
                }
            }
        });
    }
    private static void sendError(int statusCode, HttpServerResponse response) {
        response.setStatusCode(statusCode).end();
    }
}
