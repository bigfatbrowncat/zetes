#include <stddef.h>

#include <algorithm>
#include <string>
#include <vector>

using namespace std;

#include <jni.h>

#define PNG_SETJMP_SUPPORTED
#define PNG_STDIO_SUPPORTED
#define PNG_SEQUENTIAL_READ_SUPPORTED
#include <png/png.h>

#include <GL3/gl3w.h>

#include "cubex/ShaderProgram.h"

using namespace cubex;

static GLuint VertexArrayID;
// This will identify our vertex buffer
static GLuint vertexBufferObject;

static ShaderProgram* program;

static int vertexCoordinatesAttrib;
static int textureCoordinatesAttrib;
static int diffuseColorAttrib;
static int textureID;

// An array of 3 vectors which represents 3 vertices
static const GLfloat vertices[] =
{
	// vertex: (X Y Z), diffuse color: (R G B), tex. coords: (U V)
  -1.0f, -1.0f, 0.0f,		1.0f, 0.0f, 0.0f, 			0.0f, 0.0f,
   1.0f, -1.0f, 0.0f,		0.0f, 1.0f, 0.0f, 			1.0f, 1.0f,
   0.0f,  1.0f, 0.0f,		0.0f, 0.0f, 1.0f, 			0.0f, 1.0f,
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

GLuint png_texture_load(const char * file_name, int * width, int * height)
{
    png_byte header[8];

    FILE *fp = fopen(file_name, "rb");
    if (fp == 0)
    {
        perror(file_name);
        return 0;
    }

    // read the header
    fread(header, 1, 8, fp);

    if (png_sig_cmp(header, 0, 8))
    {
        fprintf(stderr, "error: %s is not a PNG.\n", file_name);
        fclose(fp);
        return 0;
    }

    png_structp png_ptr = png_create_read_struct(PNG_LIBPNG_VER_STRING, NULL, NULL, NULL);
    if (!png_ptr)
    {
        fprintf(stderr, "error: png_create_read_struct returned 0.\n");
        fclose(fp);
        return 0;
    }

    // create png info struct
    png_infop info_ptr = png_create_info_struct(png_ptr);
    if (!info_ptr)
    {
        fprintf(stderr, "error: png_create_info_struct returned 0.\n");
        png_destroy_read_struct(&png_ptr, (png_infopp)NULL, (png_infopp)NULL);
        fclose(fp);
        return 0;
    }

    // create png info struct
    png_infop end_info = png_create_info_struct(png_ptr);
    if (!end_info)
    {
        fprintf(stderr, "error: png_create_info_struct returned 0.\n");
        png_destroy_read_struct(&png_ptr, &info_ptr, (png_infopp) NULL);
        fclose(fp);
        return 0;
    }

    // the code in this if statement gets called if libpng encounters an error
    if (setjmp(png_jmpbuf(png_ptr))) {
        fprintf(stderr, "error from libpng\n");
        png_destroy_read_struct(&png_ptr, &info_ptr, &end_info);
        fclose(fp);
        return 0;
    }

    // init png reading
    png_init_io(png_ptr, fp);

    // let libpng know you already read the first 8 bytes
    png_set_sig_bytes(png_ptr, 8);

    // read all the info up to the image data
    png_read_info(png_ptr, info_ptr);

    // variables to pass to get info
    int bit_depth, color_type;
    png_uint_32 temp_width, temp_height;

    // get info about png
    png_get_IHDR(png_ptr, info_ptr, &temp_width, &temp_height, &bit_depth, &color_type,
        NULL, NULL, NULL);

    if (width){ *width = temp_width; }
    if (height){ *height = temp_height; }

    // Update the png info struct.
    png_read_update_info(png_ptr, info_ptr);

    // Row size in bytes.
    int rowbytes = png_get_rowbytes(png_ptr, info_ptr);

    // glTexImage2d requires rows to be 4-byte aligned
    rowbytes += 3 - ((rowbytes-1) % 4);

    // Allocate the image_data as a big block, to be given to opengl
    png_byte * image_data;
    image_data = (png_byte *)malloc(rowbytes * temp_height * sizeof(png_byte)+15);
    if (image_data == NULL)
    {
        fprintf(stderr, "error: could not allocate memory for PNG image data\n");
        png_destroy_read_struct(&png_ptr, &info_ptr, &end_info);
        fclose(fp);
        return 0;
    }

    // row_pointers is for pointing to image_data for reading the png with libpng
    png_bytep * row_pointers = (png_bytep *)malloc(temp_height * sizeof(png_bytep));
    if (row_pointers == NULL)
    {
        fprintf(stderr, "error: could not allocate memory for PNG row pointers\n");
        png_destroy_read_struct(&png_ptr, &info_ptr, &end_info);
        free(image_data);
        fclose(fp);
        return 0;
    }

    // set the individual row_pointers to point at the correct offsets of image_data
    int i;
    for (i = 0; i < temp_height; i++)
    {
        row_pointers[temp_height - 1 - i] = image_data + i * rowbytes;
    }

    // read the png into image_data through row_pointers
    png_read_image(png_ptr, row_pointers);

    // Generate the OpenGL texture object
    GLuint texture;
    glGenTextures(1, &texture);
    glBindTexture(GL_TEXTURE_2D, texture);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, temp_width, temp_height, 0, GL_RGB, GL_UNSIGNED_BYTE, image_data);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

    // clean up
    png_destroy_read_struct(&png_ptr, &info_ptr, &end_info);
    free(image_data);
    free(row_pointers);
    fclose(fp);
    return texture;
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
	glGenBuffers(1, &vertexBufferObject);

	// The following commands will talk about our 'vertexbuffer' buffer
	glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject);

	// Give our vertices to OpenGL.
	glBufferData(GL_ARRAY_BUFFER, (long)sizeof(GLfloat) * 8 * 3, vertices, GL_STATIC_DRAW);

	// Give our texture coords to OpenGL
	//glBufferData(GL_ARRAY_BUFFER, (long)sizeof(GLfloat) * 2 * 3, textureCoords, GL_STATIC_DRAW);

	int maj, min, slmaj, slmin;
	getGlVersion(&maj, &min);
	getGlslVersion(&slmaj, &slmin);

	printf("OpenGL version: %d.%d\n", maj, min);
	printf("GLSL version: %d.%d\n", slmaj, slmin);


    // Read the Vertex Shader code from the file
    std::string VertexShaderCode = string() +
    		"#version 150"                                              + "\n" +
    		"in vec3 in_vertexPosition;"                                + "\n" +
    		"in vec2 in_textureCoords;"                                 + "\n" +
    		"in vec3 in_diffuseColor;"                                  + "\n" +

    		"out vec3 diffuseColor;"                                   + "\n" +

    		"void main()"                                               + "\n" +
    		"{"                                                         + "\n" +
    		"    gl_Position.xyz = in_vertexPosition;"                  + "\n" +
    		"    gl_Position.w = 1.0;"                                  + "\n" +
    		"    diffuseColor = in_diffuseColor;"                                  + "\n" +
            "}\n";

    // Read the Fragment Shader code from the file
    std::string FragmentShaderCode = string() +
			"#version 150"                                              + "\n" +
    		"in vec3 diffuseColor;"                                  + "\n" +

    		"out vec3 color;"                                           + "\n" +

    		"void main()"                                               + "\n" +
			"{"                                                         + "\n" +
			"    color = diffuseColor;"                              + "\n" +
			"}\n";

    program = new ShaderProgram(VertexShaderCode, FragmentShaderCode);

    vertexCoordinatesAttrib = program->getAttribLocation("in_vertexPosition");
    textureCoordinatesAttrib = program->getAttribLocation("in_textureCoords");
    diffuseColorAttrib = program->getAttribLocation("in_diffuseColor");

    int tw;
    int th;
    textureID = png_texture_load("PNGTex.png", &tw, &th);
}

void drawScene(double angle)
{
	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

	// Binding the VBO
	glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject);

	// 1st attribute buffer : vertices
	glEnableVertexAttribArray(vertexCoordinatesAttrib);
	// 2nd attribute buffer : texture coords
	glEnableVertexAttribArray(textureCoordinatesAttrib);
	// 2nd attribute buffer : texture coords
	glEnableVertexAttribArray(diffuseColorAttrib);

	// Setting vertex data
	glVertexAttribPointer(
	   vertexCoordinatesAttrib,                  // first "in" in shader
	   3,                  	// size
	   GL_FLOAT,           // type
	   GL_FALSE,           // normalized?
	   sizeof(GLfloat) * 8,                    // stride
	   (void*)0                                    // array buffer offset
	);
	// Setting diffuse color data
	glVertexAttribPointer(
	   diffuseColorAttrib,                  // second "in" in shader
	   3,                  // size
	   GL_FLOAT,           // type
	   GL_FALSE,           // normalized?
	   sizeof(GLfloat) * 8,                    // stride
	   (void*)(sizeof(GLfloat) * 3)            // array buffer offset
	);
	// Setting texture coordinates data
	glVertexAttribPointer(
	   textureCoordinatesAttrib,                  // second "in" in shader
	   2,                  // size
	   GL_FLOAT,           // type
	   GL_FALSE,           // normalized?
	   sizeof(GLfloat) * 8,                    // stride
	   (void*)(sizeof(GLfloat) * 6)            // array buffer offset
	);


	// Use our shader
	program->use();

	// Draw the triangle !
	glDrawArrays(GL_TRIANGLES, 0, 3); // Starting from vertex 0; 3 vertices total -> 1 triangle

	glDisableVertexAttribArray(vertexCoordinatesAttrib);
	glDisableVertexAttribArray(textureCoordinatesAttrib);

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
