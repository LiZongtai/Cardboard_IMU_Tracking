#version 300 es
layout (location = 0) in vec4 aPosition;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
//uniform mat4 rotation;

out vec4 vColor;
void main() {
    gl_Position  =   projection * view *  model * aPosition;
    vColor = vec4(0.6, 1.0, 0.2, 0.9);
}