#version 400 core

in vec3 pos;
in vec2 uv;

out vec2 uv_out;

uniform mat4 transformationMatrix;
uniform mat4 cameraMatrix;
uniform mat4 projectionMatrix;
uniform vec3 lightPosition;

void main(void){
    
    gl_Position = projectionMatrix*cameraMatrix*transformationMatrix*vec4(pos,1.0);
    uv_out = uv;

}