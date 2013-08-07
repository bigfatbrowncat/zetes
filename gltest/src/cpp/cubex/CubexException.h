/*
 * CubexException.h
 *
 *  Created on: 17.06.2013
 *      Author: il
 */

#ifndef CUBEXEXCEPTION_H_
#define CUBEXEXCEPTION_H_

#include <string>

using namespace std;

namespace cubex
{

	class CubexException
	{
	private:
		string fileName;
		int lineNumber;
		string message;
	public:
		CubexException(const string& fileName, int lineNumber, const string& message);
		const string getReport() const;
		virtual ~CubexException();
	};

}
#endif
