/*
 * GLObject.h
 *
 *  Created on: 07 авг. 2013 г.
 *      Author: il
 */

#ifndef GLOBJECT_H_
#define GLOBJECT_H_

#include <string>

using namespace std;

namespace cubex {

class GLObject {
protected:
	void checkForError(const string& fileName, int lineNumber) const;
public:
	GLObject();
	virtual ~GLObject();
};

} /* namespace cubex */
#endif /* GLOBJECT_H_ */
