
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
public class Majority implements VotingSystem {

    private String winner;
    private int winnerId;
    private Voter[] voters;
    private Map<String,Integer> candidates;
    private int[][] positions;
    private double supportPercentage;

    public Majority(Voter[] voters, Candidate[] candidates) {
        this.voters = voters;
        this.candidates=new HashMap();
        for(int i=0;i<candidates.length;i++){
        this.candidates.put(candidates[i].getName(),i);
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
    
    private void determineWinner(Candidate[] candidates) {
        List<String> presentCandidates = new ArrayList();
        for (Candidate can : candidates) {
            presentCandidates.add(can.getName());
        }

        TreeMap<Integer, List<String>> map = new TreeMap();
        int positionStage = 0;
        
        boolean tie;
        positions = new int[candidates.length][candidates.length];

        for (Voter v : voters) {
            for (int i = 0; i < v.getPreferedCandidates().length; i++) {
                positions[v.getPreferedCandidates()[i].getId() - 1][i]++;
            }

        }

        do {
            map.clear();
            tie = false;

            for (int x = 0; x < positions[positionStage].length; x++) {
                if (presentCandidates.contains(candidates[x].getName())) {

                    if (!map.containsKey(positions[x][positionStage])) {
                        map.put(positions[x][positionStage], new ArrayList());
                        map.get(positions[x][positionStage]).add(candidates[x].getName());

                    } else {

                        map.get(positions[x][positionStage]).add(candidates[x].getName());

                    }

                }
            }
           
            if (map.get(map.lastKey()).size() > 1) {
                tie = true;
            }
             
            if (tie) {
                presentCandidates = map.pollLastEntry().getValue();
                positionStage++;
            }

        } while (tie && positionStage < candidates.length);

        if (!map.isEmpty()) {
            winner = map.get(map.lastKey()).get(0);
            winnerId=this.candidates.get(winner);
            
            
            } else {
            winner = "tied";
        }
        

    }

    public int[][] getPositions() {
        return positions;
    }

    @Override
    public String getWinner() {
        return winner;
    }

   

    
  
}
