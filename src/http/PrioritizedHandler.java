package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import model.Task;
import java.io.IOException;
import java.util.Set;

public class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendText(exchange, "Method not allowed", 405);
                return;
            }

            Set<Task> prioritizedTasks = manager.getPrioritizedTasks();
            sendText(exchange, gson.toJson(prioritizedTasks));
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }
}