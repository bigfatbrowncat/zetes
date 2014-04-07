// Global includes/methods

#include <sstream>
#include <string>

#include "WinLinMacApi.h"

using namespace std;

string simpleLocateResource(const string& path, const string& filename)
{
	stringstream ss;
	ss << path << "/" << filename;

	return ss.str();
}


#ifdef __APPLE__
// MacOS X includes/methods

#include <sys/syslimits.h>

#include <CoreFoundation/CFBundle.h>

string WinLinMacApi::locateResource(const string& path, const string& filename)
{
	string res = "";
	CFBundleRef mainBundle = CFBundleGetMainBundle();
	if (mainBundle)
	{
		CFStringRef cfFileName = CFStringCreateWithCString(NULL, filename.c_str(), kCFStringEncodingASCII);
		CFStringRef cfPath = CFStringCreateWithCString(NULL, path.c_str(), kCFStringEncodingASCII);

		CFURLRef resourceURL = CFBundleCopyResourceURL(mainBundle, cfFileName, NULL, cfPath);
		if (resourceURL)
		{
			char fileurl[PATH_MAX + 1];
			if (CFURLGetFileSystemRepresentation(resourceURL, true, (UInt8*)fileurl, PATH_MAX))
			{
				res = fileurl;
			}
			else
			{
				res = simpleLocateResource(path, filename);
			}
			CFRelease(resourceURL);
		}
		else
		{
			res = simpleLocateResource(path, filename);

		}
		CFRelease(cfFileName);
		CFRelease(cfPath);
	}
	else
	{
		// from the current directory
		res = simpleLocateResource(path, filename);
	}
	return res;
}

string WinLinMacApi::locateExecutable()
{
	string res = "";
	CFBundleRef mainBundle = CFBundleGetMainBundle();
	if (mainBundle)
	{
		CFURLRef executableURL = CFBundleCopyExecutableURL(mainBundle);	// CFBundleCopyBundleURL
		if (executableURL)
		{
			CFURLRef executablePathUrl = CFURLCreateCopyDeletingLastPathComponent(kCFAllocatorDefault, executableURL);

			CFStringRef executablePath = CFURLCopyFileSystemPath(executablePathUrl, kCFURLPOSIXPathStyle);	// CFURLCopyFileSystemPath
			if (executablePath)
			{
				char fileurl[PATH_MAX + 1];
				if (CFStringGetCString(executablePath, fileurl, PATH_MAX, kCFStringEncodingUTF8))
				{
					res = fileurl;
				}
				else
				{
					res = "./";
				}
				CFRelease(executablePath);
			}
			else
			{
				res = "./";
			}
			CFRelease(executablePathUrl);
			CFRelease(executableURL);
		}
	}
	else
	{
		// from the current directory
		res = "./";
	}
	return res;
}

#elif __WIN32__

// Win32 includes/methods
#include <windows.h>
#include <direct.h>

BOOL FileExists(string szPath)
{
  DWORD dwAttrib = GetFileAttributes(szPath.c_str());

  return (dwAttrib != INVALID_FILE_ATTRIBUTES &&
         !(dwAttrib & FILE_ATTRIBUTE_DIRECTORY));
}

string WinLinMacApi::locateExecutable()
{
	char szAppPath[MAX_PATH] = "";
	char szAppDirectory[MAX_PATH]= "";

	if (!GetModuleFileName(NULL, szAppPath, MAX_PATH - 1))
	{
		// Trying to locate the resource locally...
		return ".\\";
	}

	strncpy(szAppDirectory, szAppPath, strrchr(szAppPath, '\\') - szAppPath);
	szAppDirectory[MAX_PATH - 1] = '\0';				// For sure...


	return szAppDirectory;
}

string WinLinMacApi::locateResource(const string& path, const string& filename)
{
	string appDirectory = locateExecutable();

	stringstream ss;
	ss << appDirectory << "\\" << path << "\\" << filename;

	if (!FileExists(ss.str()))
	{
		// Trying to locate the resource locally...
		return simpleLocateResource(path, filename);
	}

	return ss.str();
}

HANDLE WinLinMacApi::globalLock(string name)
{
	HANDLE hMutex = CreateMutex(NULL, false, name.c_str());
	if (hMutex == NULL)
	{
		printf("Can't create mutex named %s. Error: %d\n", name.c_str(), GetLastError());
		return NULL;
	}

	bool res = (WaitForSingleObject(hMutex, INFINITE) == WAIT_OBJECT_0);

	return hMutex;
}

bool WinLinMacApi::isLocked(string name)
{
	HANDLE hMutex = CreateMutex(NULL, false, name.c_str());
	if (hMutex == NULL)
	{
		printf("Can't create mutex named %s. Error: %d\n", name.c_str(), GetLastError());
		return NULL;
	}

	bool res;
	int w = WaitForSingleObject(hMutex, 0);
	switch (w)
	{
	case WAIT_OBJECT_0:
		res = false;
		break;
	case WAIT_TIMEOUT:
		res = true;
		break;
	default:
		printf("Problem with mutex\n");
		break;
	}

	return res;
}

bool WinLinMacApi::globalUnlock(HANDLE hMutex)
{
	bool res = ReleaseMutex(hMutex);
	CloseHandle(hMutex);
	return res;
}

string WinLinMacApi::readFromPipe(string name)
{
	string res = "";

	HANDLE hPipe = CreateNamedPipe(name.c_str(),             // pipe name
			PIPE_ACCESS_DUPLEX,       // read/write access
			PIPE_TYPE_MESSAGE |       // message type pipe
					PIPE_READMODE_MESSAGE |   // message-read mode
					PIPE_WAIT,                // blocking mode
			PIPE_UNLIMITED_INSTANCES, // max. instances
			BUFFER_SIZE,               // output buffer size
			BUFFER_SIZE,                  // input buffer size
			SERVER_WAIT,                        // client time-out
			NULL);

	if (hPipe == INVALID_HANDLE_VALUE)
	{
		printf("We have a problem while creating an inbound pipe: %d\n", GetLastError());
		return res;
	}

	bool fConnected = ConnectNamedPipe(hPipe, NULL) ? TRUE : (GetLastError() == ERROR_PIPE_CONNECTED);

	if (fConnected)
	{
		char bufferToReceive[BUFFER_SIZE + 1] = { 0 };
		bufferToReceive[BUFFER_SIZE] = 0;
		unsigned long bytesRead = 0;

		bool fSuccess = true;
		do
		{
			bufferToReceive[bytesRead] = 0;
			res += bufferToReceive;

			fSuccess = ReadFile(hPipe,        		// handle to pipe
					bufferToReceive,   			// buffer to receive data
					BUFFER_SIZE * sizeof(char),	// size of buffer
					&bytesRead,					// number of bytes read
					NULL);						// not overlapped I/O

		}
		while (bytesRead > 0);

		FlushFileBuffers(hPipe);
		DisconnectNamedPipe(hPipe);
	}
	else
	{
		printf("Can't connect the pipe\n");
	}

	CloseHandle(hPipe);

	return res;
}

bool WinLinMacApi::writeToPipe(string name, string textToWrite)
{
	bool res = true;

	bool waitSuccess = false;
	for (int attempt = 0; attempt < ATTEMPTS; attempt++)
	{
		if (WaitNamedPipe(name.c_str(), 0))
		{
			waitSuccess = true;
			break;
		}
		Sleep(10);
	}
	if (!waitSuccess) return false;

	HANDLE hPipe = CreateFile(
		name.c_str(),
		GENERIC_WRITE, // only need read access
		FILE_SHARE_READ | FILE_SHARE_WRITE,
		NULL,
		OPEN_EXISTING,
		FILE_ATTRIBUTE_NORMAL,
		NULL
	);

	if (hPipe == INVALID_HANDLE_VALUE)
	{
		printf("We have a problem while creating an outbound pipe: %d\n", GetLastError());
		return false;
	}

	unsigned long written = 0;

	char toWrite[textToWrite.length() + 2];
	strcpy(toWrite, textToWrite.c_str());
	int fSuccess = WriteFile(hPipe, toWrite, textToWrite.length(), &written, NULL);

	if (!fSuccess || textToWrite.length() != written)
	{
		printf("We can't write\n");
		res = false;
	}

	CloseHandle(hPipe);
	return res;
}

#else
// Linux includes/methods
#include <unistd.h>
#include <string.h>
#include <linux/limits.h>
#include <sys/file.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/stat.h>

string WinLinMacApi::locateExecutable()
{
	char arg[20];
	char exepath[PATH_MAX + 1];
	exepath[0] = 0;

	sprintf(arg, "/proc/%d/exe", getpid());
	readlink(arg, exepath, PATH_MAX);

	for (int i = strlen(exepath) - 1; i >= 0; i--)
	{
		if (exepath[i] == '/')
		{
			exepath[i] = 0;
			break;
		}
	}

	return exepath;
}

string WinLinMacApi::locateResource(const string& path, const string& filename)
{
	string appDirectory = locateExecutable();

	stringstream ss;
	ss << appDirectory << "/" << path << "/" << filename;

	return ss.str();
}

LOCK_HANDLE WinLinMacApi::globalLock(string name)
{
	string fullName = (string)"/tmp/.lock_" + name;
	int fd;
	while (true) {
		fd = open(fullName.c_str(), O_WRONLY | O_CREAT);
		if (fd == -1) {
			if (errno == EINTR) {
				// interrupted, try again
				continue;
			}
			printf("Can't create file named %s. Error: %d\n", fullName.c_str(), errno);
			return -1;
		}
		break;
	}

	while (true) {
		if (flock(fd, LOCK_EX | LOCK_NB) == -1) {
			if (errno == EINTR) {
				// syscall was interrupted, try again
				continue;
			}
			printf("Can't lock file named %s. Error: %d\n", fullName.c_str(), errno);
			return -1;
		}
		return fd;
	}
}

static bool retryingClose(int fd) {
	int rc;
	while (true) {
		rc = close(fd);
		if (rc != 0 && errno == EINTR) {
			continue;
		}
		return rc == 0;
	}
}

bool WinLinMacApi::globalUnlock(LOCK_HANDLE hMutex)
{
	return retryingClose(hMutex);
}

bool WinLinMacApi::isLocked(string name)
{
	string fullName = (string)"/tmp/.lock_" + name;
	int fd;
	while (true) {
		fd = open(fullName.c_str(), O_RDONLY);
		if (fd == -1) {
			switch (errno) {
				case EINTR:
					// interrupted, try again
					continue;
				case ENOENT:
					// lock file doesn't exist
					return false;
				default:
					break;
			}
			printf("Can't open file named %s. Error: %d\n", fullName.c_str(), errno);
			return false;
		}
		break;
	}

	while (true) {
		if (flock(fd, LOCK_EX | LOCK_NB) == -1) {
			switch (errno) {
				case EINTR:
					// syscall was interrupted, try again
					continue;
				case EWOULDBLOCK:
					// file is locked
					return true;
				default:
					break;
			}
			printf("Can't check lock on file named %s. Error: %d\n", fullName.c_str(), errno);
			return false;
		}
		break;
	}
	// if we got here this means we were able to lock the file... release it, then
	retryingClose(fd);
	return false;
}

string WinLinMacApi::readFromPipe(string name)
{
	string res = "";

	string fullName = (string)"/tmp/.fifo_" + name;

	int mf = mkfifo(fullName.c_str(), 0666);
	if (mf != 0)
	{
		if (errno != EEXIST)
		{
			printf("Can't create fifo named %s. Error: %d\n", fullName.c_str(), errno);
			return res;
		}
	}
	
	int fd = open(fullName.c_str(), O_RDONLY);
	if (fd == -1)
	{
		printf("Can't open fifo named %s for reading. Error: %d\n", fullName.c_str(), errno);
		return res;
	}	

	char bufferToReceive[BUFFER_SIZE + 1] = { 0 };
	bufferToReceive[BUFFER_SIZE] = 0;
	size_t bytesRead = 0;

	do
	{
		bufferToReceive[bytesRead] = 0;
		res += bufferToReceive;

		bytesRead = read(fd, bufferToReceive, BUFFER_SIZE * sizeof(char));
	}
	while (bytesRead > 0);
	
	return res;
}

bool WinLinMacApi::writeToPipe(string name, string textToWrite)
{
	string fullName = (string)"/tmp/.fifo_" + name;

	int mf = mkfifo(fullName.c_str(), 0666);
	if (mf != 0)
	{
		if (errno != EEXIST)
		{
			printf("Can't create fifo named %s. Error: %d\n", fullName.c_str(), errno);
			return false;
		}
	}

	FILE* fd = fopen(fullName.c_str(), "w");
	if (fd == NULL)
	{
		printf("Can't open fifo named %s for writing. Error: %d\n", fullName.c_str(), errno);
		return false;
	}
	fprintf(fd, "%s", textToWrite.c_str());
	fclose(fd);
	return true;
}

#endif
