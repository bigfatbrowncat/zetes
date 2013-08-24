#version 150

in vec3 in_mesh_vertexPosition;
in vec2 in_mesh_textureCoords;
in vec3 in_mesh_normal;

out vec3 normal;
out vec2 textureCoords;
out vec4 vertex;

void main()
{
    gl_Position = vec4(in_mesh_vertexPosition, 1.0);
    vertex = gl_Position;
    textureCoords = in_mesh_textureCoords;
    normal = in_mesh_normal;
}
