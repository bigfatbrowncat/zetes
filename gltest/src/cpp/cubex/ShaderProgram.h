/*
 * ShaderProgram.h
 *
 *  Created on: May 21, 2013
 *      Author: imizus
 */

#ifndef SHADERPROGRAM_H_
#define SHADERPROGRAM_H_

#include <string>

using namespace std;

namespace cubex
{
	class ShaderProgram
	{
	private:
		// This class isn't copyable
		ShaderProgram operator = (const ShaderProgram& other);
		ShaderProgram(const ShaderProgram& other);

	private:
		GLuint programID;
	public:
		ShaderProgram(const string& vertexShaderCode, const string& fragmentShaderCode);
		static ShaderProgram* fromFiles(const string& vertexShaderFileName, const string& fragmentShaderFileName);

		GLint getAttribLocation(const string& attribName) const;
		GLint getUniformLocation(const string& uniformName) const;

		void use() const { glUseProgram(programID); }
		virtual ~ShaderProgram();
	};
}

#endif /* SHADERPROGRAM_H_ */
