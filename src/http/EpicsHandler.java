package http;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import managers.TaskManager;
import model.Epic;
import java.io.IOException;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler {
    public EpicsHandler(TaskManager manager, Gson gson) {
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
                        List<Epic> epics = manager.getEpics();
                        sendText(exchange, gson.toJson(epics));
                    } else {
                        try {
                            int id = Integer.parseInt(query.substring(3));
                            Epic epic = manager.getEpic(id);
                            if (epic != null) {
                                sendText(exchange, gson.toJson(epic));
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
                    Epic epic = gson.fromJson(body, Epic.class);
                    if (epic == null) {
                        sendBadRequest(exchange);
                        return;
                    }
                    if (epic.getId() == 0) {
                        manager.createEpic(epic);
                        sendCreated(exchange, gson.toJson(epic));
                    } else {
                        manager.updateEpic(epic);
                        sendCreated(exchange, gson.toJson(epic));
                    }
                    break;

                case "DELETE":
                    if (query == null) {
                        manager.deleteAllEpics();
                        sendText(exchange, "Все эпики удалены");
                    } else {
                        try {
                            int id = Integer.parseInt(query.substring(3));
                            manager.deleteEpic(id);
                            sendText(exchange, "Эпик удален");
                        } catch (NumberFormatException e) {
                            sendBadRequest(exchange);
                        }
                    }
                    break;

                default:
                    sendText(exchange, "Метод не поддерживается", 405);
            }
        } catch (Exception e) {
            sendInternalError(exchange);
        }
    }
}