import java.util.*;

public class ConnectedSolution extends Solution{
    public ConnectedSolution(ConnectedGraph graph){
        super(graph);
        /*
        ArrayList<ServicePoint> servicePoints=graph.getServicePoints();
        Arrays.fill(link, 0);
        for(int i=0;i<remain.length;i++){
            remain[i]=servicePoints.get(i).getCapacity();
        }
        cost=0.0;
        ArrayList<Integer> grow=new ArrayList<>();
        for(int i=0;i<servicePoints.size();i++){
            int idx=graph.getDemandPointIndex(servicePoints.get(i).getId());
            link[idx]=i;
            remain[i]-=graph.getDemandPoints().get(idx).getPopulation();
            cost+=graph.getDistance()[idx][i]*graph.getDemandPoints().get(idx).getPopulation();
            grow.add(idx);
        }
        Random random=new Random();
        while(grow.size()!=0){
            int idx=random.nextInt(grow.size()),npos=grow.get(idx);
            for(Integer neighbor:graph.getNeighbors(npos)){
                if(link[neighbor]!=0){
                    continue;
                }
                link[neighbor]=link[npos];
                remain[link[npos]]-=graph.getDemandPoints().get(neighbor).getPopulation();
                cost+=graph.getDistance()[neighbor][link[npos]]*graph.getDemandPoints().get(neighbor).getPopulation();
                grow.add(neighbor);
            }
            grow.remove(idx);
        }
        for(int j:remain){
            if (j<0) {
                cost-=j*penalty;
            }
        }
         */
    }

    public boolean checkContinuity(){
        ArrayList<ServicePoint> servicePoints=graph.getServicePoints();
        ArrayList<Integer> centerPoints=new ArrayList<>();
        HashSet<Integer> cover=new HashSet<>();
        for(int i=0;i<graph.getDemandPoints().size();i++){
            cover.add(i);
        }
        for(ServicePoint servicePoint:servicePoints){
            int idx=graph.getDemandPointIndex(servicePoint.getId());
            centerPoints.add(idx);
            cover.remove(idx);
        }
        for(Integer start:centerPoints){
            Queue<Integer> q=new LinkedList<>();
            q.add(start);
            while(q.size()!=0){
                int npos=q.poll();
                for(Integer neighbor:((ConnectedGraph)graph).getNeighbors(npos)){
                    if(!cover.contains(neighbor)||link[npos]!=link[neighbor]){
                        continue;
                    }
                    cover.remove(neighbor);
                    q.offer(neighbor);
                }
            }
        }
        return cover.size()==0;
    }

    public void fix(){
        ArrayList<ServicePoint> servicePoints=graph.getServicePoints();
        ArrayList<Integer> centerPoints=new ArrayList<>();
        HashSet<Integer> cover=new HashSet<>();
        for(int i=0;i<graph.getDemandPoints().size();i++){
            cover.add(i);
        }
        for(ServicePoint servicePoint:servicePoints){
            int idx=graph.getDemandPointIndex(servicePoint.getId());
            centerPoints.add(idx);
            cover.remove(idx);
        }
        for(Integer start:centerPoints){
            Queue<Integer> q=new LinkedList<>();
            q.add(start);
            while(q.size()!=0){
                int npos=q.poll();
                for(Integer neighbor:((ConnectedGraph)graph).getNeighbors(npos)){
                    if(!cover.contains(neighbor)||link[npos]!=link[neighbor]){
                        continue;
                    }
                    cover.remove(neighbor);
                    q.offer(neighbor);
                }
            }
        }
        while(cover.size()>0){
            ArrayList<Integer> removed=new ArrayList<>(),nowCover=new ArrayList<>(cover);
            for(Integer stray:nowCover){
                boolean succeed=false;
                for(Integer neighbor:((ConnectedGraph)graph).getNeighbors(stray)){
                    if(!cover.contains(neighbor)){
                        alter(stray,link[neighbor]);
                        succeed=true;
                    }
                }
                if(succeed){
                    removed.add(stray);
                }
            }
            if(removed.size()==0){
                break;
            }
            for(Integer stray:removed){
                cover.remove(stray);
            }
        }
    }

    private ArrayList<Integer> edgePoints(){
        ArrayList<Integer> ans=new ArrayList<>();
        for(int i=0;i<link.length;i++){
            for(Integer neighbor:((ConnectedGraph)graph).getNeighbors(i)){
                if(link[i]!=link[neighbor]){
                    ans.add(i);
                    break;
                }
            }
        }
        return ans;
    }

    private HashSet<Integer> nearServicePoints(int demand){
        HashSet<Integer> ans=new HashSet<>();
        for(Integer neighbor:((ConnectedGraph)graph).getNeighbors(demand)){
            if(link[neighbor]!=link[demand]){
                ans.add(link[neighbor]);
            }
        }
        return ans;
    }

    @Override
    public ConnectedSolution search1(){
        ConnectedSolution ans=getCopy();
        ArrayList<Integer> shuffled=edgePoints();
        Collections.shuffle(shuffled);
        double originalCost=cost,lastCost=cost;
        for(Integer i:shuffled){
            int originalJ=ans.link[i];
            boolean notImproved=true;
            ArrayList<Integer> servicePoints1=new ArrayList<>(nearServicePoints(i));
            Collections.shuffle(servicePoints1);
            for(Integer j:servicePoints1){
                if(j!=originalJ){
                    ans.alter(i,j);
                    if(ans.cost+0.001<lastCost&&ans.checkContinuity()){
                        notImproved=false;
                        lastCost=ans.cost;
                        break;
                    }
                }
            }
            if(notImproved){
                ans.alter(i,originalJ);
            }
        }
        return ans.getCost()+0.001<originalCost?ans:null;
    }

    @Override
    public ConnectedSolution search2(){
        ConnectedSolution ans=getCopy();
        ArrayList<Integer> shuffled1=edgePoints(),shuffled2=edgePoints();
        Collections.shuffle(shuffled1);
        double originalCost=cost,lastCost=cost;
        for(Integer i1:shuffled1){
            HashSet<Integer> mov1=nearServicePoints(i1);
            boolean notImproved=true;
            int originalJ1=ans.link[i1];
            Collections.shuffle(shuffled2);
            for(Integer i2:shuffled2){
                if(i1.equals(i2)||!mov1.contains(ans.link[i2])){
                    continue;
                }
                ans.alter(i1,ans.link[i2]);
                ArrayList<Integer> servicePoints2=new ArrayList<>(nearServicePoints(i2));
                int originalJ2=ans.link[i2];
                Collections.shuffle(servicePoints2);
                for(Integer j:servicePoints2){
                    ans.alter(i2,j);
                    if(ans.cost+0.001<lastCost&&ans.checkContinuity()){
                        notImproved=false;
                        lastCost=ans.cost;
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
        return ans.getCost()+0.001<originalCost?ans:null;
    }


    public ConnectedSolution reconstruct1(){
        ConnectedSolution ans=getCopy();
        Random random=new Random();
        int ruinProbability=5;
        ArrayList<Integer> shuffled=edgePoints();
        Collections.shuffle(shuffled);
        for(int i:shuffled){
            if(random.nextInt(100)<ruinProbability){
                ans.alter(i,graph.getNearestService()[i][0]);
            }
        }
        ans.fix();
        return ans;
    }

    public ConnectedSolution reconstruct2(){
        ConnectedSolution ans=getCopy();
        Random random=new Random();
        int ruinProbability=20;
        ArrayList<Integer> shuffled=new ArrayList<>();
        for(int i=0;i<link.length;i++){
            shuffled.add(i);
        }
        Collections.shuffle(shuffled);
        for(int i:shuffled){
            if(random.nextInt(100)<ruinProbability){
                //double tmp=random.nextDouble();
                //ans.alter(i,graph.getNearestService()[i][(int)(3*tmp*tmp)]);
                ans.alter(i,graph.getNearestService()[i][0]);
            }
        }
        ans.fix();
        return ans;
    }

    @Override
    public ConnectedSolution getCopy(){
        ConnectedSolution copy=new ConnectedSolution((ConnectedGraph) graph);
        copy.link=Arrays.copyOf(link,link.length);
        copy.remain=Arrays.copyOf(remain,remain.length);
        copy.cost=cost;
        return copy;
    }
}