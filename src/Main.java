import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
import tasks.Task;

public class Main {
    public static void main(String[] args) {
        // Создаем обычные задачи
        Task task1 = new Task("Купить продукты", "Купить нужные продукты для торта", Status.NEW);
        Task task2 = new Task("Прибраться на кухне", "Убрать все лишнее на кухне", Status.NEW);

        // Создаем эпик с подзадачами
        Epic epic1 = new Epic("Приготовить торт", "Подготовка к семейному празднику");
        Subtask subtask1 = epic1.createSubtask("Купить сливки,яйца,творог", "Составить список и купить", Status.NEW);
        Subtask subtask2 = epic1.createSubtask("Пригласить гостей", "Составить список и позвонить", Status.NEW);

        // Создаем второй эпик
        Epic epic2 = new Epic("Посмотреть фильм", "Поиск фильма");
        Subtask subtask3 = epic2.createSubtask("Найти фильм", "Посмотреть афишу", Status.NEW);

        // Выводим все задачи
        System.out.println("=== Все задачи ===");
        Task.getAllTasks().forEach(System.out::println);

        System.out.println("\n=== Все эпики ===");
        Epic.getAllEpics().forEach(System.out::println);

        System.out.println("\n=== Все подзадачи ===");
        Epic.getAllSubtasks().forEach(System.out::println);

        // Меняем статусы
        task1.setStatus(Status.IN_PROGRESS);
        Task.updateTask(task1);

        subtask1.setStatus(Status.DONE);
        Epic.updateSubtask(subtask1);

        subtask2.setStatus(Status.IN_PROGRESS);
        Epic.updateSubtask(subtask2);

        subtask3.setStatus(Status.DONE);
        Epic.updateSubtask(subtask3);

        // Выводим обновленные данные
        System.out.println("\n=== После обновления статусов ===");
        System.out.println("Все задачи:");
        Task.getAllTasks().forEach(System.out::println);

        System.out.println("\nВсе эпики:");
        Epic.getAllEpics().forEach(System.out::println);

        System.out.println("\nВсе подзадачи:");
        Epic.getAllSubtasks().forEach(System.out::println);

        // Проверяем подзадачи конкретного эпика
        System.out.println("\n=== Подзадачи эпика 1 ===");
        epic1.getSubtasks().forEach(System.out::println);

        // Удаляем одну задачу и один эпик
        Task.deleteTaskById(task1.getId());
        Epic.deleteEpicById(epic1.getId());

        // Финальный вывод
        System.out.println("\n=== После удаления ===");
        System.out.println("Все задачи:");
        Task.getAllTasks().forEach(System.out::println);

        System.out.println("\nВсе эпики:");
        Epic.getAllEpics().forEach(System.out::println);

        System.out.println("\nВсе подзадачи:");
        Epic.getAllSubtasks().forEach(System.out::println);
    }
}