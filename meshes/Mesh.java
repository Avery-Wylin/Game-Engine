package meshes;

import entities.Terrain;
import math.Vec2;
import math.TransformMatrix;
import math.Vec3;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL30.*;
import shaders.GLSLShader;
import textures.TextureManager;


public class Mesh {
    
    private static ArrayList<Mesh> loadedMeshes = new ArrayList<>() ;
    public static int ATTRB_POS=0, ATTRB_UV=1, ATTRB_NORMAL=2;
    
    protected boolean hasVAO = false;
    protected boolean hasUVs = false;
    protected int vaoId=0;
    protected ArrayList<Integer> vboIds = new ArrayList<Integer>();
    protected int vertexCount;
    protected float size;
    
    public Mesh(){
        createVAO();
    }
    
    public Mesh(float[] vertexPos, int[] vertexOrder, float[] uv, float[] normals){
        createVAO();
        load(vertexPos, vertexOrder, uv, normals);
    }

    public Mesh(String objFileName) {
        createVAO();
        loadFromObj(objFileName);
    }
    
    
    public boolean hasVAO(){
        return hasVAO;
    }
    
    public void removeVAO(){
        if (hasVAO) {
            hasVAO = false;
            for (int vbo : vboIds) {
                glDeleteBuffers(vbo);
            }
            glDeleteVertexArrays(vaoId);
        }
    }
    
    public static void removeAllWithoutVAO(){
        for(int i=loadedMeshes.size();i>=0;i--){
            if(!loadedMeshes.get(i).hasVAO){
                loadedMeshes.remove(i);
            }
        }
    }
    
    public static void removeAll(){
        for(Mesh mesh:loadedMeshes){
            mesh.removeVAO();
        }
        loadedMeshes.clear();
    }
    
    public void createVAO() {
        vaoId = glGenVertexArrays();
        hasVAO = true;
    }
    
    /**
     * Loads all non-null parameters to their respective buffers.
     */
    public void load(float[] vertexPos, int[] vertexOrder, float[] uv, float[] normals){
        if(hasVAO){
            glBindVertexArray(vaoId);
            if(vertexOrder != null)
            loadAttributeInt(vertexOrder,GL_ELEMENT_ARRAY_BUFFER,GL_STATIC_DRAW);
            if(vertexPos != null)
            loadAttributeVector(ATTRB_POS, vertexPos,3,GL_ARRAY_BUFFER,GL_STATIC_DRAW);
            if(uv != null){
                loadAttributeVector(ATTRB_UV, uv, 2, GL_ARRAY_BUFFER, GL_STATIC_DRAW);
                hasUVs = true;
            }
            else{
                hasUVs=false;
            }
            if(normals != null)
            loadAttributeVector(ATTRB_NORMAL, normals, 3, GL_ARRAY_BUFFER, GL_STATIC_DRAW);
            vertexCount = vertexOrder.length;
            //unbind the current VAO
            glBindVertexArray(0);
            loadedMeshes.add(this);
        }
    }
    
    
    
    void loadAttributeVector(int attributeId, float[] attribute, int attributeSize, int glType, int glDraw){
        int vboId = glGenBuffers();
        vboIds.add(vboId);
        glBindBuffer(glType,vboId);
        
        FloatBuffer bufferedAttribute = BufferUtils.createFloatBuffer(attribute.length);
        bufferedAttribute.put(attribute);
        bufferedAttribute.flip();
        glBufferData(glType, bufferedAttribute, glDraw);
        
        // attributeId dimensions type isNormalized distanceBetweenVerticies offset
        glVertexAttribPointer(attributeId, attributeSize, GL_FLOAT, false, 0, 0);
        
        //unbind vbo
        glBindBuffer(attributeId,0);
        
    }
    
    void loadAttributeInt(int[] attribute, int glType,int glDraw){
        int vboId = glGenBuffers();
        vboIds.add(vboId);
        glBindBuffer(glType,vboId);
        
        IntBuffer bufferedAttribute = BufferUtils.createIntBuffer(attribute.length);
        bufferedAttribute.put(attribute);
        bufferedAttribute.flip();
        glBufferData(glType, bufferedAttribute, glDraw);
    }
    
    
    public void bindVAO() {
        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(ATTRB_POS);
        if(hasUVs)
        glEnableVertexAttribArray(ATTRB_UV);
        glEnableVertexAttribArray(ATTRB_NORMAL);
    }
    
    public void unbindVAO(){
        glDisableVertexAttribArray(ATTRB_POS);
        if(hasUVs)
        glDisableVertexAttribArray(ATTRB_UV);
        glDisableVertexAttribArray(ATTRB_NORMAL);
        glBindVertexArray(0);  
    }
    
    public int getVertexCount(){
        return vertexCount;
    }
    
    public void loadFromObj(String fileName){
        if (!hasVAO) {
            return;
        }
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(new File("assets/models/" + fileName + ".obj"));
        } catch (FileNotFoundException ex) {
            System.err.println("File " + fileName + ".obj was not found");
            System.exit(-1);
        }
        BufferedReader reader = new BufferedReader(fileReader);
        String lineIn;
        List<Vertex> vertices = new ArrayList<Vertex>();
        List<Vec2> uvs = new ArrayList<Vec2>();
        List<Vec3> normals = new ArrayList<Vec3>();
        List<Integer> faces = new ArrayList<Integer>();

        try {
            while (true) {
                lineIn = reader.readLine();
                String[] contents = lineIn.split(" ");
                //vertex
                if (lineIn.startsWith("v ")) {
                    vertices.add(new Vertex(vertices.size(),new Vec3(
                            Float.parseFloat(contents[1]),
                            Float.parseFloat(contents[2]),
                            Float.parseFloat(contents[3])
                    )));
                } //texture coordinate (uv)
                else if (lineIn.startsWith("vt ")) {
                    uvs.add(new Vec2(
                            Float.parseFloat(contents[1]),
                            Float.parseFloat(contents[2])
                    ));
                } //normal
                else if (lineIn.startsWith("vn ")) {
                    normals.add(new Vec3(
                            Float.parseFloat(contents[1]),
                            Float.parseFloat(contents[2]),
                            Float.parseFloat(contents[3])
                    ));
                } //face
                else if (lineIn.startsWith("f ")) {
                    break;
                }
            }
            
            //start a separate face section
            
            while (lineIn != null ) {
                if (!lineIn.startsWith("f ")) {
                    lineIn = reader.readLine();
                    continue;
                }
                String[][] contents = new String[4][];
                contents[0] = lineIn.split(" ");
                contents[1] = contents[0][1].split("/");
                contents[2] = contents[0][2].split("/");
                contents[3] = contents[0][3].split("/");


                //put the data of each vertex in the correct position
                for (int i = 1; i < 4; i++) {
                    int index = Integer.parseInt(contents[i][0]) - 1;
                    Vertex vertex = vertices.get(index);
                        int uvIndex=0;
                    if(!contents[i][1].equals("")){
                        uvIndex = Integer.parseInt(contents[i][1])-1;
                    }
                    int normalIndex = Integer.parseInt(contents[i][2])-1;
                    if(!vertex.isInitialized()){
                        vertex.uvIndex=uvIndex;
                        vertex.normalIndex = normalIndex;
                        faces.add(index);
                    }
                    //handles vertex that has been initiated
                    else{
                        recursiveDuplicateHandle(vertex, uvIndex, normalIndex, faces, vertices);
                    }
                }

                lineIn = reader.readLine();
            }
            reader.close();
        } catch (IOException ex) {
            System.err.print("Failed loading " + fileName + ".obj");
            System.exit(-1);
        }
        //remove vertices that are not used by assigning to nonexistant value
        for(Vertex temp:vertices)
            if(!temp.isInitialized()){
                temp.uvIndex=0;
                temp.normalIndex=0;
            }
        float[] vertexData = new float[vertices.size()*3];
        float[] uvData = new float[vertices.size()*2];
        float[] normalData = new float[vertices.size()*3];
        float furthest = 0;
        
        for(int i=0;i<vertices.size();i++){
            Vertex vertex = vertices.get(i);
            if(vertex.length>furthest){
                furthest = vertex.length;
            }
            vertexData[i*3]=vertex.pos.x;
            vertexData[i*3+1]=vertex.pos.y;
            vertexData[i*3+2]=vertex.pos.z;
            if(uvs.isEmpty()){
                uvData[i*2]=0;
                uvData[i*2+1]=0;
            }
            else{
                uvData[i*2]=uvs.get(vertex.uvIndex).x;
                uvData[i*2+1]=uvs.get(vertex.uvIndex).y;
            }
            normalData[i*3]=normals.get(vertex.normalIndex).x;
            normalData[i*3+1]=normals.get(vertex.normalIndex).y;
            normalData[i*3+2]=normals.get(vertex.normalIndex).z;
        }
        
        //place faces into an array
        int[] faceData = new int[faces.size()];
        for(int i=0;i<faceData.length;i++){
            faceData[i]=faces.get(i);
        }
        
        size = furthest;
        load(vertexData, faceData, uvData, normalData);
        
    }

    private void recursiveDuplicateHandle(Vertex vertex, int uvIndex, int normalIndex, List<Integer> faces, List<Vertex> vertices) {
        //if this vertex data matches that passed, add it to the face data
        if (vertex.normalIndex == normalIndex && vertex.uvIndex == uvIndex) {
            faces.add(vertex.index);
        } else {
            //otherwise there is a duplicate
            Vertex duplicate = vertex.duplicate;
            //if this vertex has a duplicate already, call this function again
            if (duplicate != null) {
                recursiveDuplicateHandle(duplicate, uvIndex, normalIndex, faces, vertices);
            }
            //this vertex does not have a duplicate yet, create one
            else{
                Vertex newDuplicate = new Vertex(vertices.size(),vertex.pos);
                newDuplicate.uvIndex = uvIndex;
                newDuplicate.normalIndex = normalIndex;
                vertices.add(newDuplicate);
                faces.add(newDuplicate.index);
                vertex.duplicate=newDuplicate;
            }
        }
    }
    
    
    
    private class Vertex{
        Vec3 pos;
        int uvIndex;
        int normalIndex;
        Vertex duplicate;
        int index;
        float length;
        
        public Vertex(int index, Vec3 pos){
            uvIndex = -1;
            normalIndex=-1;
            duplicate = null;
            this.index = index;
            this.pos=pos;
            length = pos.length();
        }
        
        public boolean isInitialized(){
            return uvIndex !=-1 && normalIndex!=-1;
        }
    }
    
     public static float[] createFlatNormals(float[] points,int[] order){
        float[] normals = new float[order.length*3];
            Vec3 a = new Vec3();
            Vec3 b = new Vec3();
            Vec3 c = new Vec3();
        for(int i=0;i<order.length;i+=3){
            a.set(points[3 * order[i]], points[3 * order[i] + 1], points[3 * order[i] + 2]);
            b.set(points[3 * order[i + 1]], points[3 * order[i + 1] + 1], points[3 * order[i + 1] + 2]);
            c.set(points[3 * order[i + 2]], points[3 * order[i + 2] + 1], points[3 * order[i + 2] + 2]);
            
            a.multiply(-1);
            b.add(a);
            c.add(a);
            a=b.cross(c);
            
            normals[3*order[i]]=a.x;
            normals[3*order[i]+1]=a.y;
            normals[3*order[i]+2]=a.z;
        }
        return normals; 
    }
    
    public static float[] createSmoothNormals(float[] points, int[] order){
        //stores the normals in vertex order
        float[] normals = new float[points.length];
        //stores the number of normals for a vertex
        int[] normal_count = new int[normals.length/3];
        
            Vec3 a = new Vec3();
            Vec3 b = new Vec3();
            Vec3 c = new Vec3();
        //find the normal of each triangle
        for(int i=0;i<order.length;i+=3){
            a.set(points[3 * order[i]], points[3 * order[i] + 1], points[3 * order[i] + 2]);
            b.set(points[3 * order[i + 1]], points[3 * order[i + 1] + 1], points[3 * order[i + 1] + 2]);
            c.set(points[3 * order[i + 2]], points[3 * order[i + 2] + 1], points[3 * order[i + 2] + 2]);
            
            a.multiply(-1);
            b.add(a);
            c.add(a);
            a=b.cross(c);
            a.normalize();
            
            //store the normal of this triangle in each of the individual point's normals
            //add the normals, they will be divided after the loop completes
            
            //point a
            normals[3*order[i]]+=a.x;
            normals[3*order[i]+1]+=a.y;
            normals[3*order[i]+2]+=a.z;
            //point b
            normals[3*order[i+1]]+=a.x;
            normals[3*order[i+1]+1]+=a.y;
            normals[3*order[i+1]+2]+=a.z;
            //point c
            normals[3*order[i+2]]+=a.x;
            normals[3*order[i+2]+1]+=a.y;
            normals[3*order[i+2]+2]+=a.z;
            
            //increment each points normal count
            normal_count[order[i]]++;
            normal_count[order[i+1]]++;
            normal_count[order[i+2]]++;
        }
        
        //divide the normals
        int count = 0;
        for (int i = 0; i < normals.length; i += 3) {
            count = normal_count[i / 3];
            normals[i] /= count;
            normals[i + 1] /= count;
            normals[i + 2] /= count;
        }
        return normals; 
    }
    
}
