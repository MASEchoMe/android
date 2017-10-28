package mas.echome;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by Alex on 10/27/2017.
 * Defines a household.
 */

public class Household {
    private ArrayList<Person> members;

    public Household() {
        members = new ArrayList<>();
    }

    public void addToHousehold(Person p) {
        members.add(p);
    }

    public ArrayList<Person> getMembers() {
        return members;
    }

    public ArrayList<String> householdNames() {
        ArrayList<String> names = new ArrayList<>();

        for (Person p : members) {
            names.add(p.getName());
        }

        return names;
    }

    public int householdSize() {
        return members.size();
    }

    public Person getPerson(int index) {
        return members.get(index);
    }
}
