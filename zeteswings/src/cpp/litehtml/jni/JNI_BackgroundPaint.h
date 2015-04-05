/*
 * JNI_Position.h
 *
 *  Created on: 02 апр. 2015 г.
 *      Author: imizus
 */

#ifndef CPP_DEMO_JNI_BACKGROUND_PAINT_H_
#define CPP_DEMO_JNI_BACKGROUND_PAINT_H_

#include <jni.h>

jobject backgroundPaintFromNative(JNIEnv* env, const litehtml::background_paint& bp);
litehtml::background_paint backgroundPaintToNative(JNIEnv* env, jobject jbp);

#endif /* CPP_DEMO_JNI_POSITION_H_ */
