/*
 * Mesh.h
 *
 *  Created on: Jun 19, 2013
 *      Author: imizus
 */

#ifndef MESH_H_
#define MESH_H_

#include <vector>

#include <glm/glm.hpp>

#include "CubexException.h"
#include "Face.h"

using namespace std;

namespace cubex
{
	class Mesh
	{
	private:
		vector<glm::vec3> vertices;
		vector<glm::vec3> normals;
		vector<glm::vec2> textureCoords;
		vector<Face> faces;
	public:
		const vector<glm::vec3>& getVertices() const { return vertices; }
		const vector<glm::vec3>& getNormals() const { return normals; }
		const vector<glm::vec2>& getTextureCoords() const { return textureCoords; }
		const vector<Face>& getFaces() const { return faces; }

		void addVertex(glm::vec3 vertex);
		void addNormal(glm::vec3 normal);
		void addTextureCoords(glm::vec2 texCoord);

		bool checkFace(const Face& face);

		void addFace(const Face& face);
	};


}
#endif
