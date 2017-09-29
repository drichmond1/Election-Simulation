
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
        return winner;
    }

    @Override
    public int getWinnerId() {
        return winnerId;
    }

    private void determineWinner(Candidate[] candidates) {

        for (int candidateA = 0; candidateA < candidates.length; candidateA++) {

            int VotesOfcandidateA = 0;
            int VotesOfcandidateB = 0;

            boolean condorcet = true;
            for (int CandidateB = 0; CandidateB < candidates.length; CandidateB++) {
                if (candidateA != CandidateB) {

                    for (Map.Entry<List<Candidate>, Integer> entry : aggregatePreferences.entrySet()) {
                        List<Candidate> candList = entry.getKey();
                        for (int cand = 0; cand < candList.size(); cand++) {
                            if (candList.get(cand).getId() == candidates[candidateA].getId()) {
                                VotesOfcandidateA += entry.getValue();
                                break;
                            } else if (candList.get(cand).getId() == candidates[CandidateB].getId()) {

                                VotesOfcandidateB += entry.getValue();
                                break;
                            }
                        }
                    }
                    if (VotesOfcandidateA < VotesOfcandidateB) {
                        condorcet = false;

                    }
                    if (!condorcet) {
                        break;
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
