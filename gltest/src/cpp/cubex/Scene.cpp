/*
 * Scene.cpp
 *
 *  Created on: Jun 19, 2013
 *      Author: imizus
 */

#define PNG_SETJMP_SUPPORTED
#define PNG_STDIO_SUPPORTED
#define PNG_SEQUENTIAL_READ_SUPPORTED
#include <png/png.h>

#include <GL3/gl3w.h>
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include <glm/gtc/type_ptr.hpp>

#include "ObjMeshLoader.h"

#include "Scene.h"

namespace cubex
{
	GLuint png_texture_load(const char * file_name, int * width, int * height)
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
		glGenTextures(1, &texture);
		glBindTexture(GL_TEXTURE_2D, texture);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, temp_width, temp_height, 0, GL_RGB, GL_UNSIGNED_BYTE, image_data);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		// clean up
		png_destroy_read_struct(&png_ptr, &info_ptr, &end_info);
		free(image_data);
		free(row_pointers);
		fclose(fp);
		printf("texture id: %d, w: %d, h: %d\n", texture, temp_width, temp_height);
		return texture;
	}


	Scene::Scene(const string& modelFileName, const string& vertexShaderFileName, const string& fragmentShaderFileName, const string& textureFileName, int viewWidth, int viewHeight)
	{
		this->viewWidth = viewWidth;
		this->viewHeight = viewHeight;

		ObjMeshLoader objLoader;
		printf("Loading mesh from file %s...", modelFileName.c_str()); fflush(stdout);
		Mesh cube = objLoader.createMeshFromFile(modelFileName);
		printf("Loaded\n");
		printf("Generating mesh buffer...\n"); fflush(stdout);
		meshBuffer = new MeshBuffer(cube);
		printf("Mesh buffer generated\n"); fflush(stdout);

	    // Read the Vertex Shader code from the file
	    program = ShaderProgram::fromFiles(vertexShaderFileName, fragmentShaderFileName); //new ShaderProgram(VertexShaderCode, FragmentShaderCode);

	    vertexCoordinatesAttrib = program->getAttribLocation("in_vertexPosition");
	    textureCoordinatesAttrib = program->getAttribLocation("in_textureCoords");
	    diffuseColorAttrib = program->getAttribLocation("in_diffuseColor");

	    matrixUniform = program->getUniformLocation("matrix");
	    textureUniform = program->getUniformLocation("texture");
	    printf("texUni: %d\n", textureUniform);

	    int tw;
	    int th;

	    textureID = png_texture_load(textureFileName.c_str(), &tw, &th);

	}

	void Scene::resizeViewport(int width, int height)
	{
		this->viewWidth = width;
		this->viewHeight = height;
	}

	void Scene::draw(float angle)
	{
		glViewport(0, 0, viewWidth, viewHeight);

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glEnable(GL_DEPTH_TEST);

		// 1st attribute buffer : vertices
		glEnableVertexAttribArray(vertexCoordinatesAttrib);
		// 2nd attribute buffer : texture coords
		glEnableVertexAttribArray(textureCoordinatesAttrib);
		// 2nd attribute buffer : texture coords
		glEnableVertexAttribArray(diffuseColorAttrib);

		//PROJECTION
		float aspectRatio = (float)viewWidth / viewHeight;

		glm::mat4 Projection = glm::perspective(45.0f, aspectRatio, 0.1f, 100.0f);
		if (viewWidth < viewHeight)
		{
			Projection = glm::scale(Projection, glm::vec3(aspectRatio, aspectRatio, aspectRatio));
		}

		//MODEL
		glm::mat4 Model = glm::mat4(1.0);
		//Scale by factor 0.5
		Model = glm::scale(Model, glm::vec3(0.2f));
		Model = glm::rotate(Model, 30.0f, glm::vec3(1.0f, 0.0f, 0.0f));
		Model = glm::rotate(Model, (float)(angle * 180), glm::vec3(0.0f, 1.0f, 0.0f));

		glm::mat4 MP = Projection * Model;

		// Sending matrix
		glUniformMatrix4fv(matrixUniform, 1, GL_FALSE, glm::value_ptr(MP));

		// Sending texture
		glActiveTexture(GL_TEXTURE0 + 0);
		glUniform1i(textureUniform, 0);
		glBindTexture(GL_TEXTURE_2D, textureID);

		// Setting vertex data
		glVertexAttribPointer(
		   vertexCoordinatesAttrib,                  // first "in" in shader
		   3,                  	// size
		   GL_FLOAT,           // type
		   GL_FALSE,           // normalized?
		   sizeof(GLfloat) * 8,                    // stride
		   (void*)0                                    // array buffer offset
		);
		// Setting diffuse color data
		glVertexAttribPointer(
		   diffuseColorAttrib,                  // second "in" in shader
		   3,                  // size
		   GL_FLOAT,           // type
		   GL_FALSE,           // normalized?
		   sizeof(GLfloat) * 8,                    // stride
		   (void*)(sizeof(GLfloat) * 3)            // array buffer offset
		);
		// Setting texture coordinates data
		glVertexAttribPointer(
		   textureCoordinatesAttrib,                  // second "in" in shader
		   2,                  // size
		   GL_FLOAT,           // type
		   GL_FALSE,           // normalized?
		   sizeof(GLfloat) * 8,                    // stride
		   (void*)(sizeof(GLfloat) * 6)            // array buffer offset
		);


		// Use our shader
		program->use();

		meshBuffer->draw();

		glDisableVertexAttribArray(vertexCoordinatesAttrib);
		glDisableVertexAttribArray(textureCoordinatesAttrib);

	}

	Scene::~Scene()
	{
		delete meshBuffer;
	}

}
