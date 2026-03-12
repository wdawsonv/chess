package server;

import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import org.eclipse.jetty.server.Authentication;
import service.*;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.delete("/db", new Handler() {
            public void handle(Context context) {
                GameService.clear();
                AuthService.clear();
                UserService.clear();
            }
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
