/*
 * Scene.cpp
 *
 *  Created on: Jun 19, 2013
 *      Author: imizus
 */

#include <GL3/gl3w.h>
#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>
#include <glm/gtc/type_ptr.hpp>

#include "ObjMeshLoader.h"

#include "FrameBuffer.h"
#include "Scene.h"

namespace cubex
{
	Scene::Scene(const string& modelFileName, const string& vertexShaderFileName, const string& fragmentShaderFileName, const string& textureFileName, int viewWidth, int viewHeight) :
			frameImage(NULL), depthImage(NULL)
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
	    program = ShaderProgram::fromFiles(vertexShaderFileName, fragmentShaderFileName);

	    meshBuffer->connectToShaderProgram(program, "in_vertexPosition", "in_normal", "in_textureCoords");

	    lightPositionUniform = program->getUniformLocation("uni_lightPosition");
	    matrixUniform = program->getUniformLocation("uni_matrix");
	    normalMatrixUniform = program->getUniformLocation("uni_normalMatrix");

	    texture = new Texture(textureFileName);
	    texture->connectToShaderProgram(*program, "uni_texture");

	}

	void Scene::resizeViewport(int width, int height)
	{
		this->viewWidth = width;
		this->viewHeight = height;

		if (frameImage != NULL)
		{
			delete frameImage;
			delete depthImage;
			frameImage = NULL;
			depthImage = NULL;
		}

		frameImage = new Texture(viewWidth, viewHeight, Texture::tRGB, 1);
		depthImage = new Texture(viewWidth, viewHeight, Texture::tDepth, 1);
	}

	void Scene::draw(float angle)
	{
		glViewport(0, 0, viewWidth, viewHeight);
		checkForError(__FILE__, __LINE__);

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		checkForError(__FILE__, __LINE__);

		glEnable(GL_DEPTH_TEST);
		checkForError(__FILE__, __LINE__);


		//PROJECTION
		float aspectRatio = (float)viewWidth / viewHeight;

		glm::mat4 Projection = glm::perspective(45.0f, aspectRatio, 0.1f, 10.0f);
		if (viewWidth < viewHeight)
		{
			Projection = glm::scale(Projection, glm::vec3(aspectRatio, aspectRatio, aspectRatio));
		}

		// View
		glm::mat4 view = glm::mat4(1.0);
		view = glm::lookAt(glm::vec3(-6.0, 1.7, 2.0), glm::vec3(0, -0.2, 0), glm::vec3(0, 1, 0));

		//MODEL
		glm::mat4 Model = glm::mat4(1.0f);
		//Scale by factor 0.5
		Model = glm::rotate(Model, 180.0f * angle, glm::vec3(0.0f, 1.0f, 0.0f));
		glm::mat4 MP = Projection * view * Model;
		glm::mat3 NM = glm::inverse(glm::transpose(glm::mat3(MP)));

		program->use();

		// Sending matrix
		glUniformMatrix4fv(matrixUniform, 1, GL_FALSE, glm::value_ptr(MP));
		checkForError(__FILE__, __LINE__);
		glUniformMatrix3fv(normalMatrixUniform, 1, GL_FALSE, glm::value_ptr(NM));
		checkForError(__FILE__, __LINE__);

		// Sending light position
		glUniform3f(lightPositionUniform, -1.0f, 3.0f, 1.0f);
		checkForError(__FILE__, __LINE__);


		FrameBuffer fbo;
		fbo.connectToImage(*frameImage);
		fbo.bind();
		{
			// Drawing to the framebuffer
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			checkForError(__FILE__, __LINE__);

			texture->connectToShaderProgram(*program, "uni_texture");
			program->use();
			texture->bind();
			meshBuffer->draw();
			texture->unbind();
		}
		fbo.unbind();

		frameImage->connectToShaderProgram(*program, "uni_texture");
		program->use();
		frameImage->bind();
		meshBuffer->draw();
		frameImage->unbind();
	}

	Scene::~Scene()
	{
		delete program;
		delete meshBuffer;
		delete texture;
	}

}
