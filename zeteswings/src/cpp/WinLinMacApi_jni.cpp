#include <jni.h>
#include <string>

using namespace std;

#include "WinLinMacApi.h"

extern "C"
{
	JNIEXPORT jstring JNICALL Java_zetes_WinLinMacApi_locateExecutable(JNIEnv * env, jclass appClass)
	{
		 string s = WinLinMacApi::locateExecutable();
		 jstring res = env->NewStringUTF(s.c_str());
		 return res;
	}
	
	JNIEXPORT jstring JNICALL Java_zetes_WinLinMacApi_locateResource(JNIEnv * env, jclass appClass, jstring path, jstring filename)
	{
		const char* pathStr = env->GetStringUTFChars(path, 0);
		const char* filenameStr = env->GetStringUTFChars(filename, 0);

		string res = WinLinMacApi::locateResource(string(pathStr), string(filenameStr));
		
		env->ReleaseStringUTFChars(path, pathStr);
		env->ReleaseStringUTFChars(filename, filenameStr);

		return env->NewStringUTF(res.c_str());
	}
}
