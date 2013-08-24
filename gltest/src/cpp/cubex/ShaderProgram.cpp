/*
 * ShaderProgram.cpp
 *
 *  Created on: May 21, 2013
 *      Author: imizus
 */

#include <string.h>

#include <sstream>

#include <GL3/gl3w.h>

#include "ShaderProgram.h"
#include "CubexException.h"
#include "Texture.h"

using namespace std;

namespace cubex
{

	ShaderProgram::ShaderProgram(const string& vertexShaderCode, const string& fragmentShaderCode)
	{
	    // Creating the shaders
	    GLuint vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
	    GLuint fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);

	    GLint result = GL_FALSE;
	    int infoLogMaxLength;
	    int infoLogRealLength;

	    // Compiling Vertex Shader
	    printf("Compiling vertex shader\n");
	    char const* vertexShaderCodePointer = vertexShaderCode.c_str();
	    glShaderSource(vertexShaderID, 1, &vertexShaderCodePointer, NULL);
	    glCompileShader(vertexShaderID);

	    // Checking Vertex Shader
	    glGetShaderiv(vertexShaderID, GL_COMPILE_STATUS, &result);
	    printf("Compilation %s\n", (result == GL_TRUE) ? "succeeded" : "failed");

	    // Getting the log
	    glGetShaderiv(vertexShaderID, GL_INFO_LOG_LENGTH, &infoLogMaxLength);
	    char vertexShaderErrorMessage[infoLogMaxLength];
	    vertexShaderErrorMessage[0] = 0;
	    glGetShaderInfoLog(vertexShaderID, infoLogMaxLength, &infoLogRealLength, vertexShaderErrorMessage);
	    if (strlen(vertexShaderErrorMessage) > 0)
	    {
	    	fprintf(stdout, "Vertex shader compilation log:\n%s\n", vertexShaderErrorMessage);
	    }


	    // Compiling Fragment Shader
	    printf("Compiling fragment shader\n");
	    char const * fragmentSourcePointer = fragmentShaderCode.c_str();
	    glShaderSource(fragmentShaderID, 1, &fragmentSourcePointer , NULL);
	    glCompileShader(fragmentShaderID);

	    // Checking Fragment Shader
	    glGetShaderiv(fragmentShaderID, GL_COMPILE_STATUS, &result);
	    printf("Compilation %s\n", (result == GL_TRUE) ? "succeeded" : "failed");

	    // Getting the log
	    glGetShaderiv(fragmentShaderID, GL_INFO_LOG_LENGTH, &infoLogMaxLength);
	    char fragmentShaderErrorMessage[infoLogMaxLength];
	    fragmentShaderErrorMessage[0] = 0;
	    glGetShaderInfoLog(fragmentShaderID, infoLogMaxLength, &infoLogRealLength, fragmentShaderErrorMessage);
	    if (strlen(fragmentShaderErrorMessage) > 0)
	    {
	    	fprintf(stdout, "Fragment shader compilation log:\n%s\n", fragmentShaderErrorMessage);
	    }

	    // Link the program
	    fprintf(stdout, "Linking program\n");
	    programID = glCreateProgram();
	    glAttachShader(programID, vertexShaderID);
	    glAttachShader(programID, fragmentShaderID);
	    glLinkProgram(programID);

	    // Check the program
	    glGetProgramiv(programID, GL_LINK_STATUS, &result);
	    glGetProgramiv(programID, GL_INFO_LOG_LENGTH, &infoLogMaxLength);
	    char programErrorMessage[infoLogMaxLength];
	    glGetProgramInfoLog(programID, infoLogMaxLength, &infoLogRealLength, programErrorMessage);

	    if (strlen(programErrorMessage) > 0)
	    {
	    	fprintf(stdout, "Shader program linking log:\n%s\n", programErrorMessage);
	    }

	    glDeleteShader(vertexShaderID);
	    glDeleteShader(fragmentShaderID);

	}

	ShaderProgram* ShaderProgram::fromFiles(const string& vertexShaderFileName, const string& fragmentShaderFileName)
	{
		bool error = false;

		FILE* vf = fopen(vertexShaderFileName.c_str(), "r");
		if (vf == NULL)
		{
			error = true;
			throw CubexException(__FILE__, __LINE__, string("Can't load vertex shader from file ") + vertexShaderFileName);
		}

		FILE* ff = fopen(fragmentShaderFileName.c_str(), "r");
		if (ff == NULL)
		{
			error = true;
			fclose(vf);
			throw CubexException(__FILE__, __LINE__, string("Can't load fragment shader from file ") + fragmentShaderFileName);
		}

		string vc;
		while (!feof(vf))
		{
			char buf[65536];
			size_t readnum = fread(buf, 1, 65535, vf);
			buf[readnum] = 0;
			vc += buf;
		}
		fclose(vf);

		string fc;
		while (!feof(ff))
		{
			char buf[65536];
			size_t readnum = fread(buf, 1, 65535, ff);
			buf[readnum] = 0;
			fc += buf;
		}
		fclose(ff);

		return new ShaderProgram(vc, fc);
	}

	GLint ShaderProgram::getAttribLocation(const string& attribName) const
	{
		GLint res = glGetAttribLocation(this->programID, attribName.c_str());
		if (glGetError() == GL_NO_ERROR)
		{
			return res;
		}
		else
		{
			throw CubexException(__FILE__, __LINE__, "Some problem with shader program object. It's undefined or not linked");
		}
	}

	GLint ShaderProgram::getUniformLocation(const string& uniformName) const
	{
		GLint res = glGetUniformLocation(this->programID, uniformName.c_str());
		int err = glGetError();

		if (err == GL_NO_ERROR)
		{
			return res;
		}
		else
		{
			stringstream ss;
			ss << "Some problem with shader program object. It's undefined or not linked. GL error code: " << err;
			throw CubexException(__FILE__, __LINE__, ss.str());
		}
		return res;
	}

	void ShaderProgram::linkTexture(Texture& texture, const string& sampler2DShaderVariableName)
	{
		linkedTextures.insert(pair<Texture*, string>(&texture, sampler2DShaderVariableName));
		texture.linkToShaderProgram(*this);
	}
	void ShaderProgram::unlinkTexture(Texture& texture)
	{
		linkedTextures.erase(&texture);
		texture.unlinkFromShaderProgram(*this);
	}

	void ShaderProgram::linkMeshBuffer(MeshBuffer& meshBuffer, const string& meshBufferShaderVariableName)
	{
		linkedMeshBuffers.insert(pair<MeshBuffer*, string>(&meshBuffer, meshBufferShaderVariableName));
	}
	void ShaderProgram::unlinkMeshBuffer(MeshBuffer& meshBuffer)
	{
		linkedMeshBuffers.erase(&meshBuffer);
	}

	void ShaderProgram::process() const
	{
		glUseProgram(programID);
		checkForError(__FILE__, __LINE__);

		// Loading textures
		for (map<Texture*, string>::const_iterator iter = linkedTextures.begin(); iter != linkedTextures.end(); iter++)
		{
			const Texture& texture = *(*iter).first;
			const string& sampler2DShaderVariableName = (*iter).second;

			if (texture.isBoundToImageUnit())
			{
				texture.activateImageUnit();
				glUniform1i(getUniformLocation(sampler2DShaderVariableName), texture.getImageUnitIndex());
				checkForError(__FILE__, __LINE__);
			}
			else
			{
				throw CubexException(__FILE__, __LINE__, string("Can't connect an unbound texture to a shader variable ") + sampler2DShaderVariableName);
			}
		}

		// Loading mesh buffers
		for (map<MeshBuffer*, string>::const_iterator iter = linkedMeshBuffers.begin(); iter != linkedMeshBuffers.end(); iter++)
		{
			const MeshBuffer& meshBuffer = *(*iter).first;
			const string& meshBufferShaderVariableName = (*iter).second;

			meshBuffer.draw(getAttribLocation(meshBufferShaderVariableName + "_vertexPosition"),
			                getAttribLocation(meshBufferShaderVariableName + "_normal"),
			                getAttribLocation(meshBufferShaderVariableName + "_textureCoords"));
		}
	}


	ShaderProgram::~ShaderProgram()
	{
		glDeleteProgram(programID);

		while (linkedTextures.size() > 0)
		{
			map<Texture*, string>::const_iterator iter = linkedTextures.begin();
			unlinkTexture(*(*iter).first);
		}

	}

}
