package math;

import com.sun.prism.impl.BufferUtil;
import static java.lang.Math.*;
import java.nio.FloatBuffer;

/**
 *
 * @author Owner
 */


public class TransformMatrix {
   
    protected float[] m;
    static TransformMatrix temp = new TransformMatrix();
    
    public TransformMatrix(){
        m = new float[]
               {1,0,0,0,
                0,1,0,0,
                0,0,1,0,
                0,0,0,1};
    }
    
    public void setIdentity(){
        clear();
        m[0]=1;
        m[5]=1;
        m[10]=1;
        m[15]=1;
    }
    
    public void clear(){
        for(int i=0;i<16;i++){
            m[i]=0;
        }
    }
    
    public void add(TransformMatrix additive){
        for(int i=0;i<16;i++){
            m[i]+=additive.m[i];
        }
    }
    
    public void multiply(float multiplier){
        for(int i=0;i<16;i++){
            m[i]*=multiplier;
        }
    }
  
    /**
     * 
     * Applies a multiply operation of on top of this matrix.
     */
    public void applyMultiply(TransformMatrix multiplier) {
        TransformMatrix result = new TransformMatrix();
        float sum = 0;
        
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                sum=0;
                for(int i=0;i<4;i++){
                    sum+=multiplier.m[r*4+i]*m[c*4+i];
                }
                result.m[r*4+c]=sum;
            }
        }
        
        m = result.m;
    }
    
    public void transform(Vec3 a){
        Vec3 b = new Vec3(a.x,a.y,a.z);
        a.x = m[0]*b.x + m[1]*b.y + m[2]*b.z + m[3];
        a.y = m[4]*b.x + m[5]*b.y + m[6]*b.z + m[7];
        a.z = m[8]*b.x + m[9]*b.y + m[10]*b.z + m[11];
    }
    
    /**
     * 
     * Applies a multiply operation of this matrix on top of the parameter and assigns it back to this matrix.
     */
    public void applyMultiplyBelow(TransformMatrix multiplier) {
        TransformMatrix result = new TransformMatrix();
        float sum = 0;
        
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                sum=0;
                for(int i=0;i<4;i++){
                    sum+=m[r*4+i]*multiplier.m[c*4+i];
                }
                result.m[r*4+c]=sum;
            }
        }
        
        m = result.m;
    }
    
    public void transpose(){
        float temp=0;
        for(int r=0;r<4;r++){
            for(int c=r;c<4;c++){
                temp = m[r*4+c];
                m[r*4+c]=m[c*4+r];
                m[c*4+r]=temp;
            }
        }
    }
    
    public void orthographic(float left, float right, float bottom, float top, float near, float far) {
        clear();
        float tx = -(right + left) / (right - left);
        float ty = -(top + bottom) / (top - bottom);
        float tz = -(far + near) / (far - near);

        m[0] = 2f / (right - left);
        m[5] = 2f / (top - bottom);
        m[10] = -2f / (far - near);
        m[3] = tx;
        m[7] = ty;
        m[11] = tz;

    }
    
    public  void frustum(float left, float right, float bottom, float top, float near, float far) {
        clear();
        float a = (right + left) / (right - left);
        float b = (top + bottom) / (top - bottom);
        float c = -(far + near) / (far - near);
        float d = -(2f * far * near) / (far - near);

        m[0] = (2f * near) / (right - left);
        m[5] = (2f * near) / (top - bottom);
        m[2] = a;
        m[6] = b;
        m[10] = c;
        m[14] = -1f;
        m[11] = d;
        m[15] = 0f;
    }
    
    public void perspective(float fov, float aspect, float near, float far) {
        this.clear();
        float yScale = (float) (1f / Math.tan(Math.toRadians(fov/2f)))*aspect;
        float xScale = yScale/aspect;
        float frustum = far - near;
        
        m[0] = xScale;
        m[5] = yScale;
        m[10] = -((far+near)/frustum);
        m[11] = -1f;
        m[14] = -((2*near*far)/frustum);
        m[15]=0;
    }
    
    public void translate(Vec3 a){
        translate(a.x,a.y,a.z);
    }
    
    public void translate(float x, float y, float z){
       m[0] += x * m[3];
       m[1] += y * m[3];
       m[2] += z * m[3];
       
       m[4] += x * m[7];
       m[5] += y * m[7];
       m[6] += z * m[7];
       
       m[8] += x * m[11];
       m[9] += y * m[11];
       m[10] += z * m[11];
       
       m[12] += x * m[15];
       m[13] += y * m[15];
       m[14] += z * m[15];
    }
    
    public void rotate(float x, float y, float z){
        temp.setToEuler(x, y, z);
        applyMultiply(temp);
    }
    
    public void rotate(float x, float y, float z, float w){
        temp.setToQuaternion(x, y, z, w);
        applyMultiply(temp);
    }
    
    public void setToEuler(float x, float y, float z){
        float f = x / 2;
        float c1 = (float)cos(f);
        float s1 = (float)sin(f);
        f = y / 2;
        float c2 = (float)cos(f);
        float s2 = (float)sin(f);
        f = z / 2;
        float c3 = (float)cos(f);
        float s3 = (float)sin(f);
        // YZX
        x = s1 * c2 * c3 + c1 * s2 * s3;
        y = c1 * s2 * c3 + s1 * c2 * s3;
        z = c1 * c2 * s3 - s1 * s2 * c3;
        float w = c1 * c2 * c3 - s1 * s2 * s3;
        
        setToQuaternion(x, y, z, w);
    }
    
    public void setToQuaternion(float x, float y, float z, float w) {
        
        
        float x2 = x + x;
        float y2 = y + y;
        float z2 = z + z;
        float xx = x * x2;
        float xy = x * y2;
        float xz = x * z2;
        float yy = y * y2;
        float yz = y * z2;
        float zz = z * z2;
        float wx = w * x2;
        float wy = w * y2;
        float wz = w * z2;

        m[0] = 1.0f - (yy + zz);
        m[4] = xy - wz;
        m[8] = xz + wy;

        m[1] = xy + wz;
        m[5] = 1.0f - (xx + zz);
        m[9] = yz - wx;

        m[2] = xz - wy;
        m[6] = yz + wx;
        m[10] = 1.0f - (xx + yy);

        m[3] = 0;
        m[7] = 0;
        m[11] = 0;
        m[12] = 0;
        m[13] = 0;
        m[14] = 0;
        m[15] = 1;
    }
    
    
    public void scale(float x, float y, float z) {
        m[0] *= x;
        m[1] *= x;
        m[2] *= x;
        m[3] *= x;
        m[4] *= y;
        m[5] *= y;
        m[6] *= y;
        m[7] *= y;
        m[8] *= z;
        m[9] *= z;
        m[10] *= z;
        m[11] *= z;
    }
    
    public float[] getArray(){
        return m;
    }

    public void toFloatBuffer(FloatBuffer buffer) {
        buffer.put(m[0]).put(m[1]).put(m[2]).put(m[3]);
        buffer.put(m[4]).put(m[5]).put(m[6]).put(m[7]);
        buffer.put(m[8]).put(m[9]).put(m[10]).put(m[11]);
        buffer.put(m[12]).put(m[13]).put(m[14]).put(m[15]);
        buffer.flip();
    }
    
    public void inverse(){
        temp.copyFrom(this);
        float[] b = new float[12];
        
        b[0] =  temp.m[0] *  temp.m[5] -   temp.m[1] *  temp.m[4];
        b[1] =  temp.m[0] *  temp.m[6] -   temp.m[2] *  temp.m[4];
        b[2] =  temp.m[0] *  temp.m[7] -   temp.m[3] *  temp.m[4];
        
        b[3] =  temp.m[1] *  temp.m[6] -   temp.m[2] *  temp.m[5];
        b[4] =  temp.m[1] *  temp.m[7] -   temp.m[3] *  temp.m[5];
        b[5] =  temp.m[2] *  temp.m[7] -   temp.m[3] *  temp.m[6];
        
        b[6] =  temp.m[8] *  temp.m[13] -  temp.m[9] *  temp.m[12];
        b[7] =  temp.m[8] *  temp.m[14] -  temp.m[10] * temp.m[12];
        b[8] =  temp.m[8] *  temp.m[15] -  temp.m[11] * temp.m[12];
        
        b[9] =  temp.m[9] *  temp.m[14] -  temp.m[10] * temp.m[13];
        b[10] = temp.m[9] *  temp.m[15] -  temp.m[11] * temp.m[13];
        b[11] = temp.m[10] * temp.m[15] -  temp.m[11] * temp.m[14];
     

        float det = b[0] * b[11] - b[1] * b[10] + b[2] * b[9] + b[3] * b[8] - b[4] * b[7] + b[5] * b[6];
        if (det == 0.0){
            setIdentity();
            return;
        }
        det = 1.0f / det;

        m[0] =  (temp.m[5]  * b[11] - temp.m[6] * b[10] + temp.m[7]  * b[9]) * det;
        m[1] =  (temp.m[2]  * b[10] - temp.m[1] * b[11] - temp.m[3]  * b[9]) * det;
        m[2] =  (temp.m[13] * b[5] - temp.m[14] * b[4]  + temp.m[15] * b[3]) * det;
        m[3] =  (temp.m[10] * b[4] - temp.m[9]  * b[5]  - temp.m[11] * b[3]) * det;
        m[4] =  (temp.m[6]  * b[8] - temp.m[4]  * b[11] - temp.m[7]  * b[7]) * det;
        m[5] =  (temp.m[0]  * b[11] - temp.m[2] * b[8]  + temp.m[3]  * b[7]) * det;
        m[6] =  (temp.m[14] * b[2] - temp.m[12] * b[5]  - temp.m[15] * b[1]) * det;
        m[7] =  (temp.m[8]  * b[5] - temp.m[10] * b[2]  + temp.m[11] * b[1]) * det;
        m[8] =  (temp.m[4]  * b[10] - temp.m[5] * b[8]  + temp.m[7]  * b[6]) * det;
        m[9] =  (temp.m[1]  * b[8] - temp.m[0]  * b[10] - temp.m[3]  * b[6]) * det;
        m[10] = (temp.m[12] * b[4] - temp.m[13] * b[2]  + temp.m[15] * b[0]) * det;
        m[11] = (temp.m[9]  * b[2] - temp.m[8]  * b[4]  - temp.m[11] * b[0]) * det;
        m[12] = (temp.m[5]  * b[7] - temp.m[4]  * b[9]  - temp.m[6]  * b[6]) * det;
        m[13] = (temp.m[0]  * b[9] - temp.m[1]  * b[7]  + temp.m[2]  * b[6]) * det;
        m[14] = (temp.m[13] * b[1] - temp.m[12] * b[3]  - temp.m[14] * b[0]) * det;
        m[15] = (temp.m[8]  * b[3] - temp.m[9]  * b[1]  + temp.m[10] * b[0]) * det;
           
    }
    
    public void copyFrom(TransformMatrix a){
        for(int i=0;i<16;i++)
            m[i]=a.m[i];
    }
    
    @Override
    public String toString(){
        String r="";
        for(int i=0;i<16;i++){
            if(i%4==0)
                r+='\n';
            r+=m[i]+" ";
        }
        return r;
    }
    
}
