import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class LPModelGenerator {
    private static Graph graph;
    public static void main(String[] args) throws IOException {
        Scanner sc=new Scanner(new File("C:\\Projects\\IntelliJ IDEA\\GIS\\data\\source.txt"));
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
        File lpModelFile=new File("C:\\Projects\\IntelliJ IDEA\\GIS\\data\\model.lp");
        if(!lpModelFile.exists()){
            lpModelFile.createNewFile();
        }
        FileOutputStream fos=new FileOutputStream(lpModelFile);
        fos.write(graph.getLPModel().getBytes());
    }
}
