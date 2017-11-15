package mas.echome;

import java.util.ArrayList;

/**
 * Created by Alex on 10/14/2017.
 * Defines any person in the household.
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

    public void setTask(ArrayList<Task> arrList) {
        this.tasks =  arrList;
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

    @Override
    public String toString() {
        return getName();
    }
}
