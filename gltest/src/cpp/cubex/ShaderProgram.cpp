/*
 * ShaderProgram.cpp
 *
 *  Created on: May 21, 2013
 *      Author: imizus
 */

#include <string.h>

#include "GL3/gl3w.h"

#include "ShaderProgram.h"

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

	ShaderProgram::~ShaderProgram()
	{
		glDeleteProgram(programID);
	}

}
