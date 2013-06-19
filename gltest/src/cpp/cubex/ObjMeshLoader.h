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
#include "Mesh.h"

using namespace std;

namespace cubex
{
	class ObjMeshLoader
	{
	public:
		ObjMeshLoader();
		Mesh createMeshFromFile(const string& fileName);
		virtual ~ObjMeshLoader();
	};

} /* namespace cubex */
#endif /* MESHOBJLOADER_H_ */
