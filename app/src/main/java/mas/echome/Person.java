package mas.echome;

import java.util.ArrayList;

/**
 * Created by Alex on 10/14/2017.
 */

public class Person {

    private String name;
    private ArrayList<Task> tasks;

    public Person(String name) {
        this.name = name;
        tasks = new ArrayList<>();
    }

    public void giveTask(Task t) {
        tasks.add(t);
    }

    public String getName() {
        return name;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public int getNumTasks() {
        return tasks.size();
    }
}
