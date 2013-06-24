/*
 * Texture.cpp
 *
 *  Created on: 24.06.2013
 *      Author: il
 */

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

	GLuint Texture::loadPNGToTexture(const char * file_name, int * width, int * height)
	{
		png_byte header[8];

		FILE *fp = fopen(file_name, "rb");
		if (fp == 0)
		{
			perror(file_name);
			return 0;
		}

		// read the header
		fread(header, 1, 8, fp);

		if (png_sig_cmp(header, 0, 8))
		{
			fprintf(stderr, "error: %s is not a PNG.\n", file_name);
			fclose(fp);
			return 0;
		}

		png_structp png_ptr = png_create_read_struct(PNG_LIBPNG_VER_STRING, NULL, NULL, NULL);
		if (!png_ptr)
		{
			fprintf(stderr, "error: png_create_read_struct returned 0.\n");
			fclose(fp);
			return 0;
		}

		// create png info struct
		png_infop info_ptr = png_create_info_struct(png_ptr);
		if (!info_ptr)
		{
			fprintf(stderr, "error: png_create_info_struct returned 0.\n");
			png_destroy_read_struct(&png_ptr, (png_infopp)NULL, (png_infopp)NULL);
			fclose(fp);
			return 0;
		}

		// create png info struct
		png_infop end_info = png_create_info_struct(png_ptr);
		if (!end_info)
		{
			fprintf(stderr, "error: png_create_info_struct returned 0.\n");
			png_destroy_read_struct(&png_ptr, &info_ptr, (png_infopp) NULL);
			fclose(fp);
			return 0;
		}

		// the code in this if statement gets called if libpng encounters an error
		if (setjmp(png_jmpbuf(png_ptr))) {
			fprintf(stderr, "error from libpng\n");
			png_destroy_read_struct(&png_ptr, &info_ptr, &end_info);
			fclose(fp);
			return 0;
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
			return 0;
		}

		// row_pointers is for pointing to image_data for reading the png with libpng
		png_bytep * row_pointers = (png_bytep *)malloc(temp_height * sizeof(png_bytep));
		if (row_pointers == NULL)
		{
			fprintf(stderr, "error: could not allocate memory for PNG row pointers\n");
			png_destroy_read_struct(&png_ptr, &info_ptr, &end_info);
			free(image_data);
			fclose(fp);
			return 0;
		}

		// set the individual row_pointers to point at the correct offsets of image_data
		int i;
		for (i = 0; i < temp_height; i++)
		{
			row_pointers[temp_height - 1 - i] = image_data + i * rowbytes;
		}

		// read the png into image_data through row_pointers
		png_read_image(png_ptr, row_pointers);

		// Generate the OpenGL texture object
		GLuint texture;

		// Using trilinear filtering
		glGenTextures(1, &texture);
		glBindTexture(GL_TEXTURE_2D, texture);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		if (info_ptr->channels == 4)
		{
			printf("RGBA PNG texture detected\n");
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, temp_width, temp_height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image_data);
		}
		else if (info_ptr->channels == 3)
		{
			printf("RGB PNG texture detected\n");
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, temp_width, temp_height, 0, GL_RGB, GL_UNSIGNED_BYTE, image_data);
		}

		// generate mipmaps
		glGenerateMipmap(GL_TEXTURE_2D);

		// clean up
		png_destroy_read_struct(&png_ptr, &info_ptr, &end_info);
		free(image_data);
		free(row_pointers);
		fclose(fp);
		printf("Texture loaded. ID: %d, width: %d, height: %d\n", texture, temp_width, temp_height);
		return texture;
	}

	Texture::Texture(const string& fileName, const ShaderProgram& program, const string& textureNameInShaderProgram)
	{
		// Instance operations
		textureId = loadPNGToTexture(fileName.c_str(), &width, &height);
	    if (textureId == 0) throw CubexException(string("Can't load the texture ") + textureNameInShaderProgram + " from file " + fileName);

	    textureUniform = program.getUniformLocation(textureNameInShaderProgram);

	    // Global operations
		if (textureObjectsCount == 0)
		{
			printf("This texture object is the first one.\n");
			glGetIntegerv(GL_MAX_TEXTURE_IMAGE_UNITS, &imageUnitsCount);
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

	bool Texture::bind()
	{
		for (int i = 0; i < imageUnitsCount; i++)
		{
			if (!imageUnits[i])
			{
				// Binding the texture
				glActiveTexture(GL_TEXTURE0 + i);
				glBindTexture(GL_TEXTURE_2D, textureId);

				// Sending the index to which the texture is bound to the shader program
				glUniform1i(textureUniform, i);

				imageUnits[i] = true;
				boundToIndex = i;
				printf("Texture object bound to the image unit #%d.\n", i);

				return true;
			}
		}

		return false;
	}

	void Texture::unbind()
	{
		if (imageUnits[boundToIndex])
		{
			// Unbinding the texture
			glActiveTexture(GL_TEXTURE0 + boundToIndex);
			glBindTexture(GL_TEXTURE_2D, 0);
			printf("Texture object unbound from the image unit #%d.\n", boundToIndex);
		}
	}

	Texture::~Texture()
	{
		// Instance operations
		unbind();
		glDeleteTextures(1, &textureId);

		// Decrementing global object counter
		textureObjectsCount --;
		printf("Texture object unloaded. Now there are %d of them.\n", textureObjectsCount);

		// Global operatons
		if (textureObjectsCount == 0)
		{
			// Unbinding all textures
			for (int i = 0; i < imageUnitsCount; i++)
			{
				if (imageUnits[i])
				{
					glActiveTexture(GL_TEXTURE0 + i);
					glBindTexture(GL_TEXTURE_2D, 0);
					imageUnits[i] = false;
				}
			}

			// Freeing the imageUnits array
			delete [] imageUnits;
			printf("The last texture object is unloaded.\n");
		}
	}

}
