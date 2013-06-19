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
		string message;
	public:
		CubexException(const string& message);
		const string& getMessage() const { return message; }
		virtual ~CubexException();
	};

} /* namespace cubex */
#endif /* CUBEXEXCEPTION_H_ */
