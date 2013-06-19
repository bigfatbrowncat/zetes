/*
 * MeshBuffer.h
 *
 *  Created on: 18.06.2013
 *      Author: il
 */

#ifndef MESHBUFFER_H_
#define MESHBUFFER_H_

#include "MeshObjLoader.h"

namespace cubex {

class MeshBuffer {
private:
	GLuint VertexArrayID;
	GLuint vertexBufferObject;
	float* buffer;
public:
	MeshBuffer(const Mesh &mesh);
	virtual ~MeshBuffer();
	void bind();
};

} /* namespace cubex */
#endif /* MESHBUFFER_H_ */
