package http;

import com.google.gson.Gson;
import dto.Tag;
import dto.Task;
import dto.User;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HttpConnection {
    private static HttpConnection instance;
    URL add_url;
    URL search_url;
    URL last_url;
    URL all_url;
    URL sign_in_url;
    URL sign_up_url;

    private HttpConnection(
            String add_url,
            String search_url,
            String last_url,
            String all_url,
            String sign_in_url,
            String sign_up_url
    ) throws MalformedURLException {
        this.add_url = new URL(add_url);
        this.search_url = new URL(search_url);
        this.last_url = new URL(last_url);
        this.all_url = new URL(all_url);
        this.sign_in_url = new URL(sign_in_url);
        this.sign_up_url = new URL(sign_up_url);
    }

    public static HttpConnection getInstance(
            String add_url,
            String search_url,
            String last_url,
            String all_url,
            String sign_in_url,
            String sign_up_url
    ) throws MalformedURLException {
        if (instance == null){
            instance = new HttpConnection(
                    add_url,
                    search_url,
                    last_url,
                    all_url,
                    sign_in_url,
                    sign_up_url
                    );
        }
        return instance;
    }

    public boolean signIn(User user) {
        try {
            return getDefaultConnection(sign_in_url, user, false).getResponseCode() == 200;
        } catch (IOException e) {
            handleDefault(e);
            return false;
        }
    }

    public boolean signUp(User user) {
        try {
            return getDefaultConnection(sign_up_url, user, false).getResponseCode() == 201;
        } catch (IOException e) {
            handleDefault(e);
            return false;
        }
    }


    public void sendAddCommand(Task task){
        try {
            if (getDefaultConnection(add_url, task, false).getResponseCode() == 200){
                System.out.println("Задача была успешно добавлена!");
            }
        } catch (IOException e) {
            handleDefault(e);
        }
    }

    public void sendAllCommand(User user) throws MalformedURLException {
        try {
            HttpURLConnection con = getDefaultConnection(all_url, user, true);
            if (con.getResponseCode() == 200){
                printTaskList(new InputStreamReader(con.getInputStream()));
            }else{
                System.out.println("Ошибка отправки запроса! Данного параметра не существует");
            }
        } catch (IOException e) {
            handleDefault(e);
        }
    }

    public void sendSearchCommand(String value, User user) throws MalformedURLException {
        try {
            HttpURLConnection con = getDefaultConnection(new URL(search_url.toString() + "?tag=" + value), user, true);
            if (con.getResponseCode() == 200){
                printTaskList(new InputStreamReader(con.getInputStream()));
            }else{
                System.out.println("Ошибка отправки запроса! Данного параметра не существует");
            }
        } catch (IOException e) {
            handleDefault(e);
        }
    }

    public void sendLastCommand(User user) throws MalformedURLException {
        try {
            HttpURLConnection con = getDefaultConnection(last_url, user, true);
            if (con.getResponseCode() == 200) {
                JSONObject task_json = (JSONObject) JSONValue.parse(this.streamToString(new InputStreamReader(con.getInputStream())));
                if (task_json == null) {
                    System.out.println("No such tasks");
                } else {
                    Task task = taskParser(task_json);
                    System.out.println(task);
                }
            } else {
                System.out.println("Ошибка отправки запроса! Данного параметра не существует");
            }
        } catch (IOException e) {
            handleDefault(e);
        }
    }

    private Task taskParser(JSONObject task_json) {
        Task task = new Task();
        task.setTitle((String) task_json.get("title"));
        task.setDescription((String) task_json.get("description"));
        Date date = new Date();
        String date_string = (String) task_json.get("deadline");
        String[] date_arr = date_string.split("-");
        if (date_arr.length == 3) {
            date.setYear(Integer.parseInt(date_arr[0]));
            date.setMonth(Integer.parseInt(date_arr[1]));
            date.setDate(Integer.parseInt(date_arr[2].split("T")[0]));
            task.setDeadline(date.getDate() + "." + (date.getMonth() + 1) + "." + date.getYear());
        }
        List<Tag> tagList = new ArrayList<>();
        JSONArray array = (JSONArray) task_json.get("tags");
        for (Object jsonObject : array) {
            Tag tag = new Tag();
            tag.setLabel((String) ((JSONObject) jsonObject).get("label"));
            tagList.add(tag);
        }
        task.setTags(tagList);
        task.setId((long) task_json.get("id"));
        return task;
    }

    private void handleDefault(IOException e){
        e.printStackTrace();
        System.err.println("Ошибка доступа к серверу");
        System.exit(0);
    }

    public void printTaskList(InputStreamReader inputStreamReader) throws IOException {
        ArrayList<Task> tasks = new ArrayList<>();
        JSONArray tasks_json = (JSONArray) JSONValue.parse(this.streamToString(inputStreamReader));
        if (tasks_json.size() > 0) {
            for (Object jsonObject : tasks_json){
                Task task = taskParser((JSONObject) jsonObject);
                tasks.add(task);
            }
            tasks.forEach(System.out::println);
        } else
            System.out.println("No such tasks");
    }

    private HttpURLConnection getDefaultConnection(URL url, Object object, boolean withAdditionalParams) throws IOException {
        HttpURLConnection con = (HttpURLConnection)url.openConnection();
        con.setDoOutput(true);
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json; utf-8");
        if (withAdditionalParams) {
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setAllowUserInteraction(false);
        }
        Gson gson = new Gson();
        String jsonInputString = gson.toJson(object);
        try(OutputStream os = con.getOutputStream()) {
            byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        return con;
    }

    public String streamToString(InputStreamReader inputStreamReader) throws IOException {
        BufferedReader br = new BufferedReader(inputStreamReader);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }
}
