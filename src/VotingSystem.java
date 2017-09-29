
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
public interface VotingSystem {

    Map<String, Integer> getCandidates();
   
    String getWinner();

    int getWinnerId();
    
   
         
} 
