package math;
import math.*;
import static java.lang.Math.*;

public class Vec2 {
    public float x=0,y=0;
    
    public Vec2(){
    }
    
    public Vec2(float x, float y){
        set(x,y);
    }
    
    public void add(float x, float y){
        this.x+=x;
        this.y+=y;
    }
    
    public void set(float x, float y){
        this.x=x;
        this.y=y;
    }
    
    public void multiply(float a){
        x*=a;
        y*=a;
    }
    
    public void normalize(){
        float h = (float)sqrt(x*x+y*y);
        multiply(1/h);
    } 
}
