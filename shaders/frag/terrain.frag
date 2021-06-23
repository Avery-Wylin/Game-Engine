#version 400 core


in vec3 surfaceNormal;
in vec3 toLight[4];
in vec2 uv;
in vec3 toCamera;
in float fogFactor;
in vec3 vertexColour;
in vec2  uvYZ;
in vec2  uvXZ;
in vec2  uvXY;

out vec4 colour;

uniform sampler2D textureTop;
uniform sampler2D textureSide;
uniform vec3 lightColour[4];
uniform vec3 attenuation[4];
uniform vec3 diffuseColour;
uniform vec3 zenith;
uniform vec3 horizon;
uniform vec3 albedo;

void main(void){
    
    vec3 unitToCamera = normalize(toCamera);

    vec3 combinedDiffuse = vec3(0,0,0);
    vec3 combinedSpecular = vec3(0,0,0);
    vec3 textureColour = vec3(0,0,0);
    textureColour =  mix(textureColour,texture(textureSide,vec2(uvYZ)).xyz,abs(surfaceNormal.x));
    textureColour =  mix(textureColour,texture(textureSide,vec2(uvXY)).xyz,abs(surfaceNormal.z));
    textureColour =  mix(textureColour,texture(textureTop,vec2(uvXZ)).xyz,round(pow(abs(surfaceNormal.y),4)));
    if(fogFactor<.05){
        colour.xyz = horizon;
        return;
    }
    for(int i=0;i<4;i++){
        if(lightColour[i].x==0 && lightColour[i].y==0 && lightColour[i].z==0)
            continue;
        float distLight = length(toLight[i]);
        float attenuationFactor = attenuation[i].x+(attenuation[i].y*distLight)+(attenuation[i].z*distLight*distLight);
        vec3 unitToLight = toLight[i]/distLight;
        float rawDiffuse = dot(surfaceNormal,unitToLight);
        combinedDiffuse += max(rawDiffuse,0)*textureColour* (lightColour[i]/attenuationFactor);
    }

    vec3 worldLight = horizon;
    worldLight = mix(worldLight,zenith,dot(surfaceNormal,vec3(0,1,0)));
    worldLight = mix(worldLight,albedo,dot(surfaceNormal,vec3(0,-1,0)));
    colour = vec4(combinedDiffuse,1)+vec4(worldLight,1.0)/4;
    colour = mix(vec4(horizon,1.0),colour,fogFactor);
}