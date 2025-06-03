package tasks;

public class Main {
    public static void main(String[] args) {
        // Создаем менеджер задач
        TaskManager manager = Managers.getDefault();

        // Создаем обычные задачи
        Task task1 = new Task("Купить продукты", "Купить нужные продукты для торта", Status.NEW);
        Task task2 = new Task("Прибраться на кухне", "Убрать все лишнее на кухне", Status.NEW);
        manager.createTask(task1);
        manager.createTask(task2);

        // Создаем эпик с подзадачами
        Epic epic1 = new Epic("Приготовить торт", "Подготовка к семейному празднику");
        manager.createEpic(epic1);

        Subtask subtask1 = new Subtask("Купить сливки,яйца,творог", "Составить список и купить",
                Status.NEW, epic1.getId());
        Subtask subtask2 = new Subtask("Пригласить гостей", "Составить список и позвонить",
                Status.NEW, epic1.getId());
        manager.createSubtask(subtask1);
        manager.createSubtask(subtask2);

        // Создаем второй эпик
        Epic epic2 = new Epic("Посмотреть фильм", "Поиск фильма");
        manager.createEpic(epic2);

        Subtask subtask3 = new Subtask("Найти фильм", "Посмотреть афишу", Status.NEW, epic2.getId());
        manager.createSubtask(subtask3);

        // Просматриваем задачи для заполнения истории
        manager.getTask(task1.getId());
        manager.getEpic(epic1.getId());
        manager.getSubtask(subtask1.getId());

        // Выводим все задачи
        printAllTasks(manager);

        // Меняем статусы
        task1.setStatus(Status.IN_PROGRESS);
        manager.updateTask(task1);

        subtask1.setStatus(Status.DONE);
        manager.updateSubtask(subtask1);

        subtask2.setStatus(Status.IN_PROGRESS);
        manager.updateSubtask(subtask2);

        subtask3.setStatus(Status.DONE);
        manager.updateSubtask(subtask3);

        // Выводим обновленные данные
        System.out.println("\n=== После обновления статусов ===");
        printAllTasks(manager);

        // Проверяем подзадачи конкретного эпика
        System.out.println("\n=== Подзадачи эпика 1 ===");
        manager.getEpicSubtasks(epic1.getId()).forEach(System.out::println);

        // Удаляем одну задачу и один эпик
        manager.deleteTask(task1.getId());
        manager.deleteEpic(epic1.getId());

        // Финальный вывод
        System.out.println("\n=== После удаления ===");
        printAllTasks(manager);
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        manager.getTasks().forEach(System.out::println);

        System.out.println("\nЭпики:");
        manager.getEpics().forEach(epic -> {
            System.out.println(epic);
            manager.getEpicSubtasks(epic.getId()).forEach(subtask ->
                    System.out.println("--> " + subtask));
        });

        System.out.println("\nПодзадачи:");
        manager.getSubtasks().forEach(System.out::println);

        System.out.println("\nИстория просмотров:");
        manager.getHistory().forEach(System.out::println);
    }
}