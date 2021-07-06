package shaders;

import meshes.Mesh;
import org.joml.Vector3f;

public class SkyShader extends GLSLShader {

    protected int zenith,horizon,albedo; 
    
    public SkyShader() {
        super("sky");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(Mesh.ATTRB_POS, "pos");
        super.bindAttribute(Mesh.ATTRB_POS, "normal");
    }
    
    @Override
    protected void getAllUniformLocations(){
        super.getAllUniformLocations();
        zenith = getUniformLocation("zenith");
        horizon = getUniformLocation("horizon");
        albedo = getUniformLocation("albedo");
    }
    
    public void loadZenith(Vector3f zenith){
        loadUniformVector3f(this.zenith, zenith);
    }
    
    public void loadHorizon(Vector3f horizon){
        loadUniformVector3f(this.horizon, horizon);
    }
    
    public void loadAlbedo(Vector3f albedo){
        loadUniformVector3f(this.albedo, albedo);
    }
    
}
