package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import model.Task;

import java.io.IOException;
import java.util.List;

public class TasksHandler extends BaseHttpHandler {

    public TasksHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    handleGetRequest(exchange);
                    break;
                case "POST":
                    handlePostRequest(exchange);
                    break;
                case "DELETE":
                    handleDeleteRequest(exchange);
                    break;
                default:
                    sendText(exchange, "Метод не поддерживается", 405);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) {
            List<Task> tasks = manager.getTasks();
            sendText(exchange, gson.toJson(tasks));
        } else {
            try {
                int id = Integer.parseInt(query.substring(3));
                Task task = manager.getTask(id);
                if (task != null) {
                    sendText(exchange, gson.toJson(task));
                } else {
                    sendNotFound(exchange);
                }
            } catch (NumberFormatException e) {
                sendBadRequest(exchange);
            }
        }
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        String body = readText(exchange);
        Task task = gson.fromJson(body, Task.class);
        if (task == null) {
            sendBadRequest(exchange);
            return;
        }

        try {
            if (task.getId() == 0) {
                manager.createTask(task);
                sendCreated(exchange, gson.toJson(task));
            } else {
                manager.updateTask(task);
                sendCreated(exchange, gson.toJson(task));
            }
        } catch (Exception e) {
            if (e.getMessage().contains("пересекается")) {
                sendHasOverlaps(exchange);
            } else {
                sendInternalError(exchange);
            }
        }
    }

    private void handleDeleteRequest(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query == null) {
            manager.deleteAllTasks();
            sendText(exchange, "Все задачи удалены");
        } else {
            try {
                int id = Integer.parseInt(query.substring(3));
                manager.deleteTask(id);
                sendText(exchange, "Задача удалена");
            } catch (NumberFormatException e) {
                sendBadRequest(exchange);
            }
        }
    }
}