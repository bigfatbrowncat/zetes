#version 150
uniform sampler2D uni_texture;

in vec2 textureCoords;
in vec4 vertex;
in vec3 normal;

out vec4 color;

void main()
{
    vec4 tex = texture(uni_texture, textureCoords.st);

    color = vec4(1.0 - tex.r, 1.0 - tex.g, 1.0 - tex.b, tex.a);
}