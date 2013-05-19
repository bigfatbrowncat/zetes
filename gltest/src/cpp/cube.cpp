#include <stddef.h>

#include <algorithm>
#include <string>
#include <vector>

using namespace std;

#include <jni.h>

#ifdef __APPLE__
#include "gl.h"
#include "gl3.h"
#include "glu.h"
#else
#include <GL/gl.h>
#include <GL/glu.h>
#endif

static GLuint VertexArrayID;
// This will identify our vertex buffer
static GLuint vertexbuffer;

static GLuint programID;

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

GLuint LoadShaders()
{
    // Read the Vertex Shader code from the file
    std::string VertexShaderCode = string() +
    		"#version 150"                                         + "\n" +
    		"in vec3 vertexPosition_modelspace;"                        + "\n" +
    		"void main()"                                               + "\n" +
    		"{"                                                         + "\n" +
    		"    gl_Position.xyz = vertexPosition_modelspace;"          + "\n" +
    		"    gl_Position.w = 1.0;"                                  + "\n" +
            "}\n";

    // Read the Fragment Shader code from the file
    std::string FragmentShaderCode = string() +
			"#version 150"                                         + "\n" +
			"out vec3 color;"                                           + "\n" +
			"void main()"                                               + "\n" +
			"{"                                                         + "\n" +
			"    color = vec3(1,0,0);"                                  + "\n" +
			"}\n";


    // Create the shaders
    GLuint vertexShaderID = glCreateShader(GL_VERTEX_SHADER);
    GLuint fragmentShaderID = glCreateShader(GL_FRAGMENT_SHADER);

    GLint result = GL_FALSE;
    int infoLogMaxLength = 1024;
    int infoLogRealLength;

    // Compiling Vertex Shader
    printf("Compiling vertex shader\n");
    char const * VertexSourcePointer = VertexShaderCode.c_str();
    glShaderSource(vertexShaderID, 1, &VertexSourcePointer, NULL);
    glCompileShader(vertexShaderID);

    // Checking Vertex Shader
    glGetShaderiv(vertexShaderID, GL_COMPILE_STATUS, &result);
    printf("Compilation %s\n", (result == GL_TRUE) ? "succeeded" : "failed");

    // Getting the log
    glGetShaderiv(vertexShaderID, GL_INFO_LOG_LENGTH, &infoLogMaxLength);
    char VertexShaderErrorMessage[infoLogMaxLength];
    glGetShaderInfoLog(vertexShaderID, infoLogMaxLength, &infoLogRealLength, VertexShaderErrorMessage);
    fprintf(stdout, "%s\n", VertexShaderErrorMessage);


    // Compiling Fragment Shader
    printf("Compiling fragment shader\n");
    char const * FragmentSourcePointer = FragmentShaderCode.c_str();
    glShaderSource(fragmentShaderID, 1, &FragmentSourcePointer , NULL);
    glCompileShader(fragmentShaderID);

    // Checking Fragment Shader
    glGetShaderiv(fragmentShaderID, GL_COMPILE_STATUS, &result);
    printf("Compilation %s\n", (result == GL_TRUE) ? "succeeded" : "failed");

    // Getting the log
    glGetShaderiv(fragmentShaderID, GL_INFO_LOG_LENGTH, &infoLogMaxLength);
    char FragmentShaderErrorMessage[infoLogMaxLength];
    glGetShaderInfoLog(fragmentShaderID, infoLogMaxLength, &infoLogRealLength, FragmentShaderErrorMessage);
    fprintf(stdout, "%s\n", FragmentShaderErrorMessage);

    // Link the program
    fprintf(stdout, "Linking program\n");
    GLuint ProgramID = glCreateProgram();
    glAttachShader(ProgramID, vertexShaderID);
    glAttachShader(ProgramID, fragmentShaderID);
    glLinkProgram(ProgramID);

    // Check the program
    glGetProgramiv(ProgramID, GL_LINK_STATUS, &result);
    glGetProgramiv(ProgramID, GL_INFO_LOG_LENGTH, &infoLogMaxLength);
    char ProgramErrorMessage[infoLogMaxLength];
    glGetProgramInfoLog(ProgramID, infoLogMaxLength, &infoLogRealLength, ProgramErrorMessage);
    fprintf(stdout, "%s\n", &ProgramErrorMessage[0]);

    glDeleteShader(vertexShaderID);
    glDeleteShader(fragmentShaderID);

    return ProgramID;
}

void drawScene(double angle)
{
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	glMatrixMode(GL_MODELVIEW);
	glPushMatrix();
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
		glUseProgram(programID);

		// Draw the triangle !
		glDrawArrays(GL_TRIANGLES, 0, 3); // Starting from vertex 0; 3 vertices total -> 1 triangle

		glDisableVertexAttribArray(0);
	}

	glPopMatrix();
}


void init()
{
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

	programID = LoadShaders();
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
