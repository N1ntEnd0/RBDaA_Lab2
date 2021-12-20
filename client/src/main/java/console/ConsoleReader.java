package console;

import dto.Tag;
import dto.Task;
import dto.User;
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
    private User user = new User();

    private ConsoleReader(){
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")){
            properties = new Properties();
            properties.load(input);
            httpConnection = HttpConnection.getInstance(
                    (String) properties.get("add-url"),
                    (String) properties.get("search-url"),
                    (String) properties.get("last-url"),
                    (String) properties.get("all-url"),
                    (String) properties.get("sign-in-url"),
                    (String) properties.get("sign-up-url")
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
                        httpConnection.sendSearchCommand(tag, user);
                        break;
                    }
                    case "3": {
                        httpConnection.sendLastCommand(user);
                        break;
                    }
                    case "4": {
                        httpConnection.sendAllCommand(user);
                        break;
                    }
                    case "5": {
                        System.exit(0);
                        break;
                    }

                }
            }catch (CommandException | MalformedURLException e){
                System.err.println(e.getMessage());
            }
        }
    }

    private Task createTask() throws CommandException {
        System.out.println("New task");
        Task task = new Task();
        System.out.println("Title:");
        String title = scanner.nextLine().trim();
        task.setTitle(title);
        System.out.println("Description:");
        String description = scanner.nextLine().trim();
        task.setDescription(description);
        System.out.println("Deadline:");
        String[] deadline = scanner.nextLine().trim().split("\\.");
        if (deadline.length != 3)
            throw  new CommandException("Invalid type");
        Date date = new Date();
        date.setDate(Integer.parseInt(deadline[0]));
        date.setMonth(Integer.parseInt(deadline[1]) - 1);
        date.setYear(Integer.parseInt(deadline[2]));
        task.setDeadline(
                String.format("%04d", date.getYear()) + "-" +
                        String.format("%02d", (date.getMonth() )) + "-" +
                        String.format("%02d", date.getDate()));
        System.out.println("Tags (finish on empty line):");
        int i = 0;
        List<Tag> tagList = new ArrayList<>();
        while (true) {
            i++;
            System.out.println(i + ":");
            String tagString = scanner.nextLine().trim();
            if (Objects.equals(tagString, "")) break;
            Tag tag = new Tag();
            tag.setLabel(tagString);
            tagList.add(tag);
        }
        task.setTags(tagList);
        task.setUser(user);
        return task;
    }

    private String info(){
        return properties.getProperty("help-message");
    }

    private String authInfo() {
        return properties.getProperty("auth-message");
    }


    public static ConsoleReader getInstance(){
        if (instance == null){
            instance = new ConsoleReader();
        }
        return instance;
    }

    public void authorization() {
        System.out.println("Enter the number of action and press [Enter]. Then follow instructions.");
        while (true) {
            System.out.println(this.authInfo());
            String cmd = scanner.nextLine().trim();
            switch (cmd) {
                case "1":
                case "2":
                    break;
                default:
                    System.out.println("Unknown param try again");
                    continue;
            }
            String password;
            System.out.println(properties.getProperty("login"));
            String login = scanner.nextLine().trim();
            if (!login.equals("")) {
                System.out.println(properties.getProperty("password"));
                password = scanner.nextLine().trim();
                if (!password.equals("")) {
                    user.setLogin(login);
                    user.setPassword(password);
                    if (Objects.equals(cmd, "1") && httpConnection.signIn(user)) return;
                    if (Objects.equals(cmd, "2") && httpConnection.signUp(user)) return;
                    else {
                        System.out.println("Invalid data of user");
                    }
                }
            }
        }
    }
}
