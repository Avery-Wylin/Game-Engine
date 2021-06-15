#version 400 core

in vec3 pos_out;

out vec4 colour;

uniform sampler2D textureImg;
uniform vec3 lightColour;
uniform vec3 zenith;
uniform vec3 horizon;
uniform vec3 albedo;

void main(void){

   float y=pos_out.y+2;
   y = clamp(y,0.0,1.0);
   colour = mix(colour,vec4(albedo,1),y);

   y=pos_out.y+1;
   y = clamp(y,0.0,1.0);
   colour = mix(colour,vec4(horizon,1),y);

   y=pos_out.y;
   y = clamp(y,0.0,1.0);
   colour = mix(colour,vec4(zenith,1),y);

}