#include <jni.h>
#include <string>

using namespace std;

#include "WinLinMacApi.h"

#ifndef __APPLE__

extern "C" JNIEXPORT jstring JNICALL Java_crossbase_MDIHelper_readFromPipe(JNIEnv * env, jclass appClass, jstring name)
{
	const char *str= env->GetStringUTFChars(name, 0);
	string s = WinLinMacApi::readFromPipe(string(str));
	jstring res = env->NewStringUTF(s.c_str());
	env->ReleaseStringUTFChars(name, str);

	return res;
}

extern "C" JNIEXPORT jboolean JNICALL Java_crossbase_MDIHelper_writeToPipe(JNIEnv * env, jclass appClass, jstring name, jstring data)
{
	const char *str= env->GetStringUTFChars(name, 0);
	const char *data_str= env->GetStringUTFChars(data, 0);
	bool res = WinLinMacApi::writeToPipe(string(str), string(data_str));
	env->ReleaseStringUTFChars(name, str);
	env->ReleaseStringUTFChars(data, data_str);

	return res;
}

#endif
