
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class Election {

    private static final int MIN_NUMBER_OF_CANDIDATES = 2;
    private static final int MIN_NUMBER_OF_VOTERS = 2;
    private static final int MIN_POLARITY_LEVEL = 0;
    private static final int MAX_POLARITY_LEVEL = 10;
    private static final int MIN_NUMBER_OF_SAMPLES = 1;
    private static int numberOfCandidates;
    private static int numberOfVoters;
    private static Candidate[] candidates;
    private static Voter[] voters;
    private static int polarityLevel;
    private static int numberOfSamples;
    private static List<Double> records = new ArrayList();
    private static Map<List<Candidate>, Integer> aggregatePreferences;
    private static int pluralityVsCondorcet = 0;
    private static int bordaCountVsCondorcet = 0;
    private static final Scanner KEYBOARD = new Scanner(System.in);

    public static void main(String[] args) {
        getUserInputs();
        runElection();
        overallSummary();
    }

    private static void getUserInputs() {
        getNumberOfCandidates();
        getNumberOfVoters();
        getLevelOfPolarity();

 

        boolean promptUser = determinePorbability(polarityLevel);
        if (promptUser) {

           int id =  getIdOfPolarizingCandidate();
            candidates[id - 1].setPolarized();
        }
        getNumberOfSamples();

    }

    private static void getNumberOfCandidates() {
       
        boolean valid;
        do {
            valid = true;
            System.out.println("Enter number of candidates");
            try {
                numberOfCandidates = KEYBOARD.nextInt();
                if (numberOfCandidates < MIN_NUMBER_OF_CANDIDATES) {
                    throw new Exception();
                }
                createCandidates();
            } catch (InputMismatchException e) {
                System.out.println("Non-numeric value entered for number of candidates");
                KEYBOARD.nextLine();
                valid = false;
            } catch (Exception e) {
                System.out.println("Invalid value entered for number of candidates, must be at least " + MIN_NUMBER_OF_CANDIDATES);
                valid = false;
            }

        } while (!valid);

    }

    private static void getNumberOfVoters() {
        
        boolean valid;
        do {
            valid = true;
            try {
                System.out.println("Enter number of voters");
                numberOfVoters = KEYBOARD.nextInt();
                if (numberOfVoters < MIN_NUMBER_OF_VOTERS) {
                    throw new Exception();
                }

            } catch (InputMismatchException e) {
                System.out.println("Non-numeric value entered for number of voters");
                KEYBOARD.nextLine();
                valid = false;
            } catch (Exception e) {
                System.out.println("Invalid value entered for number of candidates, must be at least " + MIN_NUMBER_OF_VOTERS);
                KEYBOARD.nextLine();
                valid = false;

            }
        } while (!valid);
    }

    private static void getLevelOfPolarity() {
        
        boolean valid;
        do {
            valid = true;
            try {
                System.out.println("Level of polarity? (0-10)");
                polarityLevel = KEYBOARD.nextInt();
                if (polarityLevel < MIN_POLARITY_LEVEL || polarityLevel > MAX_POLARITY_LEVEL) {
                    throw new Exception();
                }
            } catch (InputMismatchException e) {
                System.out.println("Non-numeric value entered for polarity");
                KEYBOARD.nextLine();
                valid = false;
            } catch (Exception e) {
                System.out.println("Invalid value entered for level of polarity, must be between " + MIN_POLARITY_LEVEL + " and " + MAX_POLARITY_LEVEL);
                KEYBOARD.nextLine();
                valid = false;

            }
        } while (!valid);
    }

    private static int getIdOfPolarizingCandidate() {
        
        boolean valid;
        int id=0;
        do {
            valid = true;
            try {
                System.out.println("Id of Polarizing candidate?");
                id = KEYBOARD.nextInt();
                if (id < 1 || id > numberOfCandidates) {
                    throw new Exception();

                }
            } catch (InputMismatchException e) {
                System.out.println("Non-numeric value entered for Id of polarizing candidate");
                KEYBOARD.nextLine();
                valid = false;
            } catch (Exception e) {
                System.out.println("Invalid value entered for Id of polarizing candidate, must be between 1 and " + numberOfCandidates);
                KEYBOARD.nextLine();
                valid = false;

            }
        } while (!valid);
     return id;
    }

    private static void getNumberOfSamples() {
        boolean valid;
        do {
            valid = true;
            try {
                System.out.println("Number of Samples?");
                numberOfSamples = KEYBOARD.nextInt();
                if (numberOfSamples < MIN_NUMBER_OF_SAMPLES) {
                    throw new Exception();
                }
            } catch (InputMismatchException e) {
                System.out.println("Non-numeric value entered for number of samples");
                KEYBOARD.nextLine();
                valid = false;
            } catch (Exception e) {
                System.out.println("Invalid value entered for number of samples, must be at least " + MIN_NUMBER_OF_SAMPLES);
                KEYBOARD.nextLine();
                valid = false;

            }
        } while (!valid);
    }

    private static void runElection() {

        for (int sample = 1; sample <= numberOfSamples; sample++) {
            createVoters();

            assignPreferences();

            setPreferencesStrength(polarityLevel);

            showPreferences();
            stats();

            Majority plurality = new Majority(voters, candidates);
            BordaCount borda = new BordaCount(voters, candidates);
            System.out.println("");
            System.out.println("winner by majority takes all is " + plurality.getWinner());
            double satisfaction1;
            double satisfaction2;
            if (!plurality.getWinner().equals("tied")) {
                satisfaction1 = showSatisfaction(plurality.getWinnerId());

                System.out.println("");
                System.out.print("winner by Borda counts is " + borda.getWinner());
                if (!borda.getWinner().equals("tied")) {
                    System.out.printf("%s%.2f\n", ", with an average score of ", borda.getWinnerAverageScore());
                    System.out.println("");
                    satisfaction2 = showSatisfaction(borda.getWinnerId());
                    if (plurality.getWinnerId() != borda.getWinnerId()) {
                        records.add(satisfaction2 - satisfaction1);
                    }
                }
            }
            Condorcet cond = new Condorcet(voters, candidates, aggregatePreferences);
            System.out.println("condorcet winner was: " + cond.getWinner());
            if (cond.getWinner().equals(borda.getWinner())) {
                bordaCountVsCondorcet++;

            }
            if (cond.getWinner().equals(plurality.getWinner())) {
                pluralityVsCondorcet++;

            }
            System.out.println("=================================================================================================");
        }
    }

    private static void assignPreferences() {
        List<Candidate> list = Arrays.asList(Arrays.copyOf(candidates, numberOfCandidates));
        for (int i = 0; i < numberOfVoters; i++) {

            Collections.shuffle(list);
            voters[i].setPreferedCandidates(list.toArray(new Candidate[numberOfCandidates]));

        }

    }

    private static void createCandidates() {
        KEYBOARD.nextLine();
        candidates = new Candidate[numberOfCandidates];
        for (int i = 0; i < numberOfCandidates; i++) {
            System.out.println("name of candidate " + (i + 1));
            String name = KEYBOARD.nextLine();
            candidates[i] = new Candidate(i + 1, name);

        }

    }

    private static void createVoters() {
        voters = new Voter[numberOfVoters];

        for (int i = 0; i < numberOfVoters; i++) {
            voters[i] = new Voter(i + 1);
        }

    }

    private static void setPreferencesStrength(int polarityLevel) {
        try {
            if (polarityLevel < 0 || polarityLevel > 10) {
                throw new IllegalArgumentException();
            }
        } catch (IllegalArgumentException e) {
            do {
                System.out.println("Enter a valid level polarity (0-10)");
                
                polarityLevel = KEYBOARD.nextInt();
            } while (polarityLevel < 0 || polarityLevel > 10);
        }

        switch (polarityLevel) {
            case 0: {
                for (int i = 0; i < numberOfVoters; i++) {
                    voters[i].setStrengthOfPreferences(0);
                }
                break;
            }
            case 1: {
                int margin = (int) Math.ceil(0.1 * numberOfVoters);

                for (int i = 0; i < margin; i++) {
                    voters[i].setStrengthOfPreferences(10);
                }
                for (int i = margin; i < numberOfVoters; i++) {
                    voters[i].setStrengthOfPreferences(0);
                }
                break;

            }
            case 2: {
                int margin = (int) Math.ceil(0.2 * numberOfVoters);

                for (int i = 0; i < margin; i++) {
                    voters[i].setStrengthOfPreferences(10);
                }
                for (int i = margin; i < numberOfVoters; i++) {
                    voters[i].setStrengthOfPreferences(0);
                }
                break;

            }
            case 3: {
                int margin = (int) Math.ceil(0.3 * numberOfVoters);

                for (int i = 0; i < margin; i++) {
                    voters[i].setStrengthOfPreferences(10);
                }
                for (int i = margin; i < numberOfVoters; i++) {
                    voters[i].setStrengthOfPreferences(0);
                }
                break;

            }
            case 4: {
                int margin = (int) Math.ceil(0.4 * numberOfVoters);

                for (int i = 0; i < margin; i++) {
                    voters[i].setStrengthOfPreferences(10);
                }
                for (int i = margin; i < numberOfVoters; i++) {
                    voters[i].setStrengthOfPreferences(0);
                }
                break;

            }
            case 5: {
                int margin = (int) Math.ceil(0.5 * numberOfVoters);

                for (int i = 0; i < margin; i++) {
                    voters[i].setStrengthOfPreferences(10);
                }
                for (int i = margin; i < numberOfVoters; i++) {
                    voters[i].setStrengthOfPreferences(0);
                }
                break;

            }
            case 6: {
                int margin = (int) Math.ceil(0.6 * numberOfVoters);

                for (int i = 0; i < margin; i++) {
                    voters[i].setStrengthOfPreferences(10);
                }
                for (int i = margin; i < numberOfVoters; i++) {
                    voters[i].setStrengthOfPreferences(0);
                }
                break;

            }
            case 7: {
                int margin = (int) Math.ceil(0.7 * numberOfVoters);

                for (int i = 0; i < margin; i++) {
                    voters[i].setStrengthOfPreferences(10);
                }
                for (int i = margin; i < numberOfVoters; i++) {
                    voters[i].setStrengthOfPreferences(0);
                }
                break;

            }
            case 8: {
                int margin = (int) Math.ceil(0.8 * numberOfVoters);

                for (int i = 0; i < margin; i++) {
                    voters[i].setStrengthOfPreferences(10);
                }
                for (int i = margin; i < numberOfVoters; i++) {
                    voters[i].setStrengthOfPreferences(0);
                }
                break;

            }
            case 9: {
                int margin = (int) Math.ceil(0.9 * numberOfVoters);

                for (int i = 0; i < margin; i++) {
                    voters[i].setStrengthOfPreferences(10);
                }
                for (int i = margin; i < numberOfVoters; i++) {
                    voters[i].setStrengthOfPreferences(0);
                }
                break;

            }
            case 10: {

                for (int i = 0; i < numberOfVoters; i++) {
                    voters[i].setStrengthOfPreferences(10);
                }
                break;

            }

        }

    }

    private static void showPreferences() {
        List<List<Candidate>> generalPreferences = new ArrayList();
        for (Voter x : voters) {
            System.out.println("voter " + x.getId());
            generalPreferences.add(Arrays.asList(x.getPreferedCandidates()));

            for (int i = 0; i < numberOfCandidates; i++) {
                if (i != numberOfCandidates - 1) {

                    System.out.print(x.getPreferedCandidates()[i] + ",");
                } else {
                    System.out.print(x.getPreferedCandidates()[i]);
                }
            }
            System.out.println("");
            System.out.println("strengths");

            List<Integer> strengths = new ArrayList();

            strengths.addAll(x.getStrengthOfPreferences().values());
            strengths.sort((a, b) -> -a.compareTo(b));
            System.out.println(strengths);

            System.out.println("");
            System.out.println("");
        }
        Map<List<Candidate>, Integer> generalPreferences2 = new HashMap();
        List<Candidate> current = new ArrayList();
        int count = 0;

        for (int i = 0; i < generalPreferences.size(); i++) {
            if (!generalPreferences2.containsKey(generalPreferences.get(i))) {
                generalPreferences2.put(generalPreferences.get(i), 1);

            } else {
                generalPreferences2.put(generalPreferences.get(i), generalPreferences2.get(generalPreferences.get(i)) + 1);

            }

        }
        aggregatePreferences = generalPreferences2;
        System.out.println("AGGREGATE PREFERENCES");
        for (Map.Entry<List<Candidate>, Integer> entry : generalPreferences2.entrySet()) {
            System.out.printf("%.2f%s", 100 * (double) entry.getValue() / voters.length, " % of voters:");
            System.out.println("");
            for (Candidate candidate : entry.getKey()) {
                System.out.print(candidate.getName() + ",");

            }
            System.out.println("");
        }
        System.out.println("");
    }

    private static void stats() {

        int[][] positions = new int[numberOfCandidates][numberOfCandidates];

        for (Voter v : voters) {
            for (int i = 0; i < v.getPreferedCandidates().length; i++) {
                positions[v.getPreferedCandidates()[i].getId() - 1][i]++;
            }
        }

        for (int x = 0; x < positions.length; x++) {
            System.out.println("for candidate " + candidates[x].getName());
            for (int a = 0; a < positions[x].length; a++) {
                System.out.printf("%s%d%s%d%s%.2f%s", "appeared ", positions[x][a], " times in position ", (a + 1), ": ",
                        (100 * (double) positions[x][a] / voters.length), "%");
                System.out.println("");
            }

        }
    }

    private static double showSatisfaction(int winnerId) {
        double sum = 0;
        for (int i = 0; i < numberOfVoters; i++) {
            sum += voters[i].getStrengthOfPreference(winnerId);
            System.out.println("Strength of preferences :" + voters[i].getStrengthOfPreferences()
                    + " Score=" + voters[i].getStrengthOfPreference(winnerId));

        }
        System.out.println("total Score= " + sum);
        System.out.println("");
        double satisfaction = sum / numberOfVoters;
        System.out.printf("%s%.2f%s", "Average Voter Satisfaction is ", satisfaction, "/10");
        System.out.println("");
        return satisfaction;
    }

    private static void overallSummary() {
        System.out.println("-------------------------------------------------------------------");
        if (records.isEmpty()) {
            System.out.println("Both methods produced the same winner in all " + numberOfSamples + " sample(s)");
        } else {
            double sum = 0;
            for (double difference : records) {
                sum += difference;
            }
            System.out.println("");
            System.out.println("In " + records.size() + " out of " + numberOfSamples + " samples, Borda Counts selected a different winner "
                    + "than Plurality.");
            System.out.printf("%s%.2f", "The average improvement in voter satisfaction was ", (sum / records.size()));
            System.out.println("");
        }
        System.out.println("Plurality picked the condorcet winner, " + pluralityVsCondorcet + " time(s).");
        System.out.println("Borda count picked the condorcet winner, " + bordaCountVsCondorcet + " time(s).");
    }

    private static boolean determinePorbability(int polarityLevel) {
        List<String> probabilityList;
        Random rand = new Random();
        switch (polarityLevel) {

            case 0:
                return false;
            case 1: {
                probabilityList = new ArrayList();
                probabilityList.add("yes");
                for (int i = 0; i < 9; i++) {
                    probabilityList.add("no");
                }
                Collections.shuffle(probabilityList);
                return probabilityList.get(rand.nextInt(10)).equals("yes");
            }
            case 2: {
                probabilityList = new ArrayList();
                probabilityList.add("yes");
                probabilityList.add("yes");
                for (int i = 0; i < 8; i++) {
                    probabilityList.add("no");
                }
                Collections.shuffle(probabilityList);
                return probabilityList.get(rand.nextInt(10)).equals("yes");
            }
            case 3: {
                probabilityList = new ArrayList();
                probabilityList.add("yes");
                probabilityList.add("yes");
                probabilityList.add("yes");
                for (int i = 0; i < 7; i++) {
                    probabilityList.add("no");
                }
                Collections.shuffle(probabilityList);
                return probabilityList.get(rand.nextInt(10)).equals("yes");
            }
            case 4: {
                probabilityList = new ArrayList();
                for (int i = 0; i < 4; i++) {
                    probabilityList.add("yes");
                }

                for (int i = 0; i < 6; i++) {
                    probabilityList.add("no");
                }
                Collections.shuffle(probabilityList);
                return probabilityList.get(rand.nextInt(10)).equals("yes");
            }

            case 5: {
                probabilityList = new ArrayList();
                for (int i = 0; i < 5; i++) {
                    probabilityList.add("yes");
                }

                for (int i = 0; i < 5; i++) {
                    probabilityList.add("no");
                }
                Collections.shuffle(probabilityList);
                return probabilityList.get(rand.nextInt(10)).equals("yes");
            }
            case 6: {
                probabilityList = new ArrayList();
                for (int i = 0; i < 6; i++) {
                    probabilityList.add("yes");
                }

                for (int i = 0; i < 4; i++) {
                    probabilityList.add("no");
                }
                Collections.shuffle(probabilityList);
                return probabilityList.get(rand.nextInt(10)).equals("yes");
            }
            case 7: {
                probabilityList = new ArrayList();
                for (int i = 0; i < 7; i++) {
                    probabilityList.add("yes");
                }

                for (int i = 0; i < 3; i++) {
                    probabilityList.add("no");
                }
                Collections.shuffle(probabilityList);
                return probabilityList.get(rand.nextInt(10)).equals("yes");
            }
            case 8: {
                probabilityList = new ArrayList();
                for (int i = 0; i < 8; i++) {
                    probabilityList.add("yes");
                }

                for (int i = 0; i < 2; i++) {
                    probabilityList.add("no");
                }
                Collections.shuffle(probabilityList);
                return probabilityList.get(rand.nextInt(10)).equals("yes");
            }
            case 9: {
                probabilityList = new ArrayList();
                for (int i = 0; i < 9; i++) {
                    probabilityList.add("yes");
                }

                for (int i = 0; i < 1; i++) {
                    probabilityList.add("no");
                }
                Collections.shuffle(probabilityList);
                return probabilityList.get(rand.nextInt(10)).equals("yes");
            }

            case 10: {
                return true;
            }
            default:
                return false;

        }
    }

}
