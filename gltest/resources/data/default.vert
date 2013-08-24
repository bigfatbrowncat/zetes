#version 150

in vec3 in_mesh_vertexPosition;
in vec2 in_mesh_textureCoords;
in vec3 in_mesh_normal;

uniform mat4 uni_matrix;
uniform mat3 uni_normalMatrix;
uniform vec3 uni_lightPosition;

out vec3 normal;
out vec2 textureCoords;
out vec4 vertex;
out vec4 lightPosition;

void main()
{
    gl_Position = uni_matrix * vec4(in_mesh_vertexPosition, 1.0);

    lightPosition = uni_matrix * vec4(uni_lightPosition, 1.0);
    vertex = gl_Position;
    normal = uni_normalMatrix * in_mesh_normal;
    textureCoords = in_mesh_textureCoords;
}
