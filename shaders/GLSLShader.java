package shaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL30.*;

public abstract class GLSLShader {
    
    public static ArrayList<GLSLShader> loadedShaders = new ArrayList<>();
    
    protected int programId;
    protected int vertexShaderId;
    protected int fragmentShaderId;
    protected int transformMatrixLocation,projectionMatrixLocation,cameraMatrixLocation;
    
    protected int lightColourLocation=0;
    protected int lightPositionLocation=0;
    
    private static FloatBuffer tempMatrix = BufferUtils.createFloatBuffer(16);
    
    public GLSLShader(String shaderName){
        start();
        vertexShaderId = load("src/shaders/vert/"+shaderName+".vert", GL_VERTEX_SHADER);
        fragmentShaderId = load("src/shaders/frag/"+shaderName+".frag",GL_FRAGMENT_SHADER);
        programId = glCreateProgram();
        glAttachShader(programId, vertexShaderId);
        glAttachShader(programId, fragmentShaderId);
        bindAttributes();
        glLinkProgram(programId);
        getAllUniformLocations();
        glValidateProgram(programId);
        loadedShaders.add(this);
    }
    
        
    
    protected void getAllUniformLocations(){
        transformMatrixLocation = getUniformLocation("transformationMatrix");
        cameraMatrixLocation = getUniformLocation("cameraMatrix");
        projectionMatrixLocation = getUniformLocation("projectionMatrix");
    }
    
    protected int getUniformLocation(String uniformName){
        return glGetUniformLocation(programId, uniformName);
    }
    
    protected void loadUniformMatrix4f(int location, Matrix4f matrix){
        float[] buffer = new float[16];
        matrix.get(buffer);
        glUniformMatrix4fv(location, false, buffer);
    }
    
    protected void loadUniformFloat(int location,float uniform){
        glUniform1f(location, uniform);
    }
    
    protected void loadUniformInt(int location,int uniform){
        glUniform1i(location, uniform);
    }
    
    protected void loadUniformVector3f(int location, Vector3f vec){
        glUniform3f(location,vec.x,vec.y,vec.z);
    }
    
    public void loadTransformationMatrix(Matrix4f transform){
         loadUniformMatrix4f(transformMatrixLocation, transform);
    }
    
    public void loadCameraMatrix(Matrix4f transform){
         loadUniformMatrix4f(cameraMatrixLocation, transform);
    }
    
    public void loadProjectionMatrix(Matrix4f transform){
         loadUniformMatrix4f(projectionMatrixLocation, transform);
    }
    
    
    public void start(){
        glUseProgram(programId);
    }
    
    public void stop(){
        glUseProgram(0);
    }
    
    public void delete(){
        stop();
        glDetachShader(programId, vertexShaderId);
        glDetachShader(programId, fragmentShaderId);
        glDeleteShader(vertexShaderId);
        glDeleteShader(fragmentShaderId);
        glDeleteProgram(programId);
    }
    
    public static void deleteAll(){
        for(GLSLShader shader: loadedShaders){
            shader.delete();
        }
        loadedShaders.clear();
    }
    
    /**
     * Links shader input to VAO attributes.
     */
    protected abstract void bindAttributes();
    
    protected void bindAttribute(int attributeId, String varName){
        glBindAttribLocation(programId, attributeId, varName);
    }
    
    /**
     * Loads a shader from a file and compiles it.
     */
    private static int load(String file, int type){
        StringBuilder shaderSource = new StringBuilder();
        try{
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while((line=reader.readLine()) != null){
                shaderSource.append(line).append('\n');
            }
            reader.close();
        }
        catch(IOException e){
            System.err.println("Shader \""+file+"\" could not be read.");
            System.exit(-1);
        }
        int shaderId = glCreateShader(type);
        glShaderSource(shaderId, shaderSource);
        glCompileShader(shaderId);
        if(glGetShaderi(shaderId,GL_COMPILE_STATUS)==GL_FALSE){
            System.err.println("Shader \""+file+"\" could not be compiled.");
            System.err.println(glGetShaderInfoLog(shaderId, 500));
            System.exit(-1);
        }
        return shaderId;
    }

}
