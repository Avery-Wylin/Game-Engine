#version 400 core

in vec3 pos;
in vec2 uvIn;
in vec3 normal;
out vec3 surfaceNormal;
out vec3 toLight[4];
out vec2 uv;
out vec3 toCamera;
out float fogFactor;
out vec3 vertexColour;

uniform mat4 transformationMatrix;
uniform mat4 cameraMatrix;
uniform mat4 projectionMatrix;
uniform vec3 lightPosition[4];
uniform float fogDensity;
uniform float fogGradient;

void main(void){
    
    vec4 objectSpace = transformationMatrix*vec4(pos,1.0);
    vec4 cameraSpace = cameraMatrix*objectSpace;
    gl_Position = projectionMatrix*cameraSpace;
    
    uv = uvIn;
    surfaceNormal = normalize((transformationMatrix*vec4(normal,0.0)).xyz);
    for(int i=0;i<4;i++){
    toLight[i] = lightPosition[i]-objectSpace.xyz;
    }

    toCamera = (inverse(cameraMatrix)*vec4(0.0,0.0,0.0,1.0)).xyz-objectSpace.xyz;

    float depth = length(cameraSpace.xyz);
    fogFactor = exp(-pow((depth*fogDensity),fogGradient));
    fogFactor = clamp(fogFactor,0.0,1.0);
    vertexColour = mix(vec3(0,1,0),vec3(1,1,1),pos.y*10);

}