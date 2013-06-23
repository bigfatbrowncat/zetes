#version 150
uniform sampler2D texture;
uniform mat4 matrix;

in vec3 in_vertexPosition;
in vec2 in_textureCoords;
in vec3 in_diffuseColor;

out vec4 diffuseColor;
out vec2 textureCoords;

void main()
{
    gl_Position = matrix * vec4(in_vertexPosition, 1.0);
    //gl_Position.w = 1.0;
    
    diffuseColor.x = abs(in_diffuseColor.x);
    diffuseColor.y = abs(in_diffuseColor.y);
    diffuseColor.z = abs(in_diffuseColor.z);
    diffuseColor.w = 1.0;
    
    textureCoords = in_textureCoords;
}
