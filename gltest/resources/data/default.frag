#version 150
uniform sampler2D texture;

in vec2 textureCoords;
in vec4 diffuseColor;

out vec4 color;

void main()
{
    vec4 tex = texture(texture, textureCoords.st);
    //tex.w = 1.0;
    
    color = 0.5 * (tex + diffuseColor);
}