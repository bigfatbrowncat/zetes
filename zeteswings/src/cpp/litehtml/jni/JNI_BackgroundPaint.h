#ifndef CPP_DEMO_JNI_BACKGROUND_PAINT_H_
#define CPP_DEMO_JNI_BACKGROUND_PAINT_H_

#include <jni.h>

jobject backgroundPaintFromNative(JNIEnv* env, const litehtml::background_paint& bp);
//litehtml::background_paint backgroundPaintToNative(JNIEnv* env, jobject jbp);

#endif
