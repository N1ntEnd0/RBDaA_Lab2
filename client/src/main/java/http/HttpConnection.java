package http;

import com.google.gson.Gson;
import dto.Tag;
import dto.Task;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class HttpConnection {
    private static HttpConnection instance;
    URL add_url;
    URL search_url;
    URL last_url;
    URL all_url;

    private HttpConnection(String add_url, String search_url, String last_url, String all_url) throws MalformedURLException {
        this.add_url = new URL(add_url);
        this.search_url = new URL(search_url);
        this.last_url = new URL(last_url);
        this.all_url = new URL(all_url);
    }

    public static HttpConnection getInstance(String add_url, String search_url, String last_url, String all_url) throws MalformedURLException {
        if (instance == null){
            instance = new HttpConnection(add_url, search_url, last_url, all_url);
        }
        return instance;
    }


    public void sendAddCommand(Task task){
        try {
            HttpURLConnection con = (HttpURLConnection)add_url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            Gson gson = new Gson();
            String jsonInputString = gson.toJson(task);
            try(OutputStream os = con.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            if (con.getResponseCode() == 201){
                System.out.println("Задача была успешно добавлена!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Ошибка доступа к серверу");
            System.exit(0);
        }
    }

    public void sendAllCommand() throws MalformedURLException {
        URL url = new URL(all_url.toString());
        HttpURLConnection con = null;
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            con = (HttpURLConnection)url.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setUseCaches(false);
            con.setAllowUserInteraction(false);
            con.connect();
            int status = con.getResponseCode();
            if (status == 200){
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                JSONArray tasks_json = (JSONArray) JSONValue.parse(sb.toString());
                if (tasks_json.size() > 0) {
                    for (Object jsonObject : tasks_json){
                        Task task = taskParser((JSONObject) jsonObject);
                        tasks.add(task);
                    }
                    tasks.forEach(System.out::println);
                } else
                    System.out.println("No such tasks");
            }else{
                System.out.println("Ошибка отправки запроса! Данного параметра не существует");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendSearchCommand(String value) throws MalformedURLException {
        URL url = new URL(search_url.toString() + "?tag=" + value);
        HttpURLConnection con = null;
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            con = (HttpURLConnection)url.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setUseCaches(false);
            con.setAllowUserInteraction(false);
            con.connect();
            int status = con.getResponseCode();
            if (status == 200){
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line+"\n");
                }
                JSONArray tasks_json = (JSONArray) JSONValue.parse(sb.toString());
                if (tasks_json.size() > 0) {
                    for (Object jsonObject : tasks_json){
                        Task task = taskParser((JSONObject) jsonObject);
                        tasks.add(task);
                    }
                    tasks.forEach(System.out::println);
                } else
                    System.out.println("No such tasks");
            }else{
                System.out.println("Ошибка отправки запроса! Данного параметра не существует");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendLastCommand() throws MalformedURLException {
        URL url = new URL(last_url.toString());
        HttpURLConnection con = null;
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json; utf-8");
            con.setUseCaches(false);
            con.setAllowUserInteraction(false);
            con.connect();
            int status = con.getResponseCode();
            if (status == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line + "\n");
                }
                JSONObject task_json = (JSONObject) JSONValue.parse(sb.toString());
                if (task_json == null) {
                    System.out.println("No such tasks");
                } else {
                    Task task = taskParser(task_json);
                    System.out.println(task.toString());
                }
            } else {
                System.out.println("Ошибка отправки запроса! Данного параметра не существует");
            }
        } catch (IOException e) {
            e.printStackTrace();
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
            date.setMonth(Integer.parseInt(date_arr[1]) - 1);
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
}
