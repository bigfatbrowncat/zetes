#version 150
uniform sampler2D uni_texture;

in vec2 textureCoords;
in vec4 vertex;
in vec3 normal;
in vec4 lightPosition;

out vec4 color;

void main()
{
	float brightness = 2.0;
	float ambientBrightness = 0.5;

    vec4 tex = texture(uni_texture, textureCoords.st);
    
    vec4 lightV =  normalize(lightPosition - vertex);
    float ndl = max(dot(normal, lightV.xyz), 0.0);
    
    color = tex * (ndl * brightness + ambientBrightness);
}