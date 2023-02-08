import java.util.ArrayList;
import java.util.HashSet;

public class SppModel {
    private final ArrayList<ServiceArea> serviceAreas;
    private final ArrayList[] serviceAreaOfServicePoint,serviceAreaOfDemandPoint;
    private HashSet[] used;
    public SppModel(Graph graph){
        serviceAreas=new ArrayList<>();
        serviceAreaOfServicePoint=new ArrayList[graph.getServicePoints().size()];
        serviceAreaOfDemandPoint=new ArrayList[graph.getDemandPoints().size()];
        used=new HashSet[graph.getServicePoints().size()];
    }
    public void addServiceArea(ServiceArea serviceArea){
        int si=serviceArea.getServicePointIndex();
        ArrayList<Integer> dis=serviceArea.getDemandPointIndexes();
        if(used[si]==null){
            used[si]=new HashSet<Integer>();
        }
        if(!used[si].contains(serviceArea.hashCode())){
            serviceAreas.add(serviceArea);
            used[si].add(serviceArea.hashCode());
            if(serviceAreaOfServicePoint[si]==null){
                serviceAreaOfServicePoint[si]=new ArrayList<Integer>();
            }
            serviceAreaOfServicePoint[si].add(serviceAreas.size()-1);
            for(int di:dis){
                if(serviceAreaOfDemandPoint[di]==null){
                    serviceAreaOfDemandPoint[di]=new ArrayList<Integer>();
                }
                serviceAreaOfDemandPoint[di].add(serviceAreas.size()-1);
            }
        }
    }
    @Override
    public String toString(){
        StringBuilder sb=new StringBuilder("Minimize\nOBJ: ");
        for(int i=0;i<serviceAreas.size();i++){
            sb.append(String.format("%.8f",serviceAreas.get(i).getTotalCost())).append(" x_").append(i).append(" + ");
        }
        sb.delete(sb.length()-3,sb.length());
        sb.append("\nsubject to\n");
        for(int i=0;i<serviceAreaOfDemandPoint.length;i++){
            if(serviceAreaOfDemandPoint[i]!=null){
                sb.append("D").append(i).append(": ");
                for(Object j:serviceAreaOfDemandPoint[i]){
                    sb.append("x_").append((Integer)j).append(" + ");
                }
                sb.delete(sb.length()-3,sb.length());
                sb.append(" = 1\n");
            }
        }
        for(int i=0;i<serviceAreaOfServicePoint.length;i++){
            if(serviceAreaOfServicePoint[i]!=null){
                sb.append("S").append(i).append(": ");
                for(Object j:serviceAreaOfServicePoint[i]){
                    sb.append("x_").append((Integer)j).append(" + ");
                }
                sb.delete(sb.length()-3,sb.length());
                sb.append(" <= 1\n");
            }
        }
        sb.append("Binaries\n");
        for(int i=0;i<serviceAreas.size();i++){
            sb.append("x_").append(i).append("\n");
        }
        sb.append("End");
        return sb.toString();
    }
}
