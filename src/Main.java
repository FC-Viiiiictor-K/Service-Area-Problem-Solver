import java.io.*;
import java.util.Scanner;

public class Main {
    private static Graph graph;
    private static Solution solution,bestSolution;
    private static final int notImproveBound=50;
    private static final double answer=76566.5355;
    private static int totalLoop=0;
    private static SppModel sppModel;

    public static void vnd(){
        while(true){
            totalLoop++;
            Solution tmp;
            tmp=solution.search1();
            if(tmp!=null){
                solution=tmp;
                continue;
            }
            tmp=solution.search2();
            if(tmp!=null){
                solution=tmp;
                continue;
            }
            break;
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner sc=new Scanner(new File("./data/source.txt"));
        graph=new Graph();
        sc.nextLine();
        while(sc.hasNext()){
            String id=sc.next();
            int population=sc.nextInt();
            double x=sc.nextDouble();
            double y=sc.nextDouble();
            sc.next();
            sc.next();
            int capacity=sc.nextInt();
            graph.addDemandPoint(id,population,x,y);
            if(capacity>0) {
                graph.addServicePoint(id,capacity,x,y);
            }
        }
        graph.constructDistance();
        graph.constructNearest();

        solution=new Solution(graph);
        sppModel=new SppModel(graph);
        long time=System.currentTimeMillis();
        int notImproved=0;
        bestSolution=solution.getCopy();
        while(true){
            vnd();
            ServiceArea[] tmp=solution.getAllServiceAreas();
            for(ServiceArea serviceArea:tmp){
                if(serviceArea==null){
                    continue;
                }
                sppModel.addServiceArea(serviceArea);
            }
            if(solution.getCost()<bestSolution.getCost()){
                notImproved+=(solution.getCost()-bestSolution.getCost())/bestSolution.getCost()*notImproveBound*1000;
                notImproved=Math.max(notImproved,0);
                System.out.println("notImproved="+notImproved);
                bestSolution=solution.getCopy();
                solution=bestSolution.reconstruct();
                //System.out.println("Reconstructed!");
            }
            else{
                if(notImproved<notImproveBound){
                    notImproved++;
                    solution=bestSolution.reconstruct();
                    //System.out.println("Not improved, reconstructed!");
                    System.out.println("notImproved="+notImproved);
                }
                else{
                    break;
                }
            }
            System.out.println("Current best solution:"+bestSolution.getCost()+"\nGap: "+(bestSolution.getCost()-answer)/answer*100+"%");
        }
        //bestSolution.outputInformation();
        System.out.println("----------End of search----------");
        System.out.println("Best solution found: "+bestSolution.getCost()+"\nGap: "+(bestSolution.getCost()-answer)/answer*100+"%");
        System.out.println("Total search loop: "+totalLoop);
        System.out.println("Total search time: "+(System.currentTimeMillis()-time));

        File lpModelFile=new File("./data/sppModel.lp");
        if(!lpModelFile.exists()){
            lpModelFile.createNewFile();
        }
        FileOutputStream fos=new FileOutputStream(lpModelFile);
        fos.write(sppModel.toString().getBytes());

        Runtime runtime=Runtime.getRuntime();
        try{
            Process process=runtime.exec("cbc \"data/sppModel.lp\"");
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while((line= bufferedReader.readLine())!=null){
                System.out.println(line);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
