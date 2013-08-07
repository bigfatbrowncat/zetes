/*
 * Scene.h
 *
 *  Created on: Jun 19, 2013
 *      Author: imizus
 */

#ifndef SCENE_H_
#define SCENE_H_

#include "ShaderProgram.h"
#include "MeshBuffer.h"
#include "Texture.h"


namespace cubex
{

	class Scene
	{
	private:
		// This class isn't copyable
		Scene operator = (const Scene& other);
		Scene(const Texture& other);

	private:
		ShaderProgram* program;
		MeshBuffer* meshBuffer;
		Texture* texture;

		/*int vertexCoordinatesAttrib;
		int textureCoordinatesAttrib;
		int normalAttrib;*/
		int lightPositionUniform;
		int matrixUniform, normalMatrixUniform;

		int viewWidth, viewHeight;
	public:
		Scene(const string& modelFileName,
		      const string& vertexShaderFileName,
		      const string& fragmentShaderFileName,
		      const string& textureFileName,
		      int viewWidth, int viewHeight);
		void resizeViewport(int width, int height);
		void draw(float angle);

		virtual ~Scene();
	};

}
#endif
