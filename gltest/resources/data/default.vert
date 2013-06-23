#version 150
uniform sampler2D texture;
uniform mat4 matrix;
uniform mat3 normalMatrix;
uniform vec3 in_lightPosition;

in vec3 in_vertexPosition;
in vec2 in_textureCoords;
in vec3 in_normal;

out vec3 normal;
out vec2 textureCoords;
out vec4 vertex;
out vec4 lightPosition;

void main()
{
    gl_Position = matrix * vec4(in_vertexPosition, 1.0);

    lightPosition = /*matrix */ vec4(in_lightPosition, 1.0);
    vertex = gl_Position;
    normal = normalMatrix * in_normal;
    textureCoords = in_textureCoords;
}
