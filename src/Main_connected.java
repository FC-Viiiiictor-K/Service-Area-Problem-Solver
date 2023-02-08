import java.io.*;
import java.math.BigDecimal;
import java.util.Random;
import java.util.Scanner;

public class Main_connected {
    private static ConnectedGraph graph;
    private static ConnectedSolution solution,bestSolution;
    private static final int notImproveBound=500;
    private static final double answer=79514.67;
    private static int totalLoop=0;
    private static SppModel sppModel;

    public static void vnd(){
        while(true){
            ConnectedSolution tmp;
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
        Scanner sc=new Scanner(new File("C:\\Projects\\IntelliJ IDEA\\GIS\\data\\source.txt"));
        graph=new ConnectedGraph();
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
        graph.initializeNeighbors();
        sc=new Scanner(new File("C:\\Projects\\IntelliJ IDEA\\GIS\\data\\connectivity.txt"));
        sc.nextLine();
        while(sc.hasNext()){
            String line=sc.nextLine();
            String[] tokens=line.split(",");
            graph.addEdge(tokens[1],tokens[2]);
        }

        solution=new ConnectedSolution(graph);
        solution.fix();
        sppModel=new SppModel(graph);
        long time=System.currentTimeMillis();
        int notImproved=0;
        bestSolution=solution.getCopy();
        Random random=new Random();
        while(true){
            totalLoop++;
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
                bestSolution=solution.getCopy();
                //System.out.println(solution.checkContinuity());
                //System.out.println("Reconstructed!");
            }
            else{
                if(notImproved<notImproveBound){
                    notImproved++;
                    //System.out.println(solution.checkContinuity());
                    //System.out.println("Not improved, reconstructed!");
                }
                else{
                    break;
                }
            }
            if(random.nextInt(2)>0){
                solution=bestSolution.reconstruct2();
            }
            else{
                solution=bestSolution.reconstruct1();
            }
            System.out.println("totalLoop="+totalLoop+"\tnotImproved="+notImproved+"\tCurrent best solution:"+bestSolution.getCost()+"\tGap: "+(bestSolution.getCost()-answer)/answer*100+"%\tContinuity: "+bestSolution.checkContinuity());
        }
        //bestSolution.outputInformation();
        System.out.println("----------End of search----------");
        System.out.println("Continuity: "+bestSolution.checkContinuity());
        System.out.println("Best solution found: "+bestSolution.getCost()+"\nGap: "+(bestSolution.getCost()-answer)/answer*100+"%");
        System.out.println("Total search loop: "+totalLoop);
        System.out.println("Total search time: "+(System.currentTimeMillis()-time));

        File lpModelFile=new File("C:\\Projects\\IntelliJ IDEA\\GIS\\data\\sppModel.lp");
        if(!lpModelFile.exists()){
            lpModelFile.createNewFile();
        }
        FileOutputStream fos=new FileOutputStream(lpModelFile);
        fos.write(sppModel.toString().getBytes());

        Runtime runtime=Runtime.getRuntime();
        double finalResult = 1000000.0;
        try{
            Process process=runtime.exec("cbc \"C:\\Projects\\IntelliJ IDEA\\GIS\\data\\sppModel.lp\"");
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while((line= bufferedReader.readLine())!=null){
                System.out.println(line);
                if(line.startsWith("Objective value:                ")){
                    String[] split=line.split(" ");
                    finalResult=Double.parseDouble(split[split.length-1]);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        BigDecimal bigDecimal=new BigDecimal((finalResult-answer)/answer*100);
        System.out.println("** Final Gap: "+bigDecimal.toString().substring(0,18)+"% **");
    }
}
