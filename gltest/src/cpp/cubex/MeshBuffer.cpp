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

	verticesCount = 3 * mesh.getFaces3().size();

	buffer = new float[8 * 3 * mesh.getFaces3().size()];
	int index = 0;
	for (int k = 0; k < mesh.getFaces3().size(); k++)
	{
		Face3 face = mesh.getFaces3()[k];

		glm::vec3 vertex1 = mesh.getVertices()[face.vertexIndex1];
		glm::vec3 vertex2 = mesh.getVertices()[face.vertexIndex2];
		glm::vec3 vertex3 = mesh.getVertices()[face.vertexIndex3];

		glm::vec3 normal1 = mesh.getNormals()[face.normalIndex1];
		glm::vec3 normal2 = mesh.getNormals()[face.normalIndex2];
		glm::vec3 normal3 = mesh.getNormals()[face.normalIndex3];

		glm::vec2 textureCoord1 = glm::vec2(0.0f);
		glm::vec2 textureCoord2 = glm::vec2(0.0f);
		glm::vec2 textureCoord3 = glm::vec2(0.0f);

		if (face.containsTextureCoords)
		{
			textureCoord1 = mesh.getTextureCoords()[face.textureCoordIndex1];
			textureCoord2 = mesh.getTextureCoords()[face.textureCoordIndex2];
			textureCoord3 = mesh.getTextureCoords()[face.textureCoordIndex3];
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

	}


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
