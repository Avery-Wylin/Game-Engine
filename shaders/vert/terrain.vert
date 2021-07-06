#version 400 core

in vec3 pos;
in vec3 normal;
in vec3 terrainData;

out vec3 toLight[4];
out vec2 uv;
out float fogFactor;
out vec2  uvYZ;
out vec2  uvXZ;
out vec2  uvXY;
out vec3 terrainDataFrag;
out vec3 actualNormal;
out vec3 skyVector;


uniform mat4 transformationMatrix;
uniform mat4 cameraMatrix;
uniform mat4 projectionMatrix;
uniform vec3 lightPosition[4];
uniform float fogDensity;
uniform float fogGradient;



void main(void){
    

    vec4 worldSpace = transformationMatrix*vec4(pos,1.0);
    vec4 cameraSpace = cameraMatrix*worldSpace;
    gl_Position = projectionMatrix*cameraSpace;

//clipping plane
    gl_ClipDistance[0] = dot(worldSpace,vec4(0,-1,0,60));

//normal map and normals
    actualNormal = normalize((transformationMatrix*vec4(normal,0)).xyz);
    vec3 t = normalize(vec3(actualNormal.y,-actualNormal.x,0));
    mat3 tbn = transpose(mat3(t, cross(actualNormal,t),actualNormal));

    skyVector = tbn*vec3(0,1,0);
    
//lighting
    for(int i=0;i<4;i++){
    toLight[i] = tbn*(lightPosition[i]-worldSpace.xyz);
    }

    float depth = length(cameraSpace.xyz);
    fogFactor = exp(-pow((depth*fogDensity),fogGradient));
    fogFactor = clamp(fogFactor,0.0,1.0);
    
    uvYZ= worldSpace.yz/4;
    uvXZ= worldSpace.xz/4;
    uvXY= worldSpace.xy/4;

    
    terrainDataFrag = terrainData;
}