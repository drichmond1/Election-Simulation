
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author richm
 */
public class BordaCount implements VotingSystem {

    private String winner;
    private int winnerId;
    private double winnerAverageScore;
    private Voter[] voters;
    private Map<String, Integer> candidates;
    private double supportPercentage;

    public BordaCount(Voter[] voters, Candidate[] candidates) {
        this.voters = voters;
        this.candidates = new HashMap();
        for (int i = 0; i < candidates.length; i++) {
            this.candidates.put(candidates[i].getName(), i);
        }

        determineWinner(candidates);

    }

    @Override
    public int getWinnerId() {
        return winnerId;
    }

    @Override
    public Map<String, Integer> getCandidates() {
        return candidates;
    }

    @Override
    public String getWinner() {
        return winner;
    }

    public double getWinnerAverageScore() {
        return winnerAverageScore;
    }

    public void determineWinner(Candidate[] candidates) {

        Map<String, List<Double>> countMap = new HashMap();
        TreeMap<Double, List<String>> averageMap = new TreeMap();
        int[][] positions = new int[candidates.length][candidates.length];

        System.out.println("\n Positions 2-D array is :\n");
  
        boolean tie = false;
        for (Voter v : voters) {
   
            for (int i = 0; i < v.getPreferedCandidates().length; i++) {
                positions[v.getPreferedCandidates()[i].getId() - 1][i]++;
            }

        }
        System.out.println("\nAfter all Voters' rankings recorded: ");
        for (int row = 0; row < positions.length; row++) {
            for (int col = 0; col < positions[row].length; col++) {
                System.out.print(" " + positions[row][col]);
            }
            System.out.println(" ");
        }

        countMap.clear();

        for (int row = 0; row < positions.length; row++) {
            int position = candidates.length - 1;
            countMap.put(candidates[row].getName(), new ArrayList());
            for (int column = 0; column < positions[row].length; column++) {
                List<Double> list = countMap.get(candidates[row].getName());

                list.add((double) positions[row][column] * position);
                countMap.put(candidates[row].getName(), list);
                System.out.print("\nFor candidate: " + candidates[row].getName());
                System.out.println(" the Borda points list becomes " + list);
                position--;

            }

        }
        System.out.println("\nBorda results:");

        for (Map.Entry<String, List<Double>> entry : countMap.entrySet()) {
            String name = entry.getKey();
            double total = 0;
            List<Double> list = entry.getValue();
            for (double x : list) {
                total += x;
            }

            System.out.println(" Candidate " + name + " total Borda = " + total);
            double average = total / voters.length;
            if (!averageMap.containsKey(average)) {
                averageMap.put(average, new ArrayList());
                List<String> names = averageMap.get(average);
                names.add(name);
                averageMap.put(average, names);

            } else {
                List<String> names = averageMap.get(average);
                names.add(name);
                averageMap.put(average, names);

            }

        }

        if (averageMap.get(averageMap.lastKey()).size() > 1) {
            tie = true;
        }

        if (!tie) {
            winner = averageMap.get(averageMap.lastKey()).get(0);
            winnerId = this.candidates.get(winner);
            winnerAverageScore = averageMap.lastKey();
        } else {
            winner = "tied";
        }

    }
}
