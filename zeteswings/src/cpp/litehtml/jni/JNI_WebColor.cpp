/*
 * JNI_Position.cpp
 *
 *  Created on: 02 апр. 2015 г.
 *      Author: imizus
 */

#include <stdlib.h>
#include <litehtml.h>

#include "JNI_LiteHTML.h"
#include "JNI_Position.h"

static jmethodID webColorConstructor = NULL;

jclass getWebColorClass(JNIEnv* env) {
	return env->FindClass(LITEHTML_PACKAGE "/WebColor");
}

jmethodID getWebColorConstructor(JNIEnv* env) {
	jclass pc = getWebColorClass(env);
	if (webColorConstructor == NULL) {
		webColorConstructor = env->GetMethodID(pc, "<init>", "(SSSS)V");
	}
	return webColorConstructor;
}


jobject webColorFromNative(JNIEnv* env, const litehtml::web_color& wclr) {
	return env->NewObject(getWebColorClass(env), getWebColorConstructor(env), wclr.red, wclr.green, wclr.blue, wclr.alpha);
}

litehtml::web_color webColorToNative(JNIEnv* env, jobject jwclr) {
	jclass posClass = getWebColorClass(env);
	jfieldID red = env->GetFieldID(posClass, "red", "S");
	jfieldID green = env->GetFieldID(posClass, "green", "S");
	jfieldID blue = env->GetFieldID(posClass, "blue", "S");
	jfieldID alpha = env->GetFieldID(posClass, "alpha", "S");
	return litehtml::web_color((char)env->GetShortField(jwclr, red),
	                           (char)env->GetShortField(jwclr, green),
							   (char)env->GetShortField(jwclr, blue),
							   (char)env->GetShortField(jwclr, alpha));
}
