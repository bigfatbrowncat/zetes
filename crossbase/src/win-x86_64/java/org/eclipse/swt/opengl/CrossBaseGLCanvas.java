package org.eclipse.swt.opengl;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.internal.win32.*;
import org.eclipse.swt.internal.opengl.win32.*;

/**
 * CrossBaseGLCanvas is a widget capable of displaying OpenGL content.
 * This is the Windows x86_64 version of the widget
 */

public class CrossBaseGLCanvas extends Canvas {
	long context;
	int pixelFormat;
	static final String USE_OWNDC_KEY = "org.eclipse.swt.internal.win32.useOwnDC"; //$NON-NLS-1$
/**
 * Create a GLCanvas widget using the attributes described in the GLData
 * object provided.
 *
 * @param parent a composite widget
 * @param style the bitwise OR'ing of widget styles
 * @param data the requested attributes of the GLCanvas
 *
 * @exception IllegalArgumentException
 * <ul><li>ERROR_NULL_ARGUMENT when the data is null
 *     <li>ERROR_UNSUPPORTED_DEPTH when the requested attributes cannot be provided</ul> 
 * </ul>
 */
public CrossBaseGLCanvas (Composite parent, int style, GLData data) {
	super (parent, checkStyle (parent, style));
	parent.getDisplay ().setData (USE_OWNDC_KEY, new Boolean (false));
	if (data == null) SWT.error (SWT.ERROR_NULL_ARGUMENT);
	PIXELFORMATDESCRIPTOR pfd = new PIXELFORMATDESCRIPTOR ();
	pfd.nSize = (short) PIXELFORMATDESCRIPTOR.sizeof;
	pfd.nVersion = 1;
	pfd.dwFlags = WGL.PFD_DRAW_TO_WINDOW | WGL.PFD_SUPPORT_OPENGL;
	pfd.dwLayerMask = WGL.PFD_MAIN_PLANE;
	pfd.iPixelType = (byte) WGL.PFD_TYPE_RGBA;
	if (data.doubleBuffer) pfd.dwFlags |= WGL.PFD_DOUBLEBUFFER;
	if (data.stereo) pfd.dwFlags |= WGL.PFD_STEREO;
	pfd.cRedBits = (byte) data.redSize;
	pfd.cGreenBits = (byte) data.greenSize;
	pfd.cBlueBits = (byte) data.blueSize;
	pfd.cAlphaBits = (byte) data.alphaSize;
	pfd.cDepthBits = (byte) data.depthSize;
	pfd.cStencilBits = (byte) data.stencilSize;
	pfd.cAccumRedBits = (byte) data.accumRedSize;
	pfd.cAccumGreenBits = (byte) data.accumGreenSize;
	pfd.cAccumBlueBits = (byte) data.accumBlueSize;
	pfd.cAccumAlphaBits = (byte) data.accumAlphaSize;
	pfd.cAccumBits = (byte) (pfd.cAccumRedBits + pfd.cAccumGreenBits + pfd.cAccumBlueBits + pfd.cAccumAlphaBits);
	
	//FIXME - use wglChoosePixelFormatARB
//	if (data.sampleBuffers > 0) {
//		wglAttrib [pos++] = WGL.WGL_SAMPLE_BUFFERS_ARB;
//		wglAttrib [pos++] = data.sampleBuffers;
//	}
//	if (data.samples > 0) {
//		wglAttrib [pos++] = WGL.WGL_SAMPLES_ARB;
//		wglAttrib [pos++] = data.samples;
//	}

	long hDC = OS.GetDC (handle);
	pixelFormat = WGL.ChoosePixelFormat (hDC, pfd);
	if (pixelFormat == 0 || !WGL.SetPixelFormat (hDC, pixelFormat, pfd)) {
		OS.ReleaseDC (handle, hDC);
		dispose ();
		SWT.error (SWT.ERROR_UNSUPPORTED_DEPTH);
	}
	context = WGL.wglCreateContext (hDC);
	if (context == 0) {
		OS.ReleaseDC (handle, hDC);
		SWT.error (SWT.ERROR_NO_HANDLES);
	}
	OS.ReleaseDC (handle, hDC);
	if (data.shareContext != null) {
		WGL.wglShareLists (data.shareContext.context, context);
	}
	
	Listener listener = new Listener () {
		public void handleEvent (Event event) {
			switch (event.type) {
				case SWT.Dispose:
					WGL.wglDeleteContext (context);
					break;
			}
		}
	};
	addListener (SWT.Dispose, listener);
}

static int checkStyle(Composite parent, int style) {
	if (parent != null) {
		if (!OS.IsWinCE && OS.WIN32_VERSION >= OS.VERSION (6, 0)) {
			parent.getDisplay ().setData (USE_OWNDC_KEY, new Boolean (true));
		}
	}
	return style;
}

/**
 * Returns a GLData object describing the created context.
 *  
 * @return GLData description of the OpenGL context attributes
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public GLData getGLData () {
	checkWidget ();
	GLData data = new GLData ();
	PIXELFORMATDESCRIPTOR pfd = new PIXELFORMATDESCRIPTOR ();
	pfd.nSize = (short) PIXELFORMATDESCRIPTOR.sizeof;
	long hDC = OS.GetDC (handle);
	WGL.DescribePixelFormat (hDC, pixelFormat, PIXELFORMATDESCRIPTOR.sizeof, pfd);
	OS.ReleaseDC (handle, hDC);
	data.doubleBuffer = (pfd.dwFlags & WGL.PFD_DOUBLEBUFFER) != 0;
	data.stereo = (pfd.dwFlags & WGL.PFD_STEREO) != 0;
	data.redSize = pfd.cRedBits;
	data.greenSize = pfd.cGreenBits;
	data.blueSize = pfd.cBlueBits;
	data.alphaSize = pfd.cAlphaBits;
	data.depthSize = pfd.cDepthBits;
	data.stencilSize = pfd.cStencilBits;
	data.accumRedSize = pfd.cAccumRedBits;
	data.accumGreenSize = pfd.cAccumGreenBits;
	data.accumBlueSize = pfd.cAccumBlueBits;
	data.accumAlphaSize = pfd.cAccumAlphaBits;
	return data;
}

/**
 * Returns a boolean indicating whether the receiver's OpenGL context
 * is the current context.
 *  
 * @return true if the receiver holds the current OpenGL context,
 * false otherwise
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public boolean isCurrent () {
	checkWidget ();
	return WGL.wglGetCurrentContext () == context;
}

/**
 * Sets the OpenGL context associated with this GLCanvas to be the
 * current GL context.
 * 
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void setCurrent () {
	checkWidget ();
	if (WGL.wglGetCurrentContext () == context) return;
	long hDC = OS.GetDC (handle);
	WGL.wglMakeCurrent (hDC, context);
	OS.ReleaseDC (handle, hDC);
}

/**
 * Swaps the front and back color buffers.
 * 
 * @exception SWTException <ul>
 *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
 *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
 * </ul>
 */
public void swapBuffers () {
	checkWidget ();
	long hDC = OS.GetDC (handle);
	WGL.SwapBuffers (hDC);
	OS.ReleaseDC (handle, hDC);
}
}
