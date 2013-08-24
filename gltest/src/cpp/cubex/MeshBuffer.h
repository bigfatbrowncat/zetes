/*
 * MeshBuffer.h
 *
 *  Created on: 18.06.2013
 *      Author: il
 */

#ifndef MESHBUFFER_H_
#define MESHBUFFER_H_

#include "GLObject.h"
#include "Mesh.h"

namespace cubex
{

	class MeshBuffer : public GLObject
	{
		friend class ShaderProgram;
	private:
		GLuint VertexArrayID;
		GLuint vertexBufferObject;

		float* buffer;
		int verticesCount;
	protected:
		int getVertexArrayID() const { return VertexArrayID; }
		void draw(GLint vertexVec3ShaderVariableAttrib, GLint normalVec3ShaderVariableAttrib, GLint textureVec2ShaderVariableAttrib) const;

	public:
		MeshBuffer(const Mesh &mesh);


		virtual ~MeshBuffer();
	};

}

#endif
