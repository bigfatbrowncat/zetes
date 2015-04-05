/*
 * JNI_Position.h
 *
 *  Created on: 02 апр. 2015 г.
 *      Author: imizus
 */

#ifndef CPP_LITEHTML_JNI_JNI_POSITION_H_
#define CPP_LITEHTML_JNI_JNI_POSITION_H_

#include <jni.h>

jobject positionFromNative(JNIEnv* env, const litehtml::position& pos);
litehtml::position positionToNative(JNIEnv* env, jobject jpos);

#endif /* CPP_LITEHTML_JNI_JNI_POSITION_H_ */
