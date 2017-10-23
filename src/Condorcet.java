
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Condorcet implements VotingSystem {

    private Map<List<Candidate>, Integer> aggregatePreferences;
    private String winner;
    private int winnerId;
    private Voter[] voters;
    private Map<String, Integer> candidates;
    private double supportPercentage;

    public Condorcet(Voter[] voters, Candidate[] candidates, Map<List<Candidate>, Integer> aggregatePreferences) {
        this.aggregatePreferences = aggregatePreferences;
        this.voters = voters;
        this.candidates = new HashMap();
        for (int i = 0; i < candidates.length; i++) {
            this.candidates.put(candidates[i].getName(), i);
        }
        determineWinner(candidates);
    }

    @Override
    public Map<String, Integer> getCandidates() {
        return candidates;
    }

    @Override
    public String getWinner() {
        if (winner!=null){
        return winner;}
        else 
            return "null";
    }

    @Override
    public int getWinnerId() {
        return winnerId;
    }

    private void determineWinner(Candidate[] candidates) {
      
        for (int candidateA = 0; candidateA < candidates.length; candidateA++) {

            int VotesOfcandidateA;
            int VotesOfcandidateB ;

            boolean condorcet = true;
            for (int CandidateB = 0; CandidateB < candidates.length&&condorcet; CandidateB++) {
                VotesOfcandidateA=0;
                VotesOfcandidateB=0;
                if (candidateA != CandidateB) {

                    for (Map.Entry<List<Candidate>, Integer> entry : aggregatePreferences.entrySet()) {
                        List<Candidate> candList = entry.getKey();
                        boolean found=false;
                        for (int cand = 0; cand < candList.size()&&!found; cand++) {
                            if (candList.get(cand).getId() == candidates[candidateA].getId()) {
                                VotesOfcandidateA += entry.getValue();
                                found=true;
                            } else if (candList.get(cand).getId() == candidates[CandidateB].getId()) {

                                VotesOfcandidateB += entry.getValue();
                                found=true;
                               
                            }
                        }
                    }
                    if (VotesOfcandidateA <= VotesOfcandidateB) {
                        condorcet = false;

                    }

                }

            }
            if (condorcet) {
                winner = candidates[candidateA].getName();
                return;
            }
        }

    }

}
