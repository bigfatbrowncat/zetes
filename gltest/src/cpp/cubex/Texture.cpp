/*
 * Texture.cpp
 *
 *  Created on: 24.06.2013
 *      Author: il
 */

#include <stdlib.h>
#include <string>

#define PNG_SETJMP_SUPPORTED
#define PNG_STDIO_SUPPORTED
#define PNG_SEQUENTIAL_READ_SUPPORTED
#include <png/png.h>
#include <png/pnginfo.h>

#include "CubexException.h"

#include "Texture.h"

using namespace std;

namespace cubex
{
	bool* Texture::imageUnits = NULL;
	int Texture::imageUnitsCount = 0;
	int Texture::textureObjectsCount = 0;

	void Texture::loadPNGToTexture(const char * file_name, int * width, int * height)
	{
		png_byte header[8];

		FILE *fp = fopen(file_name, "rb");
		if (fp == 0)
		{
			perror(file_name);
			textureId = 0;
			return;
		}

		// read the header
		fread(header, 1, 8, fp);

		if (png_sig_cmp(header, 0, 8))
		{
			fprintf(stderr, "error: %s is not a PNG.\n", file_name);
			fclose(fp);
			textureId = 0;
			return;
		}

		png_structp png_ptr = png_create_read_struct(PNG_LIBPNG_VER_STRING, NULL, NULL, NULL);
		if (!png_ptr)
		{
			fprintf(stderr, "error: png_create_read_struct returned 0.\n");
			fclose(fp);
			textureId = 0;
			return;
		}

		// create png info struct
		png_infop info_ptr = png_create_info_struct(png_ptr);
		if (!info_ptr)
		{
			fprintf(stderr, "error: png_create_info_struct returned 0.\n");
			png_destroy_read_struct(&png_ptr, (png_infopp)NULL, (png_infopp)NULL);
			fclose(fp);
			textureId = 0;
			return;
		}

		// create png info struct
		png_infop end_info = png_create_info_struct(png_ptr);
		if (!end_info)
		{
			fprintf(stderr, "error: png_create_info_struct returned 0.\n");
			png_destroy_read_struct(&png_ptr, &info_ptr, (png_infopp) NULL);
			fclose(fp);
			textureId = 0;
			return;
		}

		// the code in this if statement gets called if libpng encounters an error
		if (setjmp(png_jmpbuf(png_ptr))) {
			fprintf(stderr, "error from libpng\n");
			png_destroy_read_struct(&png_ptr, &info_ptr, &end_info);
			fclose(fp);
			textureId = 0;
			return;
		}

		// init png reading
		png_init_io(png_ptr, fp);

		// let libpng know you already read the first 8 bytes
		png_set_sig_bytes(png_ptr, 8);

		// read all the info up to the image data
		png_read_info(png_ptr, info_ptr);

		// variables to pass to get info
		int bit_depth, color_type;
		png_uint_32 temp_width, temp_height;

		// get info about png
		png_get_IHDR(png_ptr, info_ptr, &temp_width, &temp_height, &bit_depth, &color_type,
			NULL, NULL, NULL);

		if (width){ *width = temp_width; }
		if (height){ *height = temp_height; }

		// Update the png info struct.
		png_read_update_info(png_ptr, info_ptr);

		// Row size in bytes.
		int rowbytes = png_get_rowbytes(png_ptr, info_ptr);

		// glTexImage2d requires rows to be 4-byte aligned
		rowbytes += 3 - ((rowbytes-1) % 4);

		// Allocate the image_data as a big block, to be given to opengl
		png_byte * image_data;
		image_data = (png_byte *)malloc(rowbytes * temp_height * sizeof(png_byte)+15);
		if (image_data == NULL)
		{
			fprintf(stderr, "error: could not allocate memory for PNG image data\n");
			png_destroy_read_struct(&png_ptr, &info_ptr, &end_info);
			fclose(fp);
			textureId = 0;
			return;
		}

		// row_pointers is for pointing to image_data for reading the png with libpng
		png_bytep * row_pointers = (png_bytep *)malloc(temp_height * sizeof(png_bytep));
		if (row_pointers == NULL)
		{
			fprintf(stderr, "error: could not allocate memory for PNG row pointers\n");
			png_destroy_read_struct(&png_ptr, &info_ptr, &end_info);
			free(image_data);
			fclose(fp);
			textureId = 0;
			return;
		}

		// set the individual row_pointers to point at the correct offsets of image_data
		int i;
		for (i = 0; i < temp_height; i++)
		{
			row_pointers[temp_height - 1 - i] = image_data + i * rowbytes;
		}

		// read the png into image_data through row_pointers
		png_read_image(png_ptr, row_pointers);


		// Using trilinear filtering
		glGenTextures(1, &textureId);
		checkForError(__FILE__, __LINE__);
		glBindTexture(GL_TEXTURE_2D, textureId);
		checkForError(__FILE__, __LINE__);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		checkForError(__FILE__, __LINE__);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		checkForError(__FILE__, __LINE__);

		if (info_ptr->channels == 4)
		{
			type = tRGBA;
			printf("RGBA PNG texture detected\n");
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, temp_width, temp_height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image_data);
			checkForError(__FILE__, __LINE__);
		}
		else if (info_ptr->channels == 3)
		{
			type = tRGB;
			printf("RGB PNG texture detected\n");
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, temp_width, temp_height, 0, GL_RGB, GL_UNSIGNED_BYTE, image_data);
			checkForError(__FILE__, __LINE__);
		}

		// clean up
		png_destroy_read_struct(&png_ptr, &info_ptr, &end_info);
		free(image_data);
		free(row_pointers);
		fclose(fp);
		printf("Texture loaded. ID: %d, width: %d, height: %d\n", textureId, temp_width, temp_height);
	}

	void Texture::globalCountersInit()
	{
	    // Global operations
		if (textureObjectsCount == 0)
		{
			printf("This texture object is the first one.\n");
			glGetIntegerv(GL_MAX_TEXTURE_IMAGE_UNITS, &imageUnitsCount);
			checkForError(__FILE__, __LINE__);
			printf("This video card supports %d active textures at the same time.\n", imageUnitsCount);
			imageUnits = new bool[imageUnitsCount];
			for (int i = 0; i < imageUnitsCount; i++)
			{
				imageUnits[i] = false;
			}
		}

		// Incrementing global object counter
		textureObjectsCount ++;
		printf("Texture object loaded. Now there are %d of them.\n", textureObjectsCount);
	}

	Texture::Texture(int width, int height, Type type, int samples) : samples(samples), type(type), boundToIndex(-1)
	{
		glGenTextures(1, &textureId);
		checkForError(__FILE__, __LINE__);

		GLint format;
		string tp;
		switch (type)
		{
		case tRGBA:
			format = GL_RGBA;
			tp = "RGBA";
			break;
		case tRGB:
			format = GL_RGB;
			tp = "RGB";
			break;
		case tDepth:
			format = GL_DEPTH_COMPONENT;
			tp = "Depth";
			break;
		default:
			throw CubexException(__FILE__, __LINE__, "Strange type value");
		}

		if (samples == 1)
		{
			printf("texture created (%s)\n", tp.c_str());

			glBindTexture(GL_TEXTURE_2D, textureId);
			checkForError(__FILE__, __LINE__);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
			checkForError(__FILE__, __LINE__);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			checkForError(__FILE__, __LINE__);

			glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, format, GL_UNSIGNED_BYTE, NULL);
		}
		else
		{
			printf("RGBA multisample texture created\n");

			glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, textureId);
			checkForError(__FILE__, __LINE__);
			glTexImage2DMultisample(GL_TEXTURE_2D_MULTISAMPLE, samples, format, width, height, false);
			checkForError(__FILE__, __LINE__);
		}

		globalCountersInit();
	}

	void Texture::linkToShaderProgram(ShaderProgram& shaderProgram)
	{
		linkedShaderPrograms.push_back(&shaderProgram);
	}
	void Texture::unlinkFromShaderProgram(ShaderProgram& shaderProgram)
	{
		linkedShaderPrograms.remove(&shaderProgram);
	}

	Texture::Texture(const string& fileName) : samples(1), boundToIndex(-1)
	{
		// Instance operations
		loadPNGToTexture(fileName.c_str(), &width, &height);
	    if (textureId == 0) throw CubexException(__FILE__, __LINE__, string("Can't load the texture from file ") + fileName);

		globalCountersInit();
	}


	void Texture::bindToImageUnit()
	{
		if (boundToIndex == -1)
		{
			for (int i = 0; i < imageUnitsCount; i++)
			{
				if (!imageUnits[i])
				{
					imageUnits[i] = true;
					boundToIndex = i;
					//printf("Texture object bound to the image unit #%d.\n", i);

					return;
				}
			}

			throw CubexException(__FILE__, __LINE__, "Can't bind the texture. All image unit slots are occupied");
		}
		else
		{
			throw CubexException(__FILE__, __LINE__, "Can't bind the texture twice");
		}
	}

	void Texture::unbindFromImageUnit()
	{
		if (boundToIndex > -1)
		{
			if (imageUnits[boundToIndex])
			{
				// Unbinding the texture
				imageUnits[boundToIndex] = false;
				glActiveTexture(GL_TEXTURE0 + boundToIndex);
				checkForError(__FILE__, __LINE__);

				if (samples == 1)
				{
					glBindTexture(GL_TEXTURE_2D, 0);
					checkForError(__FILE__, __LINE__);
				}
				else
				{
					glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, 0);
					checkForError(__FILE__, __LINE__);
				}
				//printf("Texture object unbound from the image unit #%d.\n", boundToIndex);
				boundToIndex = -1;
			}
		}
	}

	void Texture::activateImageUnit() const
	{
		// Binding the texture
		glActiveTexture(GL_TEXTURE0 + boundToIndex);

		if (samples == 1)
		{
			glBindTexture(GL_TEXTURE_2D, textureId);
			checkForError(__FILE__, __LINE__);

			// generate mipmaps
			glGenerateMipmap(GL_TEXTURE_2D);
			checkForError(__FILE__, __LINE__);
		}
		else
		{
			glBindTexture(GL_TEXTURE_2D_MULTISAMPLE, textureId);
			printf("%d\n", samples);
			checkForError(__FILE__, __LINE__);
		}
	}


	Texture::~Texture()
	{
		// Unlinking from shaders
		while (linkedShaderPrograms.size() > 0)
		{
			list<ShaderProgram*>::iterator iter = linkedShaderPrograms.begin();
			(*iter)->unlinkTexture(*this);
		}

		// Instance operations
		unbindFromImageUnit();
		glDeleteTextures(1, &textureId);

		// Decrementing global object counter
		textureObjectsCount --;
		printf("Texture object unloaded. Now there are %d of them.\n", textureObjectsCount);

		// Global operatons
		if (textureObjectsCount == 0)
		{
			// Freeing the imageUnits array
			delete [] imageUnits;
			printf("The last texture object is unloaded.\n");
		}
	}

}
