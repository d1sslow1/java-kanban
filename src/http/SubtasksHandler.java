package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.ManagerSaveException;
import managers.TaskManager;
import model.Subtask;
import java.io.IOException;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler {
    public SubtasksHandler(TaskManager manager, Gson gson) {
        super(manager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String query = exchange.getRequestURI().getQuery();

            switch (method) {
                case "GET":
                    if (query == null) {
                        List<Subtask> subtasks = manager.getSubtasks();
                        sendText(exchange, gson.toJson(subtasks));
                    } else {
                        try {
                            int id = Integer.parseInt(query.substring(3));
                            Subtask subtask = manager.getSubtask(id);
                            if (subtask != null) {
                                sendText(exchange, gson.toJson(subtask));
                            } else {
                                sendNotFound(exchange);
                            }
                        } catch (NumberFormatException e) {
                            sendBadRequest(exchange);
                        }
                    }
                    break;
                case "POST":
                    String body = readText(exchange);
                    Subtask subtask = gson.fromJson(body, Subtask.class);
                    if (subtask == null) {
                        sendBadRequest(exchange);
                        return;
                    }
                    try {
                        if (subtask.getId() == 0) {
                            manager.createSubtask(subtask);
                            sendCreated(exchange, gson.toJson(subtask));
                        } else {
                            manager.updateSubtask(subtask);
                            sendCreated(exchange, gson.toJson(subtask));
                        }
                    } catch (ManagerSaveException e) {
                        if (e.getMessage().contains("пересекается")) {
                            sendHasOverlaps(exchange);
                        } else {
                            sendInternalError(exchange);
                        }
                    }
                    break;
                case "DELETE":
                    if (query == null) {
                        manager.deleteAllSubtasks();
                        sendText(exchange, "All subtasks deleted");
                    } else {
                        try {
                            int id = Integer.parseInt(query.substring(3));
                            manager.deleteSubtask(id);
                            sendText(exchange, "Subtask deleted");
                        } catch (NumberFormatException e) {
                            sendBadRequest(exchange);
                        }
                    }
                    break;
                default:
                    sendText(exchange, "Method not allowed", 405);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }
}