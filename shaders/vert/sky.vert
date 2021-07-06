#version 400 core

in vec3 pos;
in vec3 normal;

out vec3 pos_out;
out vec3 surfaceNormal;

uniform mat4 transformationMatrix;
uniform mat4 cameraMatrix;
uniform mat4 projectionMatrix;
uniform vec3 lightPosition;

void main(void){
    gl_Position = projectionMatrix*cameraMatrix*transformationMatrix*vec4(pos,1.0);
    pos_out = pos;
    surfaceNormal = normal;

}