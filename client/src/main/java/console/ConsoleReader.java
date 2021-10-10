package console;

import dto.Tag;
import dto.Task;
import exception.CommandException;
import http.HttpConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.*;

public class ConsoleReader {

    private static ConsoleReader instance;
    private Properties properties;
    private Scanner scanner;
    private static HttpConnection httpConnection;

    private ConsoleReader(){
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")){
            properties = new Properties();
            properties.load(input);
            httpConnection = HttpConnection.getInstance(
                    (String) properties.get("add-url"),
                    (String) properties.get("search-url"),
                    (String) properties.get("last-url"),
                    (String) properties.get("all-url")
            );
            scanner = new Scanner(System.in);
        }catch (IOException e){
            System.err.println("Ошибка старта программы. Отсутствует файл конфигурации");
            System.exit(0);
        }
    }

    public void read(){
        System.out.println("Enter the number of action and press [Enter]. Then follow instructions.");
        while (true) {
            System.out.println(this.info());
            try {
                String cmd = scanner.nextLine();
                switch (cmd) {
                    case "1": {
                        Task newTask = createTask();
                        httpConnection.sendAddCommand(newTask);
                        break;
                    }
                    case "2": {
                        System.out.println("Search tasks by tag:");
                        String tag = scanner.nextLine();
                        if (Objects.equals(tag, ""))
                            throw new CommandException("Invalid value");
                        httpConnection.sendSearchCommand(tag);
                        break;
                    }
                    case "3": {
                        httpConnection.sendLastCommand();
                        break;
                    }
                    case "4": {
                        httpConnection.sendAllCommand();
                        break;
                    }
                    case "5": {
                        System.exit(0);
                        break;
                    }

                }
            }catch (CommandException | MalformedURLException e){
                System.err.println(e.getMessage());
                continue;
            }
        }
    }

    private Task createTask() throws CommandException {
        System.out.println("New task");
        Task task = new Task();
        System.out.println("Title:");
        String title = scanner.nextLine();
        task.setTitle(title);
        System.out.println("Description:");
        String description = scanner.nextLine();
        task.setDescription(description);
        System.out.println("Deadline:");
        String[] deadline = scanner.nextLine().split("\\.");
        if (deadline.length != 3)
            throw  new CommandException("Invalid type");
        Date date = new Date();
        date.setDate(Integer.parseInt(deadline[0]));
        date.setMonth(Integer.parseInt(deadline[1]));
        date.setYear(Integer.parseInt(deadline[2]));
        task.setDeadline(
                String.format("%04d", date.getYear()) + "-" +
                        String.format("%02d", (date.getMonth() + 1)) + "-" +
                        String.format("%02d", date.getDate()));
        System.out.println("Tags (finish on empty line):");
        int i = 0;
        List<Tag> tagList = new ArrayList<>();
        while (true) {
            i++;
            System.out.println(i + ":");
            String tagString = scanner.nextLine();
            if (Objects.equals(tagString, "")) break;
            Tag tag = new Tag();
            tag.setLabel(tagString);
            tagList.add(tag);
        }
        task.setTags(tagList);
        return task;
    }

    private String info(){
        return properties.getProperty("help-message");
    }


    public static ConsoleReader getInstance(){
        if (instance == null){
            instance = new ConsoleReader();
        }
        return instance;
    }







}
