package meshes;

import static meshes.Mesh.ATTRB_NORMAL;
import static meshes.Mesh.ATTRB_POS;
import static meshes.Mesh.ATTRB_UV;
import static org.lwjgl.opengl.GL30.*;

/**
 * Mesh Specifically designed for terrain information.
 */
public class TerrainMesh extends Mesh{
    
    public static final int ATTRB_TERRAIN=3;
    public static final int TERRAIN_VECTOR_SIZE=3;
            
    protected int terrainDataVBO;
    
    @Override
    protected void createVBOs(){
        posVBO=glGenBuffers();
        normalVBO = glGenBuffers();
        orderVBO = glGenBuffers();
        //for terrain
        terrainDataVBO = glGenBuffers();
    }
    
    @Override
    protected void deleteVBOs(){
         glDeleteBuffers(posVBO);
         glDeleteBuffers(normalVBO);
         glDeleteBuffers(orderVBO);
         //for terrain
         glDeleteBuffers(terrainDataVBO);
    }
    
    public void load(float[] vertexPos, int[] vertexOrder,float[] terrainData) {
        if (!hasVAO) {//create VAO if one was not already made for this mesh
            createVAO();
            loadedMeshes.add(this);
        }
        //bind the VAO
        glBindVertexArray(vaoId);
        
        if (vertexOrder != null) {//load vertex order if available
            loadAttributeInt(orderVBO,vertexOrder, GL_ELEMENT_ARRAY_BUFFER, GL_DYNAMIC_DRAW);
        }
        if (vertexPos != null) {//load vertex position if available
            loadAttributeVectorf(posVBO,ATTRB_POS, vertexPos, 3, GL_ARRAY_BUFFER, GL_DYNAMIC_DRAW);
        }
        if(vertexPos!=null || vertexOrder!=null){//generate normals if either position or order was loaded
            loadAttributeVectorf(normalVBO,ATTRB_NORMAL, createSmoothNormals(vertexPos, vertexOrder), 3, GL_ARRAY_BUFFER,GL_DYNAMIC_DRAW );
        }
        if(terrainData!=null){//load terrain data if available
            loadAttributeVectorf(terrainDataVBO, ATTRB_TERRAIN, terrainData, TERRAIN_VECTOR_SIZE, GL_ARRAY_BUFFER, GL_DYNAMIC_DRAW);
        }
        
        //terrain does not have uv coords
        hasUVs=false;
        
        //set vertex length
        vertexCount = vertexOrder.length;
        
        //unbind the current VAO
        glBindVertexArray(0);
    }
    
    @Override
     public void bindVAO() {
        glBindVertexArray(vaoId);
        glEnableVertexAttribArray(ATTRB_POS);
        glEnableVertexAttribArray(ATTRB_NORMAL);
        glEnableVertexAttribArray(ATTRB_TERRAIN);
    }
    
    @Override
    public void unbindVAO(){
        glDisableVertexAttribArray(ATTRB_POS);
        glDisableVertexAttribArray(ATTRB_NORMAL);
        glDisableVertexAttribArray(ATTRB_TERRAIN);
        glBindVertexArray(0);  
    }
    
}
