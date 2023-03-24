
import java.util.Queue;

/**
 * A People object represents a person and their preferences.
 * It stores people names in String format and preference to a queue.
 *
 * @author Ashish Nagar <anagar@uw.edu> & Karan Chopra <karanc4@uw.edu>
 */
class People {
    private final String name;
    private final Queue<Integer> prefs;

    /**
     * Instantiates a new People.
     * pre: none
     * post: names and preferences are stored in the memory
     */
    public People(String name, Queue<Integer> preferences) {
        this.name = name;
        this.prefs = preferences;
    }

    /**
     * Gets name.
     * pre: none
     * post: return the name of person
     */
    public String getName() {
        return name;
    }

    /**
     * Gets prefs.
     * pre: none
     * post: returns person preference list
     */
    public Queue<Integer> getPrefs() {
        return prefs;
    }
}