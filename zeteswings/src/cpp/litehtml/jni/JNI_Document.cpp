/*
 * JNI_Document.cpp
 *
 *  Created on: 02 апр. 2015 г.
 *      Author: imizus
 */
#include <iostream>
#include <jni.h>

#include <litehtml.h>
#include "JNI_Position.h"

using namespace litehtml;

extern "C" {
	JNIEXPORT jlong JNICALL Java_zetes_wings_litehtml_jni_Document_createNativeFromString(JNIEnv* env, jobject obj, jstring html, jstring masterCSS, jlong container) {
		const char* htmlChars = env->GetStringUTFChars(html, NULL);
		const char* masterCSSChars = env->GetStringUTFChars(masterCSS, NULL);
		document_container* cont = (document_container*)container;

		context* ctx = new context();
		ctx->load_master_stylesheet(masterCSSChars);
		document::ptr doc = document::createFromUTF8(htmlChars, cont, ctx, NULL);
		doc->addRef();

		env->ReleaseStringUTFChars(html, htmlChars);
		env->ReleaseStringUTFChars(masterCSS, masterCSSChars);

		return (jlong)((document*)doc);
	}

	JNIEXPORT void JNICALL Java_zetes_wings_litehtml_jni_Document_destroyNative(JNIEnv* env, jclass clz, jlong ptr) {
		document* doc = (document*)ptr;
		doc->release();
	}

	JNIEXPORT void JNICALL Java_zetes_wings_litehtml_jni_Document_nativeDraw(JNIEnv* env, jclass clz, jlong nativeObject, jlong hdc, int x, int y, jobject clipJPos) {
		document* doc = (document*)nativeObject;
		litehtml::position clip = positionToNative(env, clipJPos);
		doc->draw((uint_ptr)hdc, x, y, &clip);
	}

	JNIEXPORT int JNICALL Java_zetes_wings_litehtml_jni_Document_nativeRender(JNIEnv* env, jclass clz, jlong nativeObject, int maxWidth) {
		document* doc = (document*)nativeObject;
		return doc->render(maxWidth);
	}

	JNIEXPORT int JNICALL Java_zetes_wings_litehtml_jni_Document_nativeWidth(JNIEnv* env, jclass clz, jlong nativeObject) {
		document* doc = (document*)nativeObject;
		return doc->width();
	}

	JNIEXPORT int JNICALL Java_zetes_wings_litehtml_jni_Document_nativeHeight(JNIEnv* env, jclass clz, jlong nativeObject) {
		document* doc = (document*)nativeObject;
		return doc->height();
	}
}
