package math;
import static java.lang.Math.*;
import java.nio.FloatBuffer;

public class Vec3 {
    public float x=0,y=0,z=0;
    
    public Vec3(){
    }
    
    public Vec3(float x, float y, float z){
        set(x,y,z);
    }
    
    public void add(float a){
        this.x+=a;
        this.y+=a;
        this.z+=a;
    }
    
    public void add(float x, float y, float z){
        this.x+=x;
        this.y+=y;
        this.z+=z;
    }
    
    public void add(Vec3 a){
        x+=a.x;
        y+=a.y;
        z+=a.z;
    }
    
    /**
     * 
     * Adds a scaled vector.
     */
    public void addScaled(Vec3 a, float scale){
        x+=a.x*scale;
        y+=a.y*scale;
        z+=a.z*scale;
    }
    
    public void set(float x, float y, float z){
        this.x=x;
        this.y=y;
        this.z=z;
    }
    
    public void multiply(float a){
        x*=a;
        y*=a;
        z*=a;
    }
    
    public void normalize(){
        float h = (float)sqrt(x*x+y*y+z*z);
        multiply(1/h);
    } 
    
    public void applyTransformation(TransformMatrix a){
        float x1=x,y1=y,z1=z;
        x=a.m[0]*x1+a.m[1]*y1+a.m[2]*z1+a.m[3]*1;
        y=a.m[4]*x1+a.m[5]*y1+a.m[6]*z1+a.m[7]*1;
        z=a.m[8]*x1+a.m[9]*y1+a.m[10]*z1+a.m[11]*1;
    }
    
    public String toString(){
        return "x:"+x+" y:"+y+" z:"+z;
    }

    public float length() {
        return (float)pow(x*x+y*y+z*z,.5);
    }
    
    public void setFrom(Vec3 a){
        x=a.x;
        y=a.y;
        z=a.z;
    }
    
    /**
     * Returns true if the vector contains a value greater than that given.
     */
    public boolean isSignificant(float threshold){
        return abs(x)>threshold || abs(y)>threshold || abs(z)>threshold;
    }
    
    /**
     * 
     * Zeros any value within the radius of the threshold centered at zero.
     */
    public void removeSignificance(float threshold){
        threshold=abs(threshold);
        x=abs(x)<=threshold?0:x;
        y=abs(y)<=threshold?0:y;
        z=abs(z)<=threshold?0:z;
    }
    
    public void setFrom(Vec2 a){
        x=a.x;
        y=a.y;
    }
    
    /**
     * 
     * Returns the cross product of the vector by its parameter.
     */
    public Vec3 cross(Vec3 a){
        Vec3 r = new Vec3();
        r.x = y*a.z - z*a.y;
        r.y = z*a.x - x*a.z;
        r.z = x*a.y - y*a.x;
        return r;
    }
    
    public static float barycentric(Vec3 a, Vec3 b, Vec3 c,float x,float z){
        float denom = (b.z-c.z)*(a.x-c.x)+(c.x-b.x)*(a.z-c.z);
        float d0 = ((b.z-c.z)*(x-c.x)+(c.z-b.x)*(z-c.z))/denom;
        float d1 = ((c.z-a.z)*(x-c.x)+(a.x-c.x)*(z-c.z))/denom;
        float d2 = 1f-d0-d1;
        return d0 * a.y + d1 * b.y + d2 * c.y;
    }
    
    
}
