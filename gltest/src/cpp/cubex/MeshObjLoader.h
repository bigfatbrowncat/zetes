/*
 * ObjLoader.h
 *
 *  Created on: 17.06.2013
 *      Author: il
 */

#ifndef MESHOBJLOADER_H_
#define MESHOBJLOADER_H_

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

	struct Face4
	{
		unsigned int vertexIndex1;
		unsigned int vertexIndex2;
		unsigned int vertexIndex3;
		unsigned int vertexIndex4;

		bool containsNormals;
		unsigned int normalIndex1;
		unsigned int normalIndex2;
		unsigned int normalIndex3;
		unsigned int normalIndex4;

		bool containsTextureCoords;
		unsigned int textureCoordIndex1;
		unsigned int textureCoordIndex2;
		unsigned int textureCoordIndex3;
		unsigned int textureCoordIndex4;

		static Face4 fromVertices(unsigned int vi1, unsigned int vi2, unsigned int vi3, unsigned int vi4)
		{
			Face4 res;

			res.containsNormals = false;
			res.containsTextureCoords = false;

			res.vertexIndex1 = vi1;
			res.vertexIndex2 = vi2;
			res.vertexIndex3 = vi3;
			res.vertexIndex4 = vi4;

			return res;
		}

		static Face4 fromVerticesAndNormals(unsigned int vi1, unsigned int vi2, unsigned int vi3, unsigned int vi4,
		                                    unsigned int vni1, unsigned int vni2, unsigned int vni3, unsigned int vni4)
		{
			Face4 res;

			res.containsNormals = true;
			res.containsTextureCoords = false;

			res.vertexIndex1 = vi1;
			res.vertexIndex2 = vi2;
			res.vertexIndex3 = vi3;
			res.vertexIndex4 = vi4;

			res.normalIndex1 = vni1;
			res.normalIndex2 = vni2;
			res.normalIndex3 = vni3;
			res.normalIndex4 = vni4;

			return res;

		}

		static Face4 fromVerticesAndTextureCoordsAndNormals(unsigned int vi1, unsigned int vi2, unsigned int vi3, unsigned int vi4,
                                                            unsigned int vti1, unsigned int vti2, unsigned int vti3, unsigned int vti4,
		                                                    unsigned int vni1, unsigned int vni2, unsigned int vni3, unsigned int vni4)
		{
			Face4 res;

			res.containsNormals = true;
			res.containsTextureCoords = true;

			res.vertexIndex1 = vi1;
			res.vertexIndex2 = vi2;
			res.vertexIndex3 = vi3;
			res.vertexIndex4 = vi4;

			res.normalIndex1 = vni1;
			res.normalIndex2 = vni2;
			res.normalIndex3 = vni3;
			res.normalIndex4 = vni4;

			res.textureCoordIndex1 = vti1;
			res.textureCoordIndex2 = vti2;
			res.textureCoordIndex3 = vti3;
			res.textureCoordIndex4 = vti4;

			return res;

		}

		static Face4 fromVerticesAndTextureCoords(unsigned int vi1, unsigned int vi2, unsigned int vi3, unsigned int vi4,
                                                  unsigned int vti1, unsigned int vti2, unsigned int vti3, unsigned int vti4)
		{
			Face4 res;

			res.containsNormals = false;
			res.containsTextureCoords = true;


			res.vertexIndex1 = vi1;
			res.vertexIndex2 = vi2;
			res.vertexIndex3 = vi3;
			res.vertexIndex4 = vi4;

			res.textureCoordIndex1 = vti1;
			res.textureCoordIndex2 = vti2;
			res.textureCoordIndex3 = vti3;
			res.textureCoordIndex4 = vti4;

			return res;

		}

		Face4()
		{
			vertexIndex1 = 0;
			vertexIndex2 = 0;
			vertexIndex3 = 0;
			vertexIndex4 = 0;

			normalIndex1 = 0;
			normalIndex2 = 0;
			normalIndex3 = 0;
			normalIndex4 = 0;

			textureCoordIndex1 = 0;
			textureCoordIndex2 = 0;
			textureCoordIndex3 = 0;
			textureCoordIndex4 = 0;

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
		vector<Face4> faces4;
	public:
		const vector<glm::vec3>& getVertices() const { return vertices; }
		const vector<glm::vec3>& getNormals() const { return normals; }
		const vector<glm::vec2>& getTextureCoords() const { return textureCoords; }
		const vector<Face3> getFaces3() const { return faces3; }
		const vector<Face4> getFaces4() const { return faces4; }

		void addVertex(glm::vec3 vertex)
		{
			vertices.push_back(vertex);
		}
		void addNormal(glm::vec3 normal)
		{
			normals.push_back(normal);
		}
		void addTextureCoords(glm::vec2 texCoord)
		{
			textureCoords.push_back(texCoord);
		}

		bool checkFace3(const Face3& face)
		{
			unsigned int verticesCount = vertices.size();


			if (face.vertexIndex1 >= verticesCount)	return false;
			if (face.vertexIndex2 >= verticesCount)	return false;
			if (face.vertexIndex3 >= verticesCount)	return false;

			if (face.containsNormals)
			{
				unsigned int normalsCount = normals.size();

				if (face.normalIndex1 >= normalsCount)	return false;
				if (face.normalIndex2 >= normalsCount)	return false;
				if (face.normalIndex3 >= normalsCount)	return false;
			}

			if (face.containsTextureCoords)
			{
				unsigned int textureCoordsCount = textureCoords.size();

				if (face.textureCoordIndex1 >= textureCoordsCount)	return false;
				if (face.textureCoordIndex2 >= textureCoordsCount)	return false;
				if (face.textureCoordIndex3 >= textureCoordsCount)	return false;
			}

			return true;
		}

		bool checkFace4(const Face4& face)
		{
			unsigned int verticesCount = vertices.size();

			if (face.vertexIndex1 >= verticesCount)	return false;
			if (face.vertexIndex2 >= verticesCount)	return false;
			if (face.vertexIndex3 >= verticesCount)	return false;
			if (face.vertexIndex4 >= verticesCount)	return false;

			if (face.containsNormals)
			{
				unsigned int normalsCount = normals.size();

				if (face.normalIndex1 >= normalsCount)	return false;
				if (face.normalIndex2 >= normalsCount)	return false;
				if (face.normalIndex3 >= normalsCount)	return false;
				if (face.normalIndex4 >= normalsCount)	return false;
			}

			if (face.containsTextureCoords)
			{
				unsigned int textureCoordsCount = textureCoords.size();

				if (face.textureCoordIndex1 >= textureCoordsCount)	return false;
				if (face.textureCoordIndex2 >= textureCoordsCount)	return false;
				if (face.textureCoordIndex3 >= textureCoordsCount)	return false;
				if (face.textureCoordIndex4 >= textureCoordsCount)	return false;
			}

			return true;
		}

		void addFace3(Face3 face)
		{
			if (!checkFace3(face))
			{
				throw CubexException("Incorrect Face3 data");
			}
			faces3.push_back(face);
		}
		void addFace4(Face4 face)
		{

			if (!checkFace4(face))
			{
				throw CubexException("Incorrect Face4 data");
			}
			faces4.push_back(face);
		}
	};

	class MeshObjLoader
	{
	public:
		MeshObjLoader();
		Mesh createMeshFromFile(const string& fileName);
		virtual ~MeshObjLoader();
	};

} /* namespace cubex */
#endif /* MESHOBJLOADER_H_ */
