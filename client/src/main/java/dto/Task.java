package dto;

import java.util.Date;
import java.util.List;

public class Task {
    private Long id;

    private String title;

    private String description;

    private String deadline;

    private List<Tag> tags;

    public Task(){}

    public Task(
            String title,
            String description,
            String deadline,
            List<Tag> tags
    ) {
        this.tags = tags;
        this.deadline = deadline;
        this.description = description;
        this.title = title;
    }

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String convertDate(Date date) {
        if (date != null)
         return date.getDate() + "." + (date.getMonth() + 1) + "." + date.getYear();
        return "Unknown";
    }

    private String getTagsToString() {
        StringBuilder tagsString = new StringBuilder();
        for (Tag tag : this.tags) {
            tagsString.append(tag.getLabel()).append(",");
        }
        if (tagsString.length() > 0)
            tagsString = new StringBuilder(tagsString.substring(0, tagsString.length() - 1));
        return tagsString.toString();
    }

    @Override
    public String toString() {

        return this.id + " Title: " + this.title + "\n" +
                "Description: " + this.description + "\n" +
                "Deadline: " + this.deadline + "\n" +
                "Tags: " + getTagsToString() + "\n";
    }
}