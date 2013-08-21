#version 150
uniform sampler2D uni_texture;

in vec2 textureCoords;
in vec4 vertex;
in vec3 normal;

out vec4 color;

void main()
{
	vec2 texcoordsDivided = vec2(textureCoords.x * 2, textureCoords.y * 2);
	
    vec4 tex = texture(uni_texture, texcoordsDivided);
	if ((textureCoords.x < 0.5) ^^ (textureCoords.y < 0.5))
	{
    	color = vec4(1.0 - tex.r, 1.0 - tex.g, 1.0 - tex.b, tex.a);
    }
    else
    {
    	color = vec4(tex.r, tex.g, tex.b, tex.a);
    }
}