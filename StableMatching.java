
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Class to represent a stable matching solution. It implements the Gale-Shapley algorithm.
 * The class reads the input file, and then calls the performMatching function to find the stable matching.
 * The algorithm starts with each person being matched to their first choice,
 * then looks at each remaining element in turn and tries to find a better match for them.
 * If there is no better match, the current match is maintained.
 *
 * @author Ashish Nagar <anagar@uw.edu> & Karan Chopra <karanc4@uw.edu>
 */
public class StableMatching {
    //Total number of people
    private int n;
    //Number of people that are not matched
    private int unmatchedCount;
    //An array that stores whether each person is matched
    private boolean[] isPersonMatched;
    //An ArrayList of Strings that stores the names of the people who will be adopting pets
    private final List<String> people;
    //An ArrayList of People objects that stores the people and their preferences of pets
    private final List<People> peopleList;
    //An ArrayList of Strings that stores the names of the pets
    private final List<String> pets;
    //A 2D array that stores the preferences of the pets in form of their ranks
    private int[][] petsRanks;
    //An array that stores the index of the person that each pet is matched with
    private int[] petAdoptedBy;

    /**
     * constructor
     * pre: none
     * post: read input file, instance variables are initialized,
     * and performing matching calculation and print the stable matches
     * @param filePath the file path of input data
     */
    public StableMatching(String filePath) {
        people = new ArrayList<>();
        pets = new ArrayList<>();
        peopleList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            n = Integer.parseInt(br.readLine());
            unmatchedCount = n;
            petsRanks = new int[n][n];
            isPersonMatched = new boolean[n];
            petAdoptedBy = new int[n];

            // Reading provided file and initializing data to respective fields
            readInputFile(br);
            // Performing people-optimal stable matching
            performMatching(n);
            // Printing the stable matches
            printMatches(n);

        } catch (IOException e) {
            System.err.println("Error !!! The provided data file or file path is incorrect.");
            e.printStackTrace();
        }
    }

    /**
     * The entry point of application.
     * It provides the filepath and calls parameterized constructor to read inout file
     * pre: none
     * post: stable matching has been performed and the output will be printed on the console
     */
    public static void main(String[] args) {
        new StableMatching("program1data.txt");
    }

    /**
     * Read the given file and store data by invoking different functions
     * pre: variables have been allocated memory
     * post: file data have been stored to preference lists and queue for each person and pet
     */
    private void readInputFile(BufferedReader br) throws IOException {
        // Reading the next n lines from the input file and adding them to the people ArrayList
        addPeople(br);
        // Reading the preferences of the people and storing them in a queue
        addPeoplePreferences(br);
        // Reading the next n lines from the input file and adds them to the pets ArrayList
        addPets(br);
        // Reading the next n lines of the input file and stores the pets' preferences in a 2D array
        addPetsPreferences(br);
    }

    /**
     * function to read and store names of people
     * pre: variables have been allocated memory
     * post: names of people are added to people ArrayList
     */
    private void addPeople(BufferedReader br) throws IOException {
        String line;
        for (int i = 2; i <= n + 1; i++)
            if ((line = br.readLine()) != null) people.add(line);
    }

    /**
     * function to read and store the preferences of the people in a queue
     * pre: people have been allocated memory
     * post: preferences of the people are stored in a preferences queue
     */
    private void addPeoplePreferences(BufferedReader br) throws IOException {
        String line;
        for (int i = n + 2, j = 0; i <= (2 * n) + 1; i++, j++) {
            Queue<Integer> preferences = new LinkedList<>();
            if ((line = br.readLine()) != null) {
                String[] temp = line.trim().split("\\s+");
                for (int k = 0; k < n; k++)
                    preferences.add(Integer.parseInt(temp[k]));

                peopleList.add(new People(people.get(j), preferences));
            }
        }
    }

    /**
     * function to read and store names of pets
     * pre: people and their preferences have been allocated memory
     * post: names of pets are added to pets ArrayList
     */
    private void addPets(BufferedReader br) throws IOException {
        String line;
        for (int i = (2 * n) + 2; i <= (3 * n) + 1; i++) {
            if ((line = br.readLine()) != null) pets.add(line);
        }
    }

    /**
     * function to read and store the ranks of preferences of the pets in a 2D array
     * pre: people, their preferences and pets have been allocated memory
     * post: ranks of preferences of the pets are stored in a 2D array
     */
    private void addPetsPreferences(BufferedReader br) throws IOException {
        String line;
        for (int i = (3 * n) + 2, j = 0; i <= (4 * n) + 1; i++, j++) {
            if ((line = br.readLine()) != null) {
                String[] temp = line.trim().split("\\s+");
                // Storing the inverse preferences of the pets in form of ranks in a 2D array
                for (int k = 0; k < n; k++)
                    petsRanks[j][(Integer.parseInt(temp[k])) - 1] = (k + 1);
            }
        }
    }

    /**
     * function to perform people-optimal stable matching.
     * if already matched pet has a better preference, will replace the current with the new one,
     * otherwise pet will be matched with new proposal
     * pre: people, pets and their preferences have been allocated memory
     * post: stable matching has been performed and the matched data have been allocated memory
     */
    public void performMatching(int n) {
        while (unmatchedCount > 0) {
            for (int personIndex = 0; personIndex < n && unmatchedCount != 0; personIndex++) {
                if (!isPersonMatched[personIndex]) {
                    int personProposesTo = peopleList.get(personIndex).getPrefs().peek();
                    int petIndex = personProposesTo - 1;
                    int proposer = personIndex + 1;
                    int currentMatch = petAdoptedBy[petIndex];

                    if (currentMatch == 0) acceptProposal(personIndex, petIndex);
                    else if (doesPetPreferNewPartner(petIndex, currentMatch, proposer))
                        replaceMatch(personIndex, petIndex, currentMatch, proposer);
                    else rejectProposal(personIndex);
                }
            }
        }
    }

    /**
     * Method to accept fresh proposal as there is no match yet
     * pre: processed file data has been allocated memory
     * post: a new match of person-pet has been stored to the memory
     * @param personIndex the index of the person who proposed to the pet
     * @param petIndex the index of the pet that is being proposed to
     */
    private void acceptProposal(int personIndex, int petIndex) {
        petAdoptedBy[petIndex] = personIndex + 1;
        isPersonMatched[personIndex] = true;
        unmatchedCount--;
    }

    /**
     * To validate if the pet prefers the new partner to the current partner, return true.
     * pre: local variables contributing to matching have been allocated memory
     * post: returns the partner preference
     * @param petIndex the index of the pet that is being considered
     * @param currentPartner the current partner of the pet
     * @param newPartner the index of the new partner
     * @return The difference between the new partner's rank and the current partner's rank.
     */
    private boolean doesPetPreferNewPartner(int petIndex, int currentPartner, int newPartner) {
        return petsRanks[petIndex][newPartner - 1] < petsRanks[petIndex][currentPartner - 1];
    }

    /**
     * Method to reject new proposal as it has lesser preference rank
     * pre: processed file data has been allocated memory
     * post: already matched data is preserved without any change to it
     * @param personIndex the index of the person who will be rejected
     */
    private void rejectProposal(int personIndex) {
        peopleList.get(personIndex).getPrefs().remove();
    }

    /**
     * Method to replace new proposal over already matched
     * pre: processed file data has been allocated memory
     * post: new person with better preference has adopted the pet,
     * and the old person is marked as unmatched
     * @param personIndex The index of the person in the personList ArrayList
     * @param petIndex the index of the pet in the petList
     * @param currentPartner The person that the pet is currently matched with.
     * @param newPartner The person that the pet is matched with
     */
    private void replaceMatch(int personIndex, int petIndex, int currentPartner, int newPartner) {
        petAdoptedBy[petIndex] = newPartner;
        isPersonMatched[(currentPartner - 1)] = false;
        isPersonMatched[personIndex] = true;
        peopleList.get((currentPartner - 1)).getPrefs().remove();
    }

    /**
     * function to print stable matching records
     * pre: performed matching data has been allocated memory
     * post: people-optimal stable matching will be printed to the console
     * @param n number of people
     */
    private void printMatches(int n) {
        for (int i = 0; i < n; i++) {
            try {
                System.out.println(peopleList.get(i).getName() + " / " + pets.get(peopleList.get(i).getPrefs().peek() - 1));
            } catch (NullPointerException e) {
                System.err.println("Error!!! For the given data, no stable matching was found.");
                e.printStackTrace();
            }
        }
    }

}



