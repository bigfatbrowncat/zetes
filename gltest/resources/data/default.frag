#version 150
uniform sampler2D texture;

in vec2 textureCoords;
in vec4 vertex;
in vec3 normal;
in vec4 lightPosition;

out vec4 color;

void main()
{
    vec4 tex = texture(texture, textureCoords.st);
    
    vec4 lightV = (lightPosition - vertex) / abs(lightPosition - vertex);
    float ndl = max(dot(normal, lightV.xyz), 0.0);
    color = tex * (ndl * 0.5 + 0.5);

}