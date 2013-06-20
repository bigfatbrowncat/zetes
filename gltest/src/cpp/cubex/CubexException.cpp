/*
 * CubexException.cpp
 *
 *  Created on: 17.06.2013
 *      Author: il
 */

#include "CubexException.h"

namespace cubex
{

	CubexException::CubexException(const string& message)
	{
		this->message = message;
	}

	CubexException::~CubexException()
	{

	}

}
