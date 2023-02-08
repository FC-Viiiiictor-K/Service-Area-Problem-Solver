import java.util.ArrayList;

public class ServiceArea{
    private final int servicePointIndex;
    private final ArrayList<Integer> demandPointIndexes;
    private double totalCost;

    public ServiceArea(int servicePointIndex){
        this.servicePointIndex=servicePointIndex;
        demandPointIndexes=new ArrayList<>();
        totalCost=0;
    }
    public void addDemandPoint(int demandPointIndex,double addCost){
        demandPointIndexes.add(demandPointIndex);
        totalCost+=addCost;
    }

    public int getServicePointIndex() {
        return servicePointIndex;
    }

    public ArrayList<Integer> getDemandPointIndexes() {
        return demandPointIndexes;
    }

    public double getTotalCost() {
        return totalCost;
    }

    @Override
    public int hashCode(){
        long hashVal=0,hashMod = 1000000007,hashBase = 19260817;
        for(int demandPointIndex:demandPointIndexes){
            hashVal=(hashVal* hashBase +demandPointIndex)% hashMod;
        }
        return (int)hashVal;
    }
}
