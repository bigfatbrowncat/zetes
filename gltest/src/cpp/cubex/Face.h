/*
 * Face.h
 *
 *  Created on: Jun 20, 2013
 *      Author: imizus
 */

#ifndef FACE_H_
#define FACE_H_

namespace cubex
{
	struct Face
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

		static Face fromVertices(unsigned int vi1, unsigned int vi2, unsigned int vi3);

		static Face fromVerticesAndNormals(unsigned int vi1,  unsigned int vi2,  unsigned int vi3,
		                                   unsigned int vni1, unsigned int vni2, unsigned int vni3);

		static Face fromVerticesAndTextureCoordsAndNormals(unsigned int vi1,  unsigned int vi2,  unsigned int vi3,
		                                                   unsigned int vti1, unsigned int vti2, unsigned int vti3,
		                                                   unsigned int vni1, unsigned int vni2, unsigned int vni3);

		static Face fromVerticesAndTextureCoords(unsigned int vi1,  unsigned int vi2,  unsigned int vi3,
		                                         unsigned int vti1, unsigned int vti2, unsigned int vti3);

		Face();
	};
}
#endif
