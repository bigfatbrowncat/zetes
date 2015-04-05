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
#include "JNI_WebColor.h"

static jmethodID backgroundPaintConstructor = NULL;

jclass getBackgroundPaintClass(JNIEnv* env) {
	return env->FindClass(LITEHTML_PACKAGE "/BackgroundPaint");
}

jmethodID getBackgroundPaintConstructor(JNIEnv* env) {
	jclass pc = getBackgroundPaintClass(env);
	if (backgroundPaintConstructor == NULL) {
		backgroundPaintConstructor = env->GetMethodID(pc, "<init>", "(L" LITEHTML_PACKAGE "/WebColor;L" LITEHTML_PACKAGE "/Position;)V");
	}
	return backgroundPaintConstructor;
}

jobject backgroundPaintFromNative(JNIEnv* env, const litehtml::background_paint& bp) {
	jobject jwclr = webColorFromNative(env, bp.color);
	jobject jbbox = positionFromNative(env, bp.border_box);
	return env->NewObject(getBackgroundPaintClass(env), getBackgroundPaintConstructor(env), jwclr, jbbox);
}

litehtml::background_paint backgroundPaintToNative(JNIEnv* env, jobject jbp) {
	jclass posClass = getBackgroundPaintClass(env);
	jfieldID colorField = env->GetFieldID(posClass, "color", LITEHTML_PACKAGE "/WebColor");
	litehtml::web_color color = webColorToNative(env, env->GetObjectField(jbp, colorField));
	litehtml::background_paint res;
	res.color = color;
	return res;
}
