#include <stddef.h>

#include <algorithm>
#include <string>
#include <vector>

using namespace std;

#include <jni.h>

#include "GL3/gl3w.h"

#include "cubex/ShaderProgram.h"

using namespace cubex;

static GLuint VertexArrayID;
// This will identify our vertex buffer
static GLuint vertexbuffer;

static ShaderProgram* program;

// An array of 3 vectors which represents 3 vertices
static const GLfloat g_vertex_buffer_data[] = {
   -1.0f, -1.0f, 0.0f,
   1.0f, -1.0f, 0.0f,
   0.0f,  1.0f, 0.0f,
};

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

void drawScene(double angle)
{
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	//glMatrixMode(GL_MODELVIEW);
	//glPushMatrix();
	{
		// 1rst attribute buffer : vertices
		glEnableVertexAttribArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, vertexbuffer);
		glVertexAttribPointer(
		   0,                  // attribute 0. No particular reason for 0, but must match the layout in the shader.
		   3,                  // size
		   GL_FLOAT,           // type
		   GL_FALSE,           // normalized?
		   0,                  // stride
		   (void*)0            // array buffer offset
		);

		// Use our shader
		program->use();

		// Draw the triangle !
		glDrawArrays(GL_TRIANGLES, 0, 3); // Starting from vertex 0; 3 vertices total -> 1 triangle

		glDisableVertexAttribArray(0);
	}

	//glPopMatrix();
}


void init()
{
	if (gl3wInit())
	{
		printf("Problem initializing OpenGL\n");
	}

	// Creating a VAO (Vertex Array Object0
	glGenVertexArrays(1, &VertexArrayID);

	// Binding the VAO
	glBindVertexArray(VertexArrayID);

	// Generate 1 buffer, put the resulting identifier in vertexbuffer
	glGenBuffers(1, &vertexbuffer);

	// The following commands will talk about our 'vertexbuffer' buffer
	glBindBuffer(GL_ARRAY_BUFFER, vertexbuffer);

	// Give our vertices to OpenGL.
	glBufferData(GL_ARRAY_BUFFER, (long)sizeof(g_vertex_buffer_data), g_vertex_buffer_data, GL_STATIC_DRAW);

	int maj, min, slmaj, slmin;
	getGlVersion(&maj, &min);
	getGlslVersion(&slmaj, &slmin);

	printf("OpenGL version: %d.%d\n", maj, min);
	printf("GLSL version: %d.%d\n", slmaj, slmin);


    // Read the Vertex Shader code from the file
    std::string VertexShaderCode = string() +
    		"#version 150"                                              + "\n" +
    		"in vec3 vertexPosition_modelspace;"                        + "\n" +
    		"void main()"                                               + "\n" +
    		"{"                                                         + "\n" +
    		"    gl_Position.xyz = vertexPosition_modelspace;"          + "\n" +
    		"    gl_Position.w = 1.0;"                                  + "\n" +
            "}\n";

    // Read the Fragment Shader code from the file
    std::string FragmentShaderCode = string() +
			"#version 150"                                              + "\n" +
			"out vec3 color;"                                           + "\n" +
			"void main()"                                               + "\n" +
			"{"                                                         + "\n" +
			"    color = vec3(1,0,0);"                                  + "\n" +
			"}\n";

    program = new ShaderProgram(VertexShaderCode, FragmentShaderCode);
}

void resize(int width, int height)
{
	glViewport(0, 0, width, height);
}


extern "C"
{
	JNIEXPORT void JNICALL Java_gltest_GLViewWindow_initScene(JNIEnv * env, jclass appClass)
	{
		init();
	}

	JNIEXPORT void JNICALL Java_gltest_GLViewWindow_resizeView(JNIEnv * env, jclass appClass, int width, int height)
	{
		resize(width, height);
	}

	JNIEXPORT void JNICALL Java_gltest_GLViewWindow_drawScene(JNIEnv * env, jclass appClass, jdouble angle)
	{
		drawScene(angle);
	}

}
