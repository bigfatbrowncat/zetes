/*
 * GLObject.cpp
 *
 *  Created on: 07 авг. 2013 г.
 *      Author: il
 */

#include <GL3/gl3w.h>

#include "CubexException.h"
#include "GLObject.h"

namespace cubex {

void GLObject::checkForError(const string& fileName, int lineNumber) const
{
	GLint errCode = glGetError();
	if (errCode != GL_NO_ERROR)
	{
		string errorName = "unknown";
		switch (errCode)
		{
		case GL_INVALID_ENUM:
			errorName = "GL_INVALID_ENUM";
			break;
		case GL_INVALID_VALUE:
			errorName = "GL_INVALID_VALUE";
			break;
		case GL_INVALID_OPERATION:
			errorName = "GL_INVALID_OPERATION";
			break;
		case GL_OUT_OF_MEMORY:
			errorName = "GL_OUT_OF_MEMORY";
			break;
		}

		throw CubexException(fileName, lineNumber, string("GL error ") + errorName + " occured");
	}
}


GLObject::GLObject() {
	// TODO Auto-generated constructor stub

}

GLObject::~GLObject() {
	// TODO Auto-generated destructor stub
}

} /* namespace cubex */
