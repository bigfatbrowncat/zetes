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
}
