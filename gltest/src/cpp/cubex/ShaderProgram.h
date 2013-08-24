/*
 * ShaderProgram.h
 *
 *  Created on: May 21, 2013
 *      Author: imizus
 */

#ifndef SHADERPROGRAM_H_
#define SHADERPROGRAM_H_

#include <string>
#include <map>

#include "GLObject.h"
#include "MeshBuffer.h"

using namespace std;

namespace cubex
{
	class Texture;

	class ShaderProgram : public GLObject
	{
	private:
		// This class isn't copyable
		ShaderProgram operator = (const ShaderProgram& other);
		ShaderProgram(const ShaderProgram& other);

		map<Texture*, string> linkedTextures;
		map<MeshBuffer*, string> linkedMeshBuffers;
	private:
		GLuint programID;
	public:
		ShaderProgram(const string& vertexShaderCode, const string& fragmentShaderCode);
		static ShaderProgram* fromFiles(const string& vertexShaderFileName, const string& fragmentShaderFileName);

		GLint getAttribLocation(const string& attribName) const;
		GLint getUniformLocation(const string& uniformName) const;

		void linkTexture(Texture& texture, const string& sampler2DShaderVariableName);
		void unlinkTexture(Texture& texture);

		void linkMeshBuffer(MeshBuffer& meshBuffer, const string& meshBufferShaderVariableName);
		void unlinkMeshBuffer(MeshBuffer& meshBuffer);

		void process() const;
		virtual ~ShaderProgram();
	};
}

#endif /* SHADERPROGRAM_H_ */
