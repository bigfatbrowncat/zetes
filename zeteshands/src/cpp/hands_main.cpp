#include <stdio.h>
#include <stdint.h>
#include <string.h>

#include <string>
#include <map>

#include "WinLinMacApi.h"
#include "FeetStarter.h"
#include "ZetesHands.h"

using namespace std;
using namespace zetes::feet;

#ifdef __MINGW32__
#include <windows.h>
#endif


int main(int argc, const char** argv)
{
	FeetStarter feetStarter;

#ifdef __MINGW32__
	// For Windows: Getting command line as a wide string
	int wac = 0;
	wchar_t** wav;
	wav = CommandLineToArgvW(GetCommandLineW(), &wac);
#else
	// For POSIX OSes: Getting command line as a plain string (encoded in UTF8)
	int wac = argc;
	const char** wav = argv;
#endif
	list<argstring>& args = feetStarter.takeArguments();

	// wav[0] is the program name which shouldn't be sent to Java code
	for (int i = 1; i < wac; i++)
	{
		args.push_back(wav[i]);
	}
	
	feetStarter.setDefinition("java.library.path", WinLinMacApi::locateExecutable());

	//feetStarter.setApplicationClassName(zetes::hands::applicationClass);

#ifdef __MINGW32__
	SetConsoleCP(65001);		// UTF-8
	SetConsoleOutputCP(65001);	// UTF-8 too
#endif

	int exitCode = feetStarter.run();

	return exitCode;
}
