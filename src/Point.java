public abstract class Point {
    private final double x,y;
    private final String id;
    public Point(String id,double x,double y){
        this.id=id;
        this.x=x;
        this.y=y;
    }
    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
    public String getId(){
        return id;
    }
    public double distance(Point p){
        return Math.sqrt((x-p.x)*(x-p.x)+(y-p.y)*(y-p.y))/1000.0;
    }
}
