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
	Scene::Scene(const string& modelFileName,
	             const string& vertexShaderFileName, const string& fragmentShaderFileName,
	             const string& screenVertexShaderFileName, const string& screenFragmentShaderFileName, const string& textureFileName, int viewWidth, int viewHeight) :
			antialiasMulti(3), frameImage(NULL), depthImage(NULL)
	{
		this->viewWidth = viewWidth;
		this->viewHeight = viewHeight;

		// Loading the cube
		ObjMeshLoader objLoader;
		printf("Loading mesh from file %s...", modelFileName.c_str()); fflush(stdout);
		Mesh cube = objLoader.createMeshFromFile(modelFileName);
		printf("Loaded\n");
		printf("Generating mesh buffer...\n"); fflush(stdout);
		meshBuffer = new MeshBuffer(cube);
		printf("Mesh buffer generated\n"); fflush(stdout);

		// Constructing the screen plane
		Mesh scrPlane;
		scrPlane.addVertex(glm::vec3(-1.0, 1.0, 0.0));
		scrPlane.addVertex(glm::vec3(1.0, 1.0, 0.0));
		scrPlane.addVertex(glm::vec3(1.0, -1.0, 0.0));
		scrPlane.addVertex(glm::vec3(-1.0, -1.0, 0.0));
		scrPlane.addTextureCoords(glm::vec2(0.0, antialiasMulti));
		scrPlane.addTextureCoords(glm::vec2(antialiasMulti, antialiasMulti));
		scrPlane.addTextureCoords(glm::vec2(antialiasMulti, 0.0));
		scrPlane.addTextureCoords(glm::vec2(0.0, 0.0));

		scrPlane.addFace(Face::fromVerticesAndTextureCoords(0, 1, 2, 0, 1, 2));
		scrPlane.addFace(Face::fromVerticesAndTextureCoords(0, 2, 3, 0, 2, 3));
		screenPlaneMeshBuffer = new MeshBuffer(scrPlane);

	    // Read the Vertex Shader code from the file
	    shaderProgram = ShaderProgram::fromFiles(vertexShaderFileName, fragmentShaderFileName);
	    shaderProgram->process();


	    lightPositionUniform = shaderProgram->getUniformLocation("uni_lightPosition");
	    matrixUniform = shaderProgram->getUniformLocation("uni_matrix");
	    normalMatrixUniform = shaderProgram->getUniformLocation("uni_normalMatrix");

	    screenPlaneShaderProgram = ShaderProgram::fromFiles(screenVertexShaderFileName, screenFragmentShaderFileName);

	    shaderProgram->linkMeshBuffer(*meshBuffer, "in_mesh");
	    screenPlaneShaderProgram->linkMeshBuffer(*screenPlaneMeshBuffer, "in_mesh");

	    //meshBuffer->connectToShaderProgram(shaderProgram, "in_vertexPosition", "in_normal", "in_textureCoords");
	    //screenPlaneMeshBuffer->connectToShaderProgram(screenPlaneShaderProgram, "in_vertexPosition", "in_normal", "in_textureCoords");

	    texture = new Texture(textureFileName);
		texture->bindToImageUnit();
		renewFramebufferTextures();
	}

	void Scene::renewFramebufferTextures()
	{
		if (frameImage != NULL)
		{
			delete frameImage;
			frameImage = NULL;
		}

		if (depthImage != NULL)
		{
			delete depthImage;
			depthImage = NULL;
		}

		frameImage = new Texture(viewWidth * antialiasMulti, viewHeight * antialiasMulti, Texture::tRGBA, 1);
		depthImage = new Texture(viewWidth * antialiasMulti, viewHeight * antialiasMulti, Texture::tDepth, 1);
		frameImage->bindToImageUnit();
		depthImage->bindToImageUnit();
	}

	void Scene::resizeViewport(int width, int height)
	{
		this->viewWidth = width;
		this->viewHeight = height;

		renewFramebufferTextures();
	}

	void Scene::draw(float angle)
	{
		glEnable(GL_DEPTH_TEST);
		checkForError(__FILE__, __LINE__);

		glEnable(GL_MULTISAMPLE);
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

		shaderProgram->process();

		// Sending matrix
		glUniformMatrix4fv(matrixUniform, 1, GL_FALSE, glm::value_ptr(MP));
		checkForError(__FILE__, __LINE__);
		glUniformMatrix3fv(normalMatrixUniform, 1, GL_FALSE, glm::value_ptr(NM));
		checkForError(__FILE__, __LINE__);

		// Sending light position
		glUniform3f(lightPositionUniform, -1.0f, 3.0f, 1.0f);
		checkForError(__FILE__, __LINE__);


		glViewport(0, 0, antialiasMulti * viewWidth, antialiasMulti * viewHeight);
		checkForError(__FILE__, __LINE__);

		FrameBuffer fbo;
		fbo.connectToImage(*frameImage, *depthImage);
		fbo.bind();
		{
			// Drawing to the framebuffer
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			checkForError(__FILE__, __LINE__);

			shaderProgram->linkTexture(*texture, "uni_texture");
			//texture->connectToShaderVariable(*shaderProgram, "uni_texture");
			//meshBuffer->draw();
			shaderProgram->process();
		}
		fbo.unbind();

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		checkForError(__FILE__, __LINE__);
		screenPlaneShaderProgram->linkTexture(*frameImage, "uni_texture");
		//frameImage->connectToShaderVariable(*screenPlaneShaderProgram, "uni_texture");

		screenPlaneShaderProgram->process();
		//screenPlaneMeshBuffer->draw();
	}

	Scene::~Scene()
	{
		delete shaderProgram;
		delete screenPlaneShaderProgram;
		delete meshBuffer;
		delete screenPlaneMeshBuffer;

		delete texture;

		if (frameImage != NULL)
		{
			delete frameImage;
		}

		if (depthImage != NULL)
		{
			delete depthImage;
		}
	}

}
