/*
 * MeshBuffer.h
 *
 *  Created on: 18.06.2013
 *      Author: il
 */

#ifndef MESHBUFFER_H_
#define MESHBUFFER_H_

#include "Mesh.h"

namespace cubex
{

	class MeshBuffer
	{
	private:
		GLuint VertexArrayID;
		GLuint vertexBufferObject;
		float* buffer;
		int verticesCount;
	public:
		MeshBuffer(const Mesh &mesh);
		virtual ~MeshBuffer();
		void draw();
	};

}

#endif
