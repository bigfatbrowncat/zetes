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

#include "GLObject.h"
#include "ShaderProgram.h"

using namespace std;

namespace cubex
{
	class Texture : public GLObject
	{
		friend class FrameBuffer;
	public:
		enum Type
		{
			tRGB, tRGBA, tDepth
		};

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
		int width, height;
		int samples;
		Type type;

		GLint textureUniform;

		void loadPNGToTexture(const char * file_name, int * width, int * height);

		void globalCountersInit();
	protected:
		int getTextureId() const { return textureId; }

	public:
		Texture(const string& fileName);
		Texture(int width, int height, Type type, int samples = 1);

		void connectToShaderVariable(const ShaderProgram& shaderProgram, const string& sampler2DShaderVariableName);
		void bindToImageUnit();
		void unbindFromImageUnit();

		int getSamples() const { return samples; }
		Type getType() const { return type; }

		virtual ~Texture();
	};
}
#endif
