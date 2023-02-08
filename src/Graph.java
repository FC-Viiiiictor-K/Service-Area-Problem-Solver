import java.util.ArrayList;
import java.util.HashMap;

public class Graph {
    private final ArrayList<DemandPoint> demandPoints;
    private final ArrayList<ServicePoint> servicePoints;
    private final HashMap<String,Integer> demandPointIndex,servicePointIndex;
    private double[][] distance;
    private int[][] nearestService;
    public Graph(){
        demandPoints=new ArrayList<>();
        servicePoints=new ArrayList<>();
        demandPointIndex=new HashMap<>();
        servicePointIndex=new HashMap<>();
    }
    public void addDemandPoint(String id,int population,double x,double y){
        demandPoints.add(new DemandPoint(id,population,x,y));
        demandPointIndex.put(id,demandPoints.size()-1);
    }
    public void addServicePoint(String id,int capacity,double x,double y){
        servicePoints.add(new ServicePoint(id,capacity,x,y));
        servicePointIndex.put(id,servicePoints.size()-1);
    }
    public void constructDistance(){
        distance=new double[demandPoints.size()][servicePoints.size()];
        for(int i=0;i<demandPoints.size();i++){
            for(int j=0;j<servicePoints.size();j++){
                distance[i][j]=demandPoints.get(i).distance(servicePoints.get(j));
            }
        }
    }
    public ArrayList<Integer> sortedNearestServicePoints(int demand){
        ArrayList<Integer> sorted=new ArrayList<>();
        for(int i=0;i<servicePoints.size();i++){
            sorted.add(i);
        }
        sorted.sort((o1,o2) -> Double.compare(distance[demand][o1],distance[demand][o2]));
        return sorted;
    }
    public void constructNearest(){
        nearestService=new int[demandPoints.size()][servicePoints.size()];
        for(int i=0;i<demandPoints.size();i++){
            ArrayList<Integer> tmp=sortedNearestServicePoints(i);
            for(int j=0;j<servicePoints.size();j++){
                nearestService[i][j]=tmp.get(j);
            }
        }
    }

    public ArrayList<DemandPoint> getDemandPoints() {
        return demandPoints;
    }

    public ArrayList<ServicePoint> getServicePoints() {
        return servicePoints;
    }

    public double[][] getDistance(){
        return distance;
    }

    public int[][] getNearestService() {
        return nearestService;
    }

    public int getServicePointIndex(String id){
        return servicePointIndex.get(id);
    }

    public int getDemandPointIndex(String id){
        return demandPointIndex.get(id);
    }

    public String getLPModel(){
        StringBuilder sb=new StringBuilder("Minimize\nOBJ: ");
        for(int i=0;i<demandPoints.size();i++){
            for(int j=0;j<servicePoints.size();j++){
                sb.append(String.format("%.8f",distance[i][j]*demandPoints.get(i).getPopulation())).append(" x_").append(i).append("_").append(j).append(" + ");
            }
        }
        sb.delete(sb.length()-3,sb.length());
        sb.append("\nsubject to\n");
        for(int i=0;i<demandPoints.size();i++){
            sb.append("D").append(i).append(": ");
            for(int j=0;j<servicePoints.size();j++){
                sb.append("x_").append(i).append("_").append(j).append(" + ");
            }
            sb.delete(sb.length()-3,sb.length());
            sb.append(" = 1\n");
        }
        for(int j=0;j<servicePoints.size();j++){
            sb.append("S").append(j).append(": ");
            for(int i=0;i<demandPoints.size();i++){
                sb.append(demandPoints.get(i).getPopulation()).append(" x_").append(i).append("_").append(j).append(" + ");
            }
            sb.delete(sb.length()-3,sb.length());
            sb.append(" <= ").append(servicePoints.get(j).getCapacity()).append("\n");
        }
        sb.append("Binaries\n");
        for(int i=0;i<demandPoints.size();i++){
            for(int j=0;j<servicePoints.size();j++){
                sb.append("x_").append(i).append("_").append(j).append("\n");
            }
        }
        sb.append("End");
        return sb.toString();
    }
}
