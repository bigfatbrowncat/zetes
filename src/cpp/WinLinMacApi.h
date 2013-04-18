/*
 * resourcelocator.h
 *
 *  Created on: 02.12.2012
 *      Author: Ilya Mizus
 */

#include <string>

using namespace std;

#ifndef RESOURCELOCATOR_H_
#define RESOURCELOCATOR_H_

class WinLinMacApi
{
public:
	string locateResource(const string& path, const string& filename);
	string locateExecutable();

#ifndef __APPLE__
	static string readFromPipe(const char* name);
	static bool writeToPipe(string name, string textToWrite);
#endif
};

#endif /* RESOURCELOCATOR_H_ */
