/*
 * Face.cpp
 *
 *  Created on: Jun 20, 2013
 *      Author: imizus
 */

#include "Face.h"

namespace cubex
{

	Face Face::fromVertices(unsigned int vi1, unsigned int vi2, unsigned int vi3)
	{
		Face res;

		res.containsNormals = false;
		res.containsTextureCoords = false;

		res.vertexIndex1 = vi1;
		res.vertexIndex2 = vi2;
		res.vertexIndex3 = vi3;

		return res;
	}

	Face Face::fromVerticesAndNormals(unsigned int vi1, unsigned int vi2, unsigned int vi3,
										unsigned int vni1, unsigned int vni2, unsigned int vni3)
	{
		Face res;

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

	Face Face::fromVerticesAndTextureCoordsAndNormals(unsigned int vi1, unsigned int vi2, unsigned int vi3,
	                                                  unsigned int vti1, unsigned int vti2, unsigned int vti3,
	                                                  unsigned int vni1, unsigned int vni2, unsigned int vni3)
	{
		Face res;

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

	Face Face::fromVerticesAndTextureCoords(unsigned int vi1, unsigned int vi2, unsigned int vi3,
	                                        unsigned int vti1, unsigned int vti2, unsigned int vti3)
	{
		Face res;

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

	Face::Face()
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

}
