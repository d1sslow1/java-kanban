package managers;

import http.HttpTaskServer;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.*;
import static org.junit.jupiter.api.Assertions.*;



class HttpTaskServerTasksTest {
    private HttpTaskServer server;
    private TaskManager manager;
    private HttpClient client;

    @BeforeEach
    void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        server = new HttpTaskServer(manager);
        client = HttpClient.newHttpClient();
        server.start();
    }

    @AfterEach
    void tearDown() {
        server.stop(); // Важно: останавливаем сервер после каждого теста
    }

    @Test
    void testTaskCreation() throws Exception {
        // Тест создания задачи
        String taskJson = """
        {
            "name": "Важная задача",
            "description": "Срочно сделать",
            "status": "NEW"
        }
        """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getTasks().size());
    }

    @Test
    void testServerStopBetweenTests() throws Exception {
        // Этот тест проверяет, что сервер корректно останавливается и запускается снова
        String taskJson = "{\"name\":\"Тест\",\"description\":\"Проверка\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(201, response.statusCode());
    }
}