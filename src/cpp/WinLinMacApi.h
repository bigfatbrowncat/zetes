/*
 * resourcelocator.h
 *
 *  Created on: 02.12.2012
 *      Author: Ilya Mizus
 */

#include <string>

#ifdef __MINGW32__
#include "Windows.h"
#endif

using namespace std;

#ifndef RESOURCELOCATOR_H_
#define RESOURCELOCATOR_H_

#define BUFFER_SIZE		4096
#define ATTEMPTS		1000
#define SERVER_WAIT		30000

class WinLinMacApi
{
public:
	static string locateResource(const string& path, const string& filename);
	static string locateExecutable();

#ifndef __APPLE__
	// We don't need pipes on OS X cause we have a default
	// multi-document handling mechanism there
	static HANDLE globalLock(string name);
	static bool globalUnlock(HANDLE hMutex);
	static bool isLocked(string name);

	static string readFromPipe(string name);
	static bool writeToPipe(string name, string textToWrite);
#endif
};

#endif /* RESOURCELOCATOR_H_ */
