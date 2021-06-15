#version 400 core

in vec2 uv_out;

out vec4 colour;

uniform sampler2D textureImg;
uniform vec3 lightColour;

void main(void){
    
    colour = texture(textureImg,uv_out);

}