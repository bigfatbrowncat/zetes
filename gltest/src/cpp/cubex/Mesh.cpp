/*
 * Mesh.cpp
 *
 *  Created on: Jun 19, 2013
 *      Author: imizus
 */

#include "Mesh.h"

namespace cubex
{
	void Mesh::addVertex(glm::vec3 vertex)
	{
		vertices.push_back(vertex);
	}

	void Mesh::addNormal(glm::vec3 normal)
	{
		normals.push_back(normal);
	}

	void Mesh::addTextureCoords(glm::vec2 texCoord)
	{
		textureCoords.push_back(texCoord);
	}

	bool Mesh::checkFace(const Face& face)
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

	void Mesh::addFace(const Face& face)
	{
		if (!checkFace(face))
		{
			throw CubexException(__FILE__, __LINE__, "Incorrect face data");
		}
		faces.push_back(face);
	}

}
