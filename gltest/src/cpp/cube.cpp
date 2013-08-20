#include <stddef.h>

#include <algorithm>
#include <string>
#include <vector>

using namespace std;

#include <jni.h>

#include <GL3/gl3w.h>
#include "WinLinMacApi.h"

#include "cubex/Scene.h"

using namespace cubex;

static Scene* scene;

// Model constants
static int MODEL_CUBE = 0;
static int MODEL_MONKEY_SIMPLE = 1;
static int MODEL_MONKEY_SUBDIVIDED = 2;

void getGlVersion(int *major, int *minor)
{
    const char *verstr = (const char *) glGetString(GL_VERSION);
    if ((verstr == NULL) || (sscanf(verstr,"%d.%d", major, minor) != 2))
    {
        *major = *minor = 0;
        fprintf(stderr, "Invalid GL_VERSION format!!!\n");
    }
}

void getGlslVersion(int *major, int *minor)
{
    int gl_major, gl_minor;
    getGlVersion(&gl_major, &gl_minor);

    *major = *minor = 0;
    if(gl_major == 1)
    {
        /* GL v1.x can only provide GLSL v1.00 as an extension */
        const char *extstr = (const char *) glGetString(GL_EXTENSIONS);
        if ((extstr != NULL) &&
            (strstr(extstr, "GL_ARB_shading_language_100") != NULL))
        {
            *major = 1;
            *minor = 0;
        }
    }
    else if (gl_major >= 2)
    {
        /* GL v2.0 and greater must parse the version string */
        const char *verstr =
            (const char *) glGetString(GL_SHADING_LANGUAGE_VERSION);

        if((verstr == NULL) ||
            (sscanf(verstr, "%d.%d", major, minor) != 2))
        {
            *major = *minor = 0;
            fprintf(stderr,
                "Invalid GL_SHADING_LANGUAGE_VERSION format!!!\n");
        }
    }
}


bool globalInit()
{
	if (gl3wInit())
	{
		printf("Problem initializing OpenGL\n");
	}

	int maj, min, slmaj, slmin;
	getGlVersion(&maj, &min);
	getGlslVersion(&slmaj, &slmin);

	printf("OpenGL version: %d.%d\n", maj, min);
	printf("GLSL version: %d.%d\n", slmaj, slmin);
}

extern "C"
{
	JNIEXPORT jboolean JNICALL Java_gltest_GLViewWindow_globalInit(JNIEnv * env, jclass appClass)
	{
		return globalInit();
	}

	JNIEXPORT jboolean JNICALL Java_gltest_GLViewWindow_createScene(JNIEnv * env, jclass appClass, jint model, jint width, jint height)
	{
		string objFileLocation;
		string textureFileLocation;
		string vertFileLocation = WinLinMacApi::locateResource("data", "default.vert");
		string fragFileLocation = WinLinMacApi::locateResource("data", "default.frag");
		string screenVertFileLocation = WinLinMacApi::locateResource("data", "screen.vert");
		string screenFragFileLocation = WinLinMacApi::locateResource("data", "screen.frag");

		if (model == MODEL_CUBE)
		{
			objFileLocation = WinLinMacApi::locateResource("data", "cube-uv.obj");
			textureFileLocation = WinLinMacApi::locateResource("data", "cube-tex.png");
		}
		else if (model == MODEL_MONKEY_SIMPLE)
		{
			objFileLocation = WinLinMacApi::locateResource("data", "monkeysimple-tri-normal.obj");
			textureFileLocation = WinLinMacApi::locateResource("data", "monkey-tex.png");
		}
		else if (model == MODEL_MONKEY_SUBDIVIDED)
		{
			objFileLocation = WinLinMacApi::locateResource("data", "monkeysubdiv-tri-normal.obj");
			textureFileLocation = WinLinMacApi::locateResource("data", "monkey-tex.png");
		}
		else
		{
			return false;
		}

		try
		{
			scene = new Scene(objFileLocation, vertFileLocation, fragFileLocation, screenVertFileLocation, screenFragFileLocation, textureFileLocation, width, height);
			return true;
		}
		catch (CubexException& ex)
		{
			printf("[ERROR] %s\n", ex.getReport().c_str());
			fflush(stdout);
			return false;
		}

	}

	JNIEXPORT jboolean JNICALL Java_gltest_GLViewWindow_destroyScene(JNIEnv * env, jclass appClass)
	{
		if (scene != NULL)
		{
			delete scene;
			scene = NULL;
			return true;
		}
		else
		{
			return false;
		}
	}

	JNIEXPORT jboolean JNICALL Java_gltest_GLViewWindow_resizeView(JNIEnv * env, jclass appClass, int width, int height)
	{
		if (scene != NULL)
		{
			scene->resizeViewport(width, height);
			return true;
		}
		else
		{
			return false;
		}
	}

	JNIEXPORT jboolean JNICALL Java_gltest_GLViewWindow_drawScene(JNIEnv * env, jclass appClass, jdouble angle)
	{
		try
		{
			if (scene != NULL)
			{
				scene->draw(angle);
				return true;
			}
			else
			{
				return false;
			}
		}
		catch (CubexException& ex)
		{
			printf("[ERROR] %s\n", ex.getReport().c_str());
			fflush(stdout);
			return false;
		}
	}

}
