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
	/*// Creating the buffer data
	buffer = new float[8 * mesh.getVertices().size()];
	for (int i = 0; i < mesh.getVertices().size(); i++)
	{
		buffer[8 * i + 0] = mesh.get
	}*/

	// Creating a VAO (Vertex Array Object)
	glGenVertexArrays(1, &VertexArrayID);

	// Binding the VAO
	glBindVertexArray(VertexArrayID);

	// Generate 1 buffer, put the resulting identifier in vertexbuffer
	glGenBuffers(1, &vertexBufferObject);

	// The following commands will talk about our 'vertexbuffer' buffer
	glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject);

	// Give our vertices to OpenGL.
	glBufferData(GL_ARRAY_BUFFER, (long)sizeof(GLfloat) * 8 * 3, buffer, GL_STATIC_DRAW);

}

void MeshBuffer::bind()
{
	glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject);
}

MeshBuffer::~MeshBuffer() {
	// TODO Auto-generated destructor stub
}

} /* namespace cubex */
