/*
 * JNI_Position.cpp
 *
 *  Created on: 02 апр. 2015 г.
 *      Author: imizus
 */

#include <stdlib.h>
#include <litehtml.h>

#include "JNI_LiteHTML.h"
#include "JNI_FontMetrics.h"

static jmethodID fontMetricsConstructor = NULL;

jclass getFontMetricsClass(JNIEnv* env) {
	return env->FindClass(LITEHTML_PACKAGE "/FontMetrics");
}

jmethodID getFontMetricsConstructor(JNIEnv* env) {
	jclass pc = getFontMetricsClass(env);
	if (fontMetricsConstructor == NULL) {
		fontMetricsConstructor = env->GetMethodID(pc, "<init>", "(IIIIZ)V");
	}
	return fontMetricsConstructor;
}


jobject fontMetricsFromNative(JNIEnv* env, const litehtml::font_metrics& fm) {
	return env->NewObject(getFontMetricsClass(env),
	                      getFontMetricsConstructor(env),
	                      fm.height, fm.ascent, fm.descent, fm.x_height, fm.draw_spaces);
}

litehtml::font_metrics fontMetricsToNative(JNIEnv* env, jobject jfm) {
	jclass posClass = getFontMetricsClass(env);
	jfieldID height = env->GetFieldID(posClass, "height", "I");
	jfieldID ascent = env->GetFieldID(posClass, "ascent", "I");
	jfieldID descent = env->GetFieldID(posClass, "descent", "I");
	jfieldID xHeight = env->GetFieldID(posClass, "xHeight", "I");
	jfieldID drawSpaces = env->GetFieldID(posClass, "drawSpaces", "Z");

	litehtml::font_metrics fm;
	fm.height = env->GetIntField(jfm, height);
	fm.ascent = env->GetIntField(jfm, ascent);
	fm.descent = env->GetIntField(jfm, descent);
	fm.x_height = env->GetIntField(jfm, xHeight);
	fm.draw_spaces = env->GetBooleanField(jfm, drawSpaces);

	return fm;
}
