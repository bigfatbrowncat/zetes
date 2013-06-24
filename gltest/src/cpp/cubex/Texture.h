/*
 * Texture.h
 *
 *  Created on: 24.06.2013
 *      Author: il
 */

#ifndef TEXTURE_H_
#define TEXTURE_H_

#include <string>

#include <GL3/gl3w.h>

#include "ShaderProgram.h"

using namespace std;

namespace cubex
{
	class Texture
	{
	private:
		// This class isn't copyable
		Texture operator = (const Texture& other);
		Texture(const Texture& other);

	private:
		static int textureObjectsCount;
		static bool* imageUnits;
		static int imageUnitsCount;

	private:
		GLuint textureId;
		int boundToIndex;
		GLint textureUniform;
		int width, height;
		static GLuint loadPNGToTexture(const char * file_name, int * width, int * height);
	public:
		Texture(const string& fileName, const ShaderProgram& program, const string& textureNameInShaderProgram);
		bool bind();
		void unbind();
		virtual ~Texture();
	};
}
#endif
