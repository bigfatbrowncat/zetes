#include <stdlib.h>
#include <iostream>
#include <litehtml.h>

#include "JNI_LiteHTML.h"
#include "JNI_Position.h"

static jmethodID sizeConstructor = NULL;

jclass getSizeClass(JNIEnv* env) {
	return env->FindClass(LITEHTML_PACKAGE "/Size");
}

jmethodID getSizeConstructor(JNIEnv* env) {
	jclass pc = getSizeClass(env);
	if (sizeConstructor == NULL) {
		sizeConstructor = env->GetMethodID(pc, "<init>", "(II)V");
	}
	return sizeConstructor;
}

jobject sizeFromNative(JNIEnv* env, const litehtml::size& sz) {
	return env->NewObject(getSizeClass(env), getSizeConstructor(env), sz.width, sz.height);
}

litehtml::size sizeToNative(JNIEnv* env, jobject jsz) {
	jclass szClass = getSizeClass(env);
	jfieldID width = env->GetFieldID(szClass, "width", "I");
	jfieldID height = env->GetFieldID(szClass, "height", "I");

	litehtml::size sz = litehtml::size();
	sz.width = env->GetIntField(jsz, width);
	sz.height = env->GetIntField(jsz, height);
	return sz;
}
