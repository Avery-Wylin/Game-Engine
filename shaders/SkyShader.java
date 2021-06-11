package shaders;

import math.Vec3;
import meshes.Mesh;

public class SkyShader extends GLSLShader {

    protected int zenith,horizon,albedo; 
    
    public SkyShader() {
        super("sky");
    }

    @Override
    protected void bindAttributes() {
        super.bindAttribute(Mesh.ATTRB_POS, "pos");
    }
    
    @Override
    protected void getAllUniformLocations(){
        super.getAllUniformLocations();
        zenith = getUniformLocation("zenith");
        horizon = getUniformLocation("horizon");
        albedo = getUniformLocation("albedo");
    }
    
    public void loadZenith(Vec3 zenith){
        loadUniformVector(this.zenith, zenith);
    }
    
    public void loadHorizon(Vec3 horizon){
        loadUniformVector(this.horizon, horizon);
    }
    
    public void loadAlbedo(Vec3 albedo){
        loadUniformVector(this.albedo, albedo);
    }
    
}
