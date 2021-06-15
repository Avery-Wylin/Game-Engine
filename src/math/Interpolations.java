package math;

public class Interpolations {
    /**
     * Linear Interpolation.
     */
    public static float lerp(float a, float b, float t){
        return a+t*(b-a);
    }
}
