/*
 * MeshBuffer.cpp
 *
 *  Created on: 18.06.2013
 *      Author: il
 */

#include <GL3/gl3w.h>

#include "MeshBuffer.h"

namespace cubex {

MeshBuffer::MeshBuffer(const Mesh &mesh)
{
	// Creating the buffer data for tri faces

	printf("Extracting data...\n");fflush(stdout);
	verticesCount = 3 * mesh.getFaces3().size();

	printf("A");fflush(stdout);
	buffer = new float[8 * 3 * mesh.getFaces3().size()];
	int index = 0;

	printf("B");fflush(stdout);
	const glm::vec3* vertices = &(mesh.getVertices()[0]);
	const glm::vec3* normals = &(mesh.getNormals()[0]);
	const glm::vec2* textureCoords = &(mesh.getTextureCoords()[0]);

	printf("C");fflush(stdout);

	for (int k = 0; k < mesh.getFaces3().size(); k++)
	{
		Face3 face = mesh.getFaces3()[k];
		printf("D");fflush(stdout);

		glm::vec3 vertex1 = vertices[face.vertexIndex1];
		glm::vec3 vertex2 = vertices[face.vertexIndex2];
		glm::vec3 vertex3 = vertices[face.vertexIndex3];

		glm::vec3 normal1 = glm::vec3(0.0f);
		glm::vec3 normal2 = glm::vec3(0.0f);
		glm::vec3 normal3 = glm::vec3(0.0f);

		if (face.containsNormals)
		{
			normal1 = normals[face.normalIndex1];
			normal2 = normals[face.normalIndex2];
			normal3 = normals[face.normalIndex3];
		}
		else
		{
			// Calculating normals. NOT SMOOTH.

			normal1 = glm::normalize(glm::cross(vertex2 - vertex1, vertex3 - vertex2));
			normal2 = normal1;
			normal3 = normal1;
		}

		glm::vec2 textureCoord1 = glm::vec2(0.0f);
		glm::vec2 textureCoord2 = glm::vec2(0.0f);
		glm::vec2 textureCoord3 = glm::vec2(0.0f);

		if (face.containsTextureCoords)
		{
			textureCoord1 = textureCoords[face.textureCoordIndex1];
			textureCoord2 = textureCoords[face.textureCoordIndex2];
			textureCoord3 = textureCoords[face.textureCoordIndex3];
		}

		// First vertex

		buffer[index++] = vertex1.x;
		buffer[index++] = vertex1.y;
		buffer[index++] = vertex1.z;

		buffer[index++] = normal1.x;
		buffer[index++] = normal1.y;
		buffer[index++] = normal1.z;

		buffer[index++] = textureCoord1.x;
		buffer[index++] = textureCoord1.y;

		// Second vertex

		buffer[index++] = vertex2.x;
		buffer[index++] = vertex2.y;
		buffer[index++] = vertex2.z;

		buffer[index++] = normal2.x;
		buffer[index++] = normal2.y;
		buffer[index++] = normal2.z;

		buffer[index++] = textureCoord2.x;
		buffer[index++] = textureCoord2.y;

		// Third vertex

		buffer[index++] = vertex3.x;
		buffer[index++] = vertex3.y;
		buffer[index++] = vertex3.z;

		buffer[index++] = normal3.x;
		buffer[index++] = normal3.y;
		buffer[index++] = normal3.z;

		buffer[index++] = textureCoord3.x;
		buffer[index++] = textureCoord3.y;

		printf("*");fflush(stdout);
	}

	printf("Loading data into video memory...\n");fflush(stdout);

	// Creating a VAO (Vertex Array Object)
	glGenVertexArrays(1, &VertexArrayID);

	// Binding the VAO
	glBindVertexArray(VertexArrayID);

	// Generate 1 buffer, put the resulting identifier in vertexbuffer
	glGenBuffers(1, &vertexBufferObject);

	// The following commands will talk about our 'vertexbuffer' buffer
	glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject);

	// Give our vertices to OpenGL.
	glBufferData(GL_ARRAY_BUFFER, (long)sizeof(GLfloat) * 8 * verticesCount, buffer, GL_STATIC_DRAW);

	printf("Loaded\n");fflush(stdout);

}

void MeshBuffer::draw()
{
	glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject);
	glDrawArrays(GL_TRIANGLES, 0, verticesCount);
}

MeshBuffer::~MeshBuffer()
{
	glDeleteBuffers(1, &vertexBufferObject);
	glDeleteVertexArrays(1, &VertexArrayID);
	delete [] buffer;
}

} /* namespace cubex */
