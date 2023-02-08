import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

public class Solution {
    protected int[] link,remain;
    protected final long penalty=1000000;
    protected final Graph graph;
    protected double cost;
    public static long alterTime=0;
    public Solution(Graph graph){
        this.graph=graph;
        link=new int[graph.getDemandPoints().size()];
        remain=new int[graph.getServicePoints().size()];

        Random random=new Random();
        for(int i=0;i<graph.getDemandPoints().size();i++){
            double tmp=random.nextDouble();
            link[i]=graph.getNearestService()[i][(int)(tmp*tmp*2)];
        }

        cost=0.0;
        for(int i=0;i<remain.length;i++){
            remain[i]=graph.getServicePoints().get(i).getCapacity();
        }
        for(int i=0;i<link.length;i++){
            cost+=graph.getDistance()[i][link[i]]*graph.getDemandPoints().get(i).getPopulation();
            remain[link[i]]-=graph.getDemandPoints().get(i).getPopulation();
        }
        for (int j:remain) {
            if(j<0){
                cost+=penalty*(-j);
            }
        }
    }

    public Solution getCopy(){
        Solution copy=new Solution(graph);
        copy.link=Arrays.copyOf(link,link.length);
        copy.remain=Arrays.copyOf(remain,remain.length);
        copy.cost=cost;
        return copy;
    }

    public void alter(int demand,int service){
        long start=System.currentTimeMillis();
        int originalService=link[demand];
        if(originalService==service){
            return;
        }
        int population=graph.getDemandPoints().get(demand).getPopulation();
        if(remain[originalService]<0){
            cost-=(Math.min(0,remain[originalService]+population)-remain[originalService])*penalty;
        }
        remain[originalService]+=population;
        cost+=population*(graph.getDistance()[demand][service]-graph.getDistance()[demand][originalService]);
        remain[service]-=population;
        if(remain[service]<0){
            cost+=(Math.min(0,remain[service]+population)-remain[service])*penalty;
        }
        link[demand]=service;
        alterTime+=System.currentTimeMillis()-start;
    }

    public Solution search1(){
        Solution ans=getCopy();
        ArrayList<Integer> shuffled=new ArrayList<>();
        for(int i=0;i<link.length;i++){
            shuffled.add(i);
        }
        Collections.shuffle(shuffled);
        double originalCost=cost,lastCost=cost;
        for(Integer i:shuffled){
            int originalJ=ans.link[i];
            boolean notImproved=true;
            for(int j:graph.getNearestService()[i]){
                if(j==originalJ){
                    if(ans.remain[j]<0){
                        continue;
                    }
                    else{
                        break;
                    }
                }
                ans.alter(i,j);
                if(ans.cost<lastCost){
                    notImproved=false;
                    lastCost=ans.getCost();
                    break;
                }
            }
            if(notImproved){
                ans.alter(i,originalJ);
            }
        }
        return ans.getCost()+0.0001<originalCost?ans:null;
    }

    public Solution search2(){
        Solution ans=getCopy();
        ArrayList<Integer> shuffled1=new ArrayList<>(),shuffled2=new ArrayList<>();
        for(int i=0;i<link.length;i++){
            shuffled1.add(i);
            shuffled2.add(i);
        }
        Collections.shuffle(shuffled1);
        Collections.shuffle(shuffled2);
        double originalCost=cost,lastCost=cost;
        for(Integer i1:shuffled1){
            int originalJ1=ans.link[i1];
            boolean notImproved=true;
            for(Integer i2:shuffled2){//i1->i2, i2->?
                if(originalJ1==ans.link[i2]){
                    continue;
                }
                int originalJ2=ans.link[i2];
                ans.alter(i1,originalJ2);
                for(int j:graph.getNearestService()[i2]){
                    if(j==originalJ2){
                        continue;
                    }
                    ans.alter(i2,j);
                    if(ans.getCost()<lastCost){
                        notImproved=false;
                        lastCost=ans.getCost();
                        break;
                    }
                }
                if(notImproved){
                    ans.alter(i2,originalJ2);
                }
                else{
                    break;
                }
            }
            if(notImproved){
                ans.alter(i1,originalJ1);
            }
        }
        return ans.getCost()+0.0001<originalCost?ans:null;
    }

    public Solution reconstruct(){
        Solution ans=getCopy();
        int ruinProbability=5;
        Random random=new Random();
        for(int i=0;i<link.length;i++){
            if(random.nextInt(100)<ruinProbability){
                ans.alter(i,graph.getNearestService()[i][0]);
            }
        }
        return ans;
    }

    public double getCost() {
        return cost;
    }

    public ServiceArea[] getAllServiceAreas(){
        ServiceArea[] ans=new ServiceArea[remain.length];
        for(int i=0;i<link.length;i++){
            if(ans[link[i]]==null){
                ans[link[i]]=new ServiceArea(link[i]);
            }
            ans[link[i]].addDemandPoint(i,graph.getDemandPoints().get(i).getPopulation()*graph.getDistance()[i][link[i]]);
        }
        for(int i=0;i<remain.length;i++){
            if(remain[i]<0){
                ans[i]=null;
            }
        }
        return ans;
    }
}
