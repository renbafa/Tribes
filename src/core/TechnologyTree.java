package core;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static core.Types.TECHNOLOGY.*;

public class TechnologyTree {

    private boolean[] researched;
    private boolean everythingResearched = false;

    public TechnologyTree(){
        researched = new boolean[Types.TECHNOLOGY.values().length];
//        Arrays.fill(researched, true); //Buff for debug purposes, keep this commented for real games.
    }

    public TechnologyTree(boolean[] researched){
        this.researched = researched;
        checkEverythingResearched();
    }

    public TechnologyTree(JSONObject jTechnologyTree){
        JSONArray jResearched = jTechnologyTree.getJSONArray("researched");
        researched = new boolean[jResearched.length()];
        for (int i=0; i<jResearched.length(); i++){
            researched[i] = jResearched.getBoolean(i);
        }
        everythingResearched = jTechnologyTree.getBoolean("everythingResearched");
    }

    public boolean isResearched(Types.TECHNOLOGY target) {
        if (target != null) {
            return researched[target.ordinal()];
        }
        return false;
    }

    public TechnologyTree copy(){
        return new TechnologyTree(researched);
    }

    public boolean isResearchable(Types.TECHNOLOGY target) {

        Types.TECHNOLOGY requirement = target.getParentTech();

        //it will be not researchable if it's already researched or its requirement is not researched yet.
        if(isResearched(target) || requirement != null && !isResearched(requirement))
            return false;

        return true;

    }

    private void checkEverythingResearched(){
        for (boolean b : researched){
            if(!b){return;}
        }
        everythingResearched = true;
    }

    public boolean isEverythingResearched(){
        return everythingResearched;
    }

    public boolean doResearch(Types.TECHNOLOGY target) {
        if(isResearchable(target)) {
            researched[target.ordinal()] = true;

            //researching leaves of the tree may
            if(target == SHIELDS || target == AQUATISM || target == CHIVALRY || target == CONSTRUCTION ||
               target == MATHEMATICS || target == NAVIGATION || target == SMITHERY ||
               target == SPIRITUALISM || target == TRADE || target == PHILOSOPHY)
            {
                    checkEverythingResearched();
            }
            return true;
        }
        return false;
   }

   public boolean researchAtRandom(Random rnd)
   {
       if(isEverythingResearched()) return false;

       ArrayList<Types.TECHNOLOGY> available = new ArrayList<>();
       for(Types.TECHNOLOGY tech : Types.TECHNOLOGY.values())
       {
           if(!isResearched(tech))
               available.add(tech);
       }

       Types.TECHNOLOGY t = available.get(rnd.nextInt(available.size()));
       return doResearch(t);
   }

    public boolean[] getResearched() {
        return researched;
    }

    public int getNumResearched()
    {
        int count = 0;
        for(Types.TECHNOLOGY tech : Types.TECHNOLOGY.values())
        {
            if(isResearched(tech))
                count++;
        }
        return count;
    }
}