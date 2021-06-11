#version 400 core

in vec3 pos;

out vec3 pos_out;
out vec3 normal_out;

uniform mat4 transformationMatrix;
uniform mat4 cameraMatrix;
uniform mat4 projectionMatrix;
uniform vec3 lightPosition;

void main(void){
    gl_Position = projectionMatrix*cameraMatrix*transformationMatrix*vec4(pos,1.0);
    pos_out = pos;

}