package mas.echome;

/**
 * Created by Alex on 10/27/2017.
 * Describes the user of the mobile device.
 */

public class User extends Person {

    private static String name;
    private String userID;
    private String householdID;

    public User(String name, String userID, String householdID) {
        super(name);
        this.userID = userID;
        this.householdID = householdID;
    }

    public static String getUserName() {
        return name;
    }
}
