public class DemandPoint extends Point{
    private final int population;
    public DemandPoint(String id,int population,double x,double y){
        super(id,x,y);
        this.population=population;
    }
    public int getPopulation(){
        return population;
    }
}
