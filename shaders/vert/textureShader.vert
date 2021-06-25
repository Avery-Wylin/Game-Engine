#version 400 core

in vec3 pos;
in vec2 uv;

out vec2 uvCoord;

uniform mat4 transformationMatrix;
uniform mat4 cameraMatrix;
uniform mat4 projectionMatrix;
uniform vec3 lightPosition;
uniform float resolution;

void main(void){


    gl_Position = vec4(pos,1.0);
    uvCoord=uv;

}