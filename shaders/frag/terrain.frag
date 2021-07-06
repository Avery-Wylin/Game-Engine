#version 400 core


in vec3 toLight[4];
in float fogFactor;
in vec2  uvYZ;
in vec2  uvXZ;
in vec2  uvXY;
in vec3 terrainDataFrag;
in vec3 actualNormal;
in vec3 skyVector;

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
    
    if(fogFactor<.05){
        colour.xyz = horizon;
        return;
    }


    vec3 combinedDiffuse = vec3(0,0,0);
    vec3 combinedSpecular = vec3(0,0,0);
     vec3 topTextureColour = texture(textureTop,vec2(uvXZ.x,-uvXZ.y)).rgb;
    // topTextureColour = texture(textureTop,vec2(uvXY.x,-uvXY.y)).rgb;
    //vec3 topTextureColour = texture(textureTop,vec2(-uvYZ.x,uvYZ.y)).rgb;
    vec3 topColour = vec3(.7,.7,.7);
    //topTextureColour.r*vec3(.8,.6,.4)*terrainDataFrag.x+
    //topTextureColour.g*vec3(.9,.8,.6)*terrainDataFrag.y+
    //topTextureColour.b*vec3(1,1,1)*terrainDataFrag.z;
    vec3 mappedNormal = normalize(topTextureColour.rgb*2.0-1.0);

    vec3 textureColour = vec3(0,0,0);
    textureColour =  mix(textureColour,texture(textureSide,vec2(uvYZ)).rgb,abs(actualNormal.x));
    textureColour =  mix(textureColour,texture(textureSide,vec2(uvXY)).rgb,abs(actualNormal.z));
    textureColour =  mix(textureColour,topColour,round(pow(abs(actualNormal.y),4)));

    
    
//light shading
    for(int i=0;i<4;i++){
        if(lightColour[i].x==0 && lightColour[i].y==0 && lightColour[i].z==0)
            continue;
        float distLight = length(toLight[i]);
        float attenuationFactor = attenuation[i].x+(attenuation[i].y*distLight)+(attenuation[i].z*distLight*distLight);
        vec3 unitToLight = toLight[i]/distLight;
        float rawDiffuse = dot(mappedNormal,unitToLight);
        combinedDiffuse += max(rawDiffuse,0)*textureColour* (lightColour[i]/attenuationFactor);
    }
    
//world shading
    vec3 worldLight = horizon;
    worldLight = mix(worldLight, zenith,dot(mappedNormal,skyVector));
    worldLight = mix(worldLight,albedo,dot(mappedNormal,-skyVector));
    colour = vec4(combinedDiffuse,1)+vec4(worldLight,1.0)/4;
    colour = mix(vec4(horizon,1.0),colour,fogFactor);
    //colour = vec4(worldLight,1);
    
}