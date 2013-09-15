#include <jni.h>
#include <math.h>

char* applicationClass = const_cast<char*>("bellardpi/Main");

extern "C" JNIEXPORT jdouble JNICALL
Java_java_lang_Math_log(JNIEnv*, jclass, jdouble val)
{
  return log(val);
}
