
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author richm
 */
public class InstantRunOff {

    private Map<List<Candidate>, Integer> aggregatePreferences;
    private String winner;
    private int winnerId;
    private Voter[] voters;
    private Map<String, Integer> candidates;

    public InstantRunOff(Voter[] voters, Candidate[] candidates, Map<List<Candidate>, Integer> aggregatePreferences) {
        this.voters = voters;
        this.candidates = new HashMap();
        for (int i = 0; i < candidates.length; i++) {
            this.candidates.put(candidates[i].getName(), i);
            this.aggregatePreferences = aggregatePreferences;
        }

        determineWinner(candidates);

    }

    private void determineWinner(Candidate[] candidates) {
        boolean found = false;
        List<String> presentCandidates = new ArrayList();
        Map<List<String>, Integer> modifiedAggPref = new HashMap();
        Map<String, Integer> candidateVotes = new HashMap();
        for (Candidate can : candidates) {
            presentCandidates.add(can.getName());
        }
        for (Map.Entry<List<Candidate>, Integer> entry : aggregatePreferences.entrySet()) {
            List<String> temp = new ArrayList();
            for (Candidate x : entry.getKey()) {
                temp.add(x.getName());

            }
            modifiedAggPref.put(temp, entry.getValue());
        }

        do {
            candidateVotes.clear();
            for (int x = 0; x < presentCandidates.size(); x++) {
                int totalVotes = 0;
                for (Map.Entry<List<String>, Integer> entry : modifiedAggPref.entrySet()) {

                    if (entry.getKey().get(0).equals(presentCandidates.get(x))) {
                        totalVotes += entry.getValue();

                    }

                }

                if ((double) totalVotes / voters.length > 0.50) {
     
                    winner = presentCandidates.get(x);
                    winnerId=this.candidates.get(winner);

                    found = true;
                    if(presentCandidates.size()==2 && presentCandidates.get(0)==presentCandidates.get(1)){
                    winner="tied";
                    }

                } else {
                    candidateVotes.put(presentCandidates.get(x), totalVotes);
                    if(presentCandidates.size()==2 && candidateVotes.get(presentCandidates.get(0))==candidateVotes.get(presentCandidates.get(1)) ){
                    winner="tied";
                    found=true;
                    }

                }

            }
            int leastVotes = voters.length;
            String leastCand = null;

            for (Map.Entry<String, Integer> entry : candidateVotes.entrySet()) {
                if (entry.getValue() < leastVotes) {
                    leastVotes = entry.getValue();
                    leastCand = entry.getKey();

                }
            }
            final String leastVoted = leastCand;
            presentCandidates.removeIf(x -> x.equals(leastVoted));

            for (Map.Entry<List<String>, Integer> entry : modifiedAggPref.entrySet()) {
                entry.getKey().removeIf(x -> x.equals(leastVoted));
            }

        } while (!found);

    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public int getWinnerId() {
        return winnerId;
    }

    public void setWinnerId(int winnerId) {
        this.winnerId = winnerId;
    }

    public Voter[] getVoters() {
        return voters;
    }

    public void setVoters(Voter[] voters) {
        this.voters = voters;
    }

    public Map<String, Integer> getCandidates() {
        return candidates;
    }

    public void setCandidates(Map<String, Integer> candidates) {
        this.candidates = candidates;
    }

}
