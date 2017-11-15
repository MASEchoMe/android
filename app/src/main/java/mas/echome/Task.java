package mas.echome;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Alex on 10/14/2017.
 */

public class Task {
    private String description;
    private Person sender;
    private Date date;
    private int numTasks;
    private String id;

    public Task(Person sender, String description) {
        this.sender = sender;
        this.description = description;

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        this.date = date;
    }

    public Task(Person sender, String description, String dateStr, String id) {
        this.sender = sender;
        this.description = description;
        this.id = id;

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            Date date = dateFormat.parse(dateStr.substring(0, dateStr.length() - 5));
            this.date = date;
        } catch (Exception e) {
            this.date = new Date();
            e.printStackTrace();
        }
    }

    public Person getSender() {
        return sender;
    }

    public String getSenderName() {
        return sender.getName();
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public int getNumTasks() {
        return numTasks;
    }

    public String getId() {
        return id;
    }
}
