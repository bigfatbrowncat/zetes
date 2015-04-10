#include <stdlib.h>
#include <iostream>
#include <litehtml.h>

#include "JNI_LiteHTML.h"
#include "JNI_Position.h"

static jmethodID positionConstructor = NULL;

jclass getPositionClass(JNIEnv* env) {
	return env->FindClass(LITEHTML_PACKAGE "/Position");
}

jmethodID getPositionConstructor(JNIEnv* env) {
	jclass pc = getPositionClass(env);
	if (positionConstructor == NULL) {
		positionConstructor = env->GetMethodID(pc, "<init>", "(IIII)V");
	}
	return positionConstructor;
}


jobject positionFromNative(JNIEnv* env, const litehtml::position& pos) {
	return env->NewObject(getPositionClass(env), getPositionConstructor(env), pos.x, pos.y, pos.width, pos.height);
}

litehtml::position positionToNative(JNIEnv* env, jobject jpos) {
	jclass posClass = getPositionClass(env);
	jfieldID x = env->GetFieldID(posClass, "x", "I");
	jfieldID y = env->GetFieldID(posClass, "y", "I");
	jfieldID width = env->GetFieldID(posClass, "width", "I");
	jfieldID height = env->GetFieldID(posClass, "height", "I");

	return litehtml::position(env->GetIntField(jpos, x),
	                          env->GetIntField(jpos, y),
							  env->GetIntField(jpos, width),
							  env->GetIntField(jpos, height));
}
