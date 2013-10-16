/*
 * FeetStarter.cpp
 *
 *  Created on: 10 ????. 2013 ?.
 *      Author: il
 */

#include <jni.h>
#include <stdint.h>
#include <string.h>

#include <sstream>
#include <iostream>
#include <list>

#include "FeetStarter.h"

using namespace std;

#if (defined __MINGW32__)
#  define EXPORT __declspec(dllexport)
#else
#  define EXPORT __attribute__ ((visibility("default"))) \
  __attribute__ ((used))
#endif

#if (! defined __x86_64__) && (defined __MINGW32__)
#  define BOOT_JAR_SYMBOL(x)				binary_boot_jar_##x
#  define ENTRY_CLASS_NAME_SYMBOL(x)		boot_class_name_##x
#else
#  define BOOT_JAR_SYMBOL(x) 				_binary_boot_jar_##x
#  define ENTRY_CLASS_NAME_SYMBOL(x)		_boot_class_name_##x
#endif

extern "C"
{

	extern const uint8_t BOOT_JAR_SYMBOL(start)[];
	extern const uint8_t BOOT_JAR_SYMBOL(end)[];
	extern const char ENTRY_CLASS_NAME_SYMBOL(start)[];
	extern const char ENTRY_CLASS_NAME_SYMBOL(end)[];


	EXPORT const uint8_t* bootJar(unsigned* size)
	{
		*size = BOOT_JAR_SYMBOL(end) - BOOT_JAR_SYMBOL(start);
		return BOOT_JAR_SYMBOL(start);
	}

} // extern "C"

namespace zetes
{
	namespace feet
	{
		string extractEntryClassName() {
			size_t size = ENTRY_CLASS_NAME_SYMBOL(end) - ENTRY_CLASS_NAME_SYMBOL(start);
			char* str = new char[size];
			memcpy(str, ENTRY_CLASS_NAME_SYMBOL(start), size);
			str[size - 1] = 0;
			return string(str);
		}

		FeetStarter::FeetStarter() : maximumHeapSizeMegabytes(16000)
		{

		}

/*		void FeetStarter::setApplicationClassName(const std::string& applicationClassName)
		{
			this->applicationClassName = applicationClassName;
		}

		const std::string& FeetStarter::getApplicationClassName() const
		{
			return applicationClassName;
		}*/

		void FeetStarter::setMaximumHeapSizeMegabytes(int maximumHeapSizeMegabytes)
		{
			this->maximumHeapSizeMegabytes = maximumHeapSizeMegabytes;
		}

		int FeetStarter::getMaximumHeapSizeMegabytes() const
		{
			return maximumHeapSizeMegabytes;
		}

		void FeetStarter::setDefinition(const std::string& name, const std::string& value)
		{
			definitions.insert(std::pair<string, string>(name, value));
		}

		const std::string* FeetStarter::getDefinition(const std::string& name) const
		{
			if (definitions.find(name) != definitions.end())
			{
				return &(definitions.at(name));
			}
			else
			{
				return NULL;
			}
		}


		std::list<argstring>& FeetStarter::takeArguments()
		{
			return arguments;
		}

		int FeetStarter::run()
		{
			JavaVMInitArgs vmArgs;
			vmArgs.version = JNI_VERSION_1_2;
			vmArgs.nOptions = definitions.size() + 2;
			vmArgs.ignoreUnrecognized = JNI_TRUE;

			JavaVMOption options[vmArgs.nOptions];
			vmArgs.options = options;

			// Setting boot classpath
			options[0].optionString = const_cast<char*>("-Xbootclasspath:[bootJar]");

			// Setting maximum heap memory amount
			stringstream xmxss;
			xmxss << "-Xmx" << maximumHeapSizeMegabytes << "m";
			options[1].optionString = const_cast<char*>(xmxss.str().c_str());	// "-Xmx16000m"

//			options[2].optionString = const_cast<char*>("-XstartOnFirstThread");



			// Adding definitions
			int i = 0;
			for (map<string, string>::const_iterator iter = definitions.begin(); iter != definitions.end(); iter++)
			{
				stringstream dss;
				dss << "-D" << (*iter).first << "=" << (*iter).second;
				char* tmp = new char[255];

				options[i + 2].optionString = new char[strlen(dss.str().c_str()) + 1];
				strcpy(options[i + 2].optionString, dss.str().c_str());
				i++;
			}

			JavaVM* vm;
			void* env;
			JNI_CreateJavaVM(&vm, &env, &vmArgs);
			JNIEnv* e = static_cast<JNIEnv*>(env);

			jclass c = e->FindClass(extractEntryClassName().c_str());
			if (not e->ExceptionCheck())
			{
				jmethodID m = e->GetStaticMethodID(c, "main", "([Ljava/lang/String;)V");
				if (not e->ExceptionCheck())
				{
					jclass stringClass = e->FindClass("java/lang/String");
					if (not e->ExceptionCheck())
					{
						jobjectArray a = e->NewObjectArray((jsize)(arguments.size()), stringClass, (jobject)0);
						if (not e->ExceptionCheck())
						{
							jsize index = 0;
							for (list<argstring>::const_iterator argi = arguments.begin(); argi != arguments.end(); argi++)
							{
		#ifdef __MINGW32__
								// For Windows: Sending wide string to Java
								int arglen = (*argi).size();
								jstring arg = e->NewString((jchar*) ((*argi).c_str()), arglen);
		#else
								// For other OS: Sending UTF8-encoded string to Java
								int arglen = (*argi).size();
								jstring arg = e->NewStringUTF((char*) ((*argi).c_str()));
		#endif
								e->SetObjectArrayElement(a, index, arg);
								index++;
							}

							e->CallStaticVoidMethod(c, m, a);
						}
					}
				}
			}

			int exitCode = 0;
			if (e->ExceptionCheck())
			{
				exitCode = -1;
				e->ExceptionDescribe();
			}

			vm->DestroyJavaVM();

			return exitCode;
		}

		FeetStarter::~FeetStarter()
		{
			// TODO Auto-generated destructor stub
		}

	}
}
