/*
 * JNI_Position.h
 *
 *  Created on: 02 апр. 2015 г.
 *      Author: imizus
 */

#ifndef CPP_DEMO_JNI_WEB_COLOR_H_
#define CPP_DEMO_JNI_WEB_COLOR_H_

#include <jni.h>

jobject webColorFromNative(JNIEnv* env, const litehtml::web_color& wclr);
litehtml::web_color webColorToNative(JNIEnv* env, jobject jwclr);

#endif /* CPP_DEMO_JNI_POSITION_H_ */
