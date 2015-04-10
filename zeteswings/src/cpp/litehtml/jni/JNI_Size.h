#ifndef CPP_LITEHTML_JNI_SIZE_H_
#define CPP_LITEHTML_JNI_SIZE_H_

#include <jni.h>

jobject sizeFromNative(JNIEnv* env, const litehtml::size& sz);
litehtml::size sizeToNative(JNIEnv* env, jobject jsz);

#endif /* CPP_LITEHTML_JNI_SIZE_H_ */
