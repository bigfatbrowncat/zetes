#include <jni.h>
#include <string>

using namespace std;

#include "WinLinMacApi.h"

#ifndef __APPLE__

extern "C"
{
	JNIEXPORT jlong JNICALL Java_zetes_wings_SingleAppInstanceDocumentHandler_globalLock(JNIEnv * env, jclass appClass, jstring name)
	{
		const char *str= env->GetStringUTFChars(name, 0);
		jlong res = (jlong)WinLinMacApi::globalLock(string(str));
		env->ReleaseStringUTFChars(name, str);

		return res;
	}

	JNIEXPORT jboolean JNICALL Java_zetes_wings_SingleAppInstanceDocumentHandler_globalUnlock(JNIEnv * env, jclass appClass, jlong hMutex)
	{
		return WinLinMacApi::globalUnlock((LOCK_HANDLE)hMutex);
	}

	JNIEXPORT jboolean JNICALL Java_zetes_wings_SingleAppInstanceDocumentHandler_isLocked(JNIEnv * env, jclass appClass, jstring name)
	{
		const char *str= env->GetStringUTFChars(name, 0);
		jboolean res = WinLinMacApi::isLocked(string(str));
		env->ReleaseStringUTFChars(name, str);

		return res;
	}

	JNIEXPORT jstring JNICALL Java_zetes_wings_SingleAppInstanceDocumentHandler_readFromPipe(JNIEnv * env, jclass appClass, jstring name)
	{
		const char *str= env->GetStringUTFChars(name, 0);
		string s = WinLinMacApi::readFromPipe(string(str));
		jstring res = env->NewStringUTF(s.c_str());
		env->ReleaseStringUTFChars(name, str);

		return res;
	}

	JNIEXPORT jboolean JNICALL Java_zetes_wings_SingleAppInstanceDocumentHandler_writeToPipe(JNIEnv * env, jclass appClass, jstring name, jstring data)
	{
		const char *str= env->GetStringUTFChars(name, 0);
		const char *data_str= env->GetStringUTFChars(data, 0);
		bool res = WinLinMacApi::writeToPipe(string(str), string(data_str));
		env->ReleaseStringUTFChars(name, str);
		env->ReleaseStringUTFChars(data, data_str);

		return res;
	}
}
#endif
