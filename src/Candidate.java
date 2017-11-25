/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author richm
 */
public class Candidate {
    private String name; 
    private int id;
    private boolean polarized;
    
    public Candidate(){
    
    }
     public Candidate(int id,String name) {
         this.id=id;
         this.name=name;
    }

    public String getName() {
        return name;
    }
    
    
    

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Candidate{"+"name: "+name + " id=" + id + '}';
    }

    public void setId(int id) {
        this.id = id;
    }

   public void setPolarized(){
   polarized=true;
   }
    public boolean isPolarizing(){
    return polarized;
    }
}
