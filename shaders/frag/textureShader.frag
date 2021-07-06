#version 400 core

in vec2 uvCoord;

out vec4 colour;

uniform vec3 lightColour;
uniform sampler2D imageTexture;
uniform sampler2D depthTexture;
uniform float resolution;

void main(void){
    //float near = .1;
    //float far = 350;
    //float depth = texture(depthTexture,uvCoord).x;
    //depth = (2.0f * near * far) / (far + near - (depth * 2.0f - 1.0f) * (far - near));
    //depth/=far;

    colour = texture(imageTexture,uvCoord);
}