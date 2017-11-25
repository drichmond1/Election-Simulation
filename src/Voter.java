
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author richm
 */
public class Voter {

    private int id;
    private Candidate[] preferedCandidates;

    private Map<Integer, Integer> strengthOfPreferences;
    private double satisfaction;

    public Voter(int id) {
        this.id = id;
        strengthOfPreferences = new HashMap();

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Candidate[] getPreferedCandidates() {
        return preferedCandidates;
    }

    public void setPreferedCandidates(Candidate[] preferedCandidates) {
        this.preferedCandidates = preferedCandidates;
    }

    public int getStrengthOfPreference(int id) {
        return strengthOfPreferences.get(id);
    }

    public Map<Integer, Integer> getStrengthOfPreferences() {

        return strengthOfPreferences;
    }

    public void setStrengthOfPreferences(boolean polarized) {

        int[] preferences = new int[preferedCandidates.length];
        List<Integer> list = new ArrayList();
        Random rand = new Random();
        int upperBound;
         
        if (polarized) {
            if (preferedCandidates[0].isPolarizing()) {
                preferences[0] = 10;
                for (int x = 1; x < preferences.length; x++) {
                    preferences[x] = 0;
                }
                //basically if the polirazing candidate is not the first or last on their their list,
                //the code swaps him/her with the last candidate on the list and assign a 0 to him/her
            } else {
                for (int x = 1; x < preferedCandidates.length; x++) {
                    if (preferedCandidates[x].isPolarizing()) {
                        Candidate temp = preferedCandidates[preferedCandidates.length - 1];
                        preferedCandidates[preferedCandidates.length - 1] = preferedCandidates[x];
                        preferedCandidates[x] = temp;
                        preferences[preferences.length - 1] = 0;
                    }
                }

                upperBound = 5;
                int count = 1;
                int sum = 0;

                list.add(0, rand.nextInt(5) + 2);
                upperBound = 10 - list.get(0);
                sum += list.get(0);

                for (int x = 1; x < preferences.length - 1; x++) {
                    list.add(x, rand.nextInt(upperBound + 1));
                    upperBound -= list.get(x);

                    if (x == preferences.length - 2) {
                        list.set(x, 10 - sum);

                    } else {
                        sum += list.get(x);
                    }
                    count++;

                }
                list.sort((x, y) -> -x.compareTo(y));
                for (int x = 0; x < count; x++) {
                    preferences[x] = list.get(x);
                }

            }
        } else {

            int count = 1;
            int sum = 0;
            int margin = preferences.length;

            list.add(0, rand.nextInt(5) + 2);
            upperBound = 10 - list.get(0);
            sum += list.get(0);

            for (int x = 1; x < margin; x++) {
                list.add(x, rand.nextInt(upperBound + 1));
                upperBound -= list.get(x);

                if (x == margin - 1) {
                    list.set(x, 10 - sum);

                } else {
                    sum += list.get(x);
                }
                count++;

            }
            list.sort((x, y) -> -x.compareTo(y));
            for (int x = 0; x < count; x++) {
                preferences[x] = list.get(x);
            }

        }
        for (int i = 0; i < preferedCandidates.length; i++) {
            strengthOfPreferences.put(preferedCandidates[i].getId() - 1, preferences[i]);
        }

    }

    public double getSatisfaction() {
        return satisfaction;
    }

    public void setSatisfaction(double satisfaction) {
        this.satisfaction = satisfaction;
    }

}
