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
		GLuint programID;
	public:
		ShaderProgram(const string& vertexShaderCode, const string& fragmentShaderCode);
		int getAttribLocation(const string& attribName);
		int getUniformLocation(const string& uniformName);

		void use() { glUseProgram(programID); }
		virtual ~ShaderProgram();
	};
}

#endif /* SHADERPROGRAM_H_ */
