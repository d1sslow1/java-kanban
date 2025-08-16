package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import model.Task;
import java.io.IOException;
import java.util.List;

public class HistoryHandler extends BaseHttpHandler {
    public HistoryHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!"GET".equals(exchange.getRequestMethod())) {
                sendText(exchange, "Метод не поддерживается", 405);
                return;
            }

            List<Task> history = manager.getHistory();
            sendText(exchange, gson.toJson(history));
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }
}