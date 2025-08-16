package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import managers.InMemoryTaskManager;
import managers.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static String[] args;
    private final HttpServer server;
    private final TaskManager manager;
    private final Gson gson;

    public HttpTaskServer(TaskManager manager) throws IOException {
        this.manager = manager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();

        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        registerHandlers();
    }

    private void registerHandlers() {
        server.createContext("/tasks", new TasksHandler(manager, gson));
        server.createContext("/subtasks", new SubtasksHandler(manager, gson));
        server.createContext("/epics", new EpicsHandler(manager, gson));
        server.createContext("/history", new HistoryHandler(manager, gson));
        server.createContext("/prioritized", new PrioritizedHandler(manager, gson));
    }

    public void start() {
        server.start();
        System.out.printf("HTTP сервер запущен на порту %d%n", PORT);
    }

    public static void main(String[] args) {
        HttpTaskServer.args = args;
        try {
            TaskManager manager = new InMemoryTaskManager(); // Замените на ваш способ получения менеджера
            HttpTaskServer server = new HttpTaskServer(manager);
            server.start();

            // Для тестирования можно добавить вызов server.stop() через некоторое время
            // Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
        } catch (IOException e) {
            System.err.println("Не удалось запустить сервер: " + e.getMessage());
        }
    }

    public void stop() {
        server.stop(0); // Немедленная остановка
        System.out.println("Сервер остановлен на порту " + PORT);
    }
}