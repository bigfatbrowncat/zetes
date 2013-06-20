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

using namespace std;

namespace cubex
{
	struct Face3
	{
		unsigned int vertexIndex1;
		unsigned int vertexIndex2;
		unsigned int vertexIndex3;

		bool containsNormals;
		unsigned int normalIndex1;
		unsigned int normalIndex2;
		unsigned int normalIndex3;

		bool containsTextureCoords;
		unsigned int textureCoordIndex1;
		unsigned int textureCoordIndex2;
		unsigned int textureCoordIndex3;

		static Face3 fromVertices(unsigned int vi1, unsigned int vi2, unsigned int vi3)
		{
			Face3 res;

			res.containsNormals = false;
			res.containsTextureCoords = false;

			res.vertexIndex1 = vi1;
			res.vertexIndex2 = vi2;
			res.vertexIndex3 = vi3;

			return res;
		}

		static Face3 fromVerticesAndNormals(unsigned int vi1, unsigned int vi2, unsigned int vi3,
											unsigned int vni1, unsigned int vni2, unsigned int vni3)
		{
			Face3 res;

			res.containsNormals = true;
			res.containsTextureCoords = false;

			res.vertexIndex1 = vi1;
			res.vertexIndex2 = vi2;
			res.vertexIndex3 = vi3;

			res.normalIndex1 = vni1;
			res.normalIndex2 = vni2;
			res.normalIndex3 = vni3;

			return res;

		}

		static Face3 fromVerticesAndTextureCoordsAndNormals(unsigned int vi1, unsigned int vi2, unsigned int vi3,
															unsigned int vti1, unsigned int vti2, unsigned int vti3,
															unsigned int vni1, unsigned int vni2, unsigned int vni3)
		{
			Face3 res;

			res.containsNormals = true;
			res.containsTextureCoords = true;

			res.vertexIndex1 = vi1;
			res.vertexIndex2 = vi2;
			res.vertexIndex3 = vi3;

			res.normalIndex1 = vni1;
			res.normalIndex2 = vni2;
			res.normalIndex3 = vni3;

			res.textureCoordIndex1 = vti1;
			res.textureCoordIndex2 = vti2;
			res.textureCoordIndex3 = vti3;

			return res;

		}

		static Face3 fromVerticesAndTextureCoords(unsigned int vi1, unsigned int vi2, unsigned int vi3,
												  unsigned int vti1, unsigned int vti2, unsigned int vti3)
		{
			Face3 res;

			res.containsNormals = false;
			res.containsTextureCoords = true;

			res.vertexIndex1 = vi1;
			res.vertexIndex2 = vi2;
			res.vertexIndex3 = vi3;

			res.textureCoordIndex1 = vti1;
			res.textureCoordIndex2 = vti2;
			res.textureCoordIndex3 = vti3;

			return res;

		}

		Face3()
		{
			vertexIndex1 = 0;
			vertexIndex2 = 0;
			vertexIndex3 = 0;

			normalIndex1 = 0;
			normalIndex2 = 0;
			normalIndex3 = 0;

			textureCoordIndex1 = 0;
			textureCoordIndex2 = 0;
			textureCoordIndex3 = 0;

			containsNormals = false;
			containsTextureCoords = false;
		}
	};

	class Mesh
	{
	private:
		vector<glm::vec3> vertices;
		vector<glm::vec3> normals;
		vector<glm::vec2> textureCoords;
		vector<Face3> faces3;
	public:
		const vector<glm::vec3>& getVertices() const { return vertices; }
		const vector<glm::vec3>& getNormals() const { return normals; }
		const vector<glm::vec2>& getTextureCoords() const { return textureCoords; }
		const vector<Face3> getFaces3() const { return faces3; }

		void addVertex(glm::vec3 vertex);
		void addNormal(glm::vec3 normal);
		void addTextureCoords(glm::vec2 texCoord);

		bool checkFace3(const Face3& face);

		void addFace3(Face3 face);
	};


} /* namespace cubex */
#endif /* MESH_H_ */
