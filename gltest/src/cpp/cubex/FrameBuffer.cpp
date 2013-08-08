/*
 * FrameBuffer.cpp
 *
 *  Created on: Aug 7, 2013
 *      Author: imizus
 */

#include <GL3/gl3w.h>

#include "CubexException.h"

#include "FrameBuffer.h"

namespace cubex
{

	FrameBuffer::FrameBuffer(): image(NULL), depth(NULL)
	{
		glGenFramebuffers(1, &frameBufferId);
		checkForError(__FILE__, __LINE__);
	}

	void FrameBuffer::connectToImage(Texture& image)
	{
		internalConnectToImage(image, NULL);
	}
	void FrameBuffer::connectToImage(Texture& image, Texture& depth)
	{
		internalConnectToImage(image, &depth);
	}

	void FrameBuffer::internalConnectToImage(Texture& image, Texture* depth)
	{
		this->image = &image;
		this->depth = depth;

		GLint oldFramebufferId;
		glGetIntegerv(GL_FRAMEBUFFER_BINDING, &oldFramebufferId);
		checkForError(__FILE__, __LINE__);

		glBindFramebuffer(GL_FRAMEBUFFER, frameBufferId);
		checkForError(__FILE__, __LINE__);

		if (this->image->getSamples() > 1)
		{
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D_MULTISAMPLE, this->image->getTextureId(), 0 );
			checkForError(__FILE__, __LINE__);
		}
		else
		{
			glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this->image->getTextureId(), 0 );
			checkForError(__FILE__, __LINE__);
		}

		if (this->depth != NULL)
		{
			if (this->depth->getSamples() > 1)
			{
				glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D_MULTISAMPLE, this->depth->getTextureId(), 0 );
				checkForError(__FILE__, __LINE__);
			}
			else
			{
				glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, this->depth->getTextureId(), 0 );
				checkForError(__FILE__, __LINE__);
			}

			GLenum status = glCheckFramebufferStatus(GL_FRAMEBUFFER);
			if (status != GL_FRAMEBUFFER_COMPLETE)
			{
				throw CubexException(__FILE__, __LINE__, "Problem with the framebuffer creation");
			}
		}

		glBindFramebuffer(GL_FRAMEBUFFER, oldFramebufferId);
		checkForError(__FILE__, __LINE__);
	}

	void FrameBuffer::bind()
	{
		glBindFramebuffer(GL_FRAMEBUFFER, frameBufferId);
		checkForError(__FILE__, __LINE__);
	}
	void FrameBuffer::unbind()
	{
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		checkForError(__FILE__, __LINE__);
	}

	FrameBuffer::~FrameBuffer()
	{
		unbind();
		glDeleteFramebuffers(1, &frameBufferId);
	}

}
