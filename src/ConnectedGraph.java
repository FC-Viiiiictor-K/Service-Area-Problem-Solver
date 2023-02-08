import java.util.ArrayList;

public class ConnectedGraph extends Graph{
    private ArrayList<Integer>[] neighbors;
    public ConnectedGraph(){
        super();
    }
    public void initializeNeighbors(){
        neighbors=new ArrayList[getDemandPoints().size()];
        for(int i=0;i<getDemandPoints().size();i++){
            neighbors[i]=new ArrayList<>();
        }
    }
    public void addEdge(String id1,String id2){
        int idx1=getDemandPointIndex(id1),idx2=getDemandPointIndex(id2);
        if(neighbors[idx1].contains(idx2)){
            return;
        }
        neighbors[idx1].add(idx2);
        neighbors[idx2].add(idx1);
    }
    public ArrayList<Integer> getNeighbors(int demand){
        return neighbors[demand];
    }
}
