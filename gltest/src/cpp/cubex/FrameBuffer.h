/*
 * FrameBuffer.h
 *
 *  Created on: Aug 7, 2013
 *      Author: imizus
 */

#ifndef FRAMEBUFFER_H_
#define FRAMEBUFFER_H_

#include <GL3/gl3w.h>

#include "GLObject.h"
#include "Texture.h"

namespace cubex
{
	class FrameBuffer : public GLObject
	{
	private:
		GLuint frameBufferId;

		Texture *image, *depth;

		void internalConnectToImage(Texture& image, Texture* depth);
	public:
		FrameBuffer();

		void connectToImage(Texture& image);
		void connectToImage(Texture& image, Texture& depth);
		void bind();
		void unbind();

		virtual ~FrameBuffer();
	};
}
#endif
