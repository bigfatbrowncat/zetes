/*
 * MeshBuffer.h
 *
 *  Created on: 18.06.2013
 *      Author: il
 */

#ifndef MESHBUFFER_H_
#define MESHBUFFER_H_

#include "ShaderProgram.h"

#include "Mesh.h"

namespace cubex
{

	class MeshBuffer : public GLObject
	{
	private:
		GLuint VertexArrayID;
		GLuint vertexBufferObject;

		const ShaderProgram* shaderProgram;

		GLint vertexVec3ShaderVariableAttrib;
		GLint normalVec3ShaderVariableAttrib;
		GLint textureVec2ShaderVariableAttrib;

		string vertexVec3ShaderVariableName;
		string normalVec3ShaderVariableName;
		string textureVec2ShaderVariableName;

		float* buffer;
		int verticesCount;
	public:
		MeshBuffer(const Mesh &mesh);

		void connectToShaderProgram(const ShaderProgram* shaderProgram,
		                            const string& vertexVec3ShaderVariableName,
		                            const string& normalVec3ShaderVariableName,
		                            const string& textureVec2ShaderVariableName);
		void draw();

		virtual ~MeshBuffer();
	};

}

#endif
