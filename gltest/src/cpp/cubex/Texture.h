/*
 * Texture.h
 *
 *  Created on: 24.06.2013
 *      Author: il
 */

#ifndef TEXTURE_H_
#define TEXTURE_H_

#include <string>
#include <list>

#include <GL3/gl3w.h>

#include "GLObject.h"
#include "ShaderProgram.h"

using namespace std;

namespace cubex
{
	class Texture : public GLObject
	{
		friend class ShaderProgram;
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

		list<ShaderProgram*> linkedShaderPrograms;

		void loadPNGToTexture(const char * file_name, int * width, int * height);

		void globalCountersInit();
	protected:
		int getTextureId() const { return textureId; }

		/**
		 * This intended to be called from the shader program only.
		 *
		 * When texture is linked to a shader program, it reports
		 * this fact to the texture itself, so when the texture
		 * is destroyed, it removes itself from all shader programs
		 * to avoid crushes
		 */
		void linkToShaderProgram(ShaderProgram& shaderProgram);

		/**
		 * This intended to be called from the shader program only.
		 *
		 * When texture is unlinked from a shader program, it reports
		 * this fact to the texture.
		 */
		void unlinkFromShaderProgram(ShaderProgram& shaderProgram);

	public:
		Texture(const string& fileName);
		Texture(int width, int height, Type type, int samples = 1);

		void bindToImageUnit();
		void unbindFromImageUnit();

		bool isBoundToImageUnit() const { return boundToIndex != -1; }
		int getImageUnitIndex() const { return boundToIndex; }

		void activateImageUnit() const;

		int getSamples() const { return samples; }
		Type getType() const { return type; }

		virtual ~Texture();
	};
}
#endif
