/*
 * CubexException.cpp
 *
 *  Created on: 17.06.2013
 *      Author: il
 */

#include <sstream>

#include "CubexException.h"

using namespace std;

namespace cubex
{

	CubexException::CubexException(const string& fileName, int lineNumber, const string& message) :
			fileName(fileName), lineNumber(lineNumber), message(message)
	{

	}

	const string CubexException::getReport() const
	{
		stringstream ss;
		ss << "Cubex exception occured in file " << fileName << " at line " << lineNumber << ": " << message;
		return ss.str();
	}

	CubexException::~CubexException()
	{

	}

}
