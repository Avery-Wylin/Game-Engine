package shaders;

import math.Vec3;
import meshes.Mesh;

public class LightShader extends GLSLShader {
    public static final int MAX_LIGHTS = 4;
    
    protected int
            specular,
            shine,
            ambient,
            diffuseColour,
            fogGradient,
            fogDensity,
            zenith,
            horizon,
            albedo;
    protected int[] lightColour,lightPosition,attenuation;
    
    public LightShader(String shaderName) {
        super(shaderName);
    }
    

    @Override
    protected void bindAttributes() {
        super.bindAttribute(Mesh.ATTRB_POS, "pos");
        super.bindAttribute(Mesh.ATTRB_UV, "uv");
        super.bindAttribute(Mesh.ATTRB_NORMAL,"normal");
    }
    
    @Override
    protected void getAllUniformLocations(){
        super.getAllUniformLocations();
        lightColour = new int[MAX_LIGHTS];
        lightPosition = new int[MAX_LIGHTS];
        attenuation = new int[MAX_LIGHTS];
        for(int i=0;i<MAX_LIGHTS;i++){
            lightColour[i] = getUniformLocation("lightColour["+i+"]");
            lightPosition[i] = getUniformLocation("lightPosition["+i+"]");
            attenuation[i] = getUniformLocation("attenuation["+i+"]");
        }
        specular = getUniformLocation("specular");
        shine = getUniformLocation("shine");
        ambient = getUniformLocation("ambient");
        diffuseColour = getUniformLocation("diffuseColour");
        fogDensity = getUniformLocation("fogDensity");
        fogGradient = getUniformLocation("fogGradient");
        zenith = getUniformLocation("zenith");
        horizon = getUniformLocation("horizon");
        albedo = getUniformLocation("albedo");
    }
    
    public void loadLight(Light light,int slot){
        if(slot<MAX_LIGHTS){
            super.loadUniformVector(lightPosition[slot], light.pos);
            super.loadUniformVector(lightColour[slot], light.col);
            super.loadUniformVector(attenuation[slot], light.attenuation);
        }
    }
    
    public void loadSpecular(float specular){
        super.loadUniformFloat(this.specular, specular);
    }
    
    public void loadShine(float shine){
        super.loadUniformFloat(this.shine, shine);
    }
    
    public void loadAmbient(Vec3 ambient){
        super.loadUniformVector(this.ambient, ambient);
    }
    
    public void loadDiffuseColour(Vec3 diffuseColour){
        super.loadUniformVector(this.diffuseColour, diffuseColour);
    }
    
    public void loadFogSettings(float density, float gradient){
        super.loadUniformFloat(fogDensity, density);
        super.loadUniformFloat(fogGradient, gradient);
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
