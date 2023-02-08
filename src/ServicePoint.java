public class ServicePoint extends Point{
    private final int capacity;
    public ServicePoint(String id,int capacity,double x,double y){
        super(id,x,y);
        this.capacity=capacity;
    }
    public int getCapacity(){
        return capacity;
    }
}
