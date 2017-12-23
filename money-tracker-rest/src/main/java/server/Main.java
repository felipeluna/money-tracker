package server;

import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by felipe on 19/12/17.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.out.println(System.getenv("http.port"));
        BlockingQueue<AsyncResult<String>> q = new ArrayBlockingQueue<>(1);
        Vertx.vertx().deployVerticle(new Server(), q::offer);
        AsyncResult<String> result = q.take();
        if (result.failed()) {
            throw new RuntimeException(result.cause());
        }
    }
}

