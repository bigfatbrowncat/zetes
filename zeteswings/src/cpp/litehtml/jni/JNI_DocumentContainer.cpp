#include <stddef.h>
#include <stdio.h>
#include <jni.h>

#define LITEHTML_UTF8

#include <litehtml.h>

#include "JNI_LiteHTML.h"
#include "JNI_Position.h"
#include "JNI_FontMetrics.h"
#include "JNI_WebColor.h"
#include "JNI_BackgroundPaint.h"

using namespace litehtml;

static jmethodID textWidthMethod = NULL;
static jmethodID drawTextMethod = NULL;
static jmethodID createFont = NULL;
static jmethodID getFontMetrics = NULL;
static jmethodID deleteFont = NULL;
static jmethodID getClientRect = NULL;
static jmethodID ptToPx = NULL;
static jmethodID getDefaultFontSize = NULL;
static jmethodID getDefaultFontName = NULL;
static jmethodID getDrawBackground = NULL;

jclass getLiteHTMLDocumentContainerClass(JNIEnv* env) {
	return env->FindClass(LITEHTML_PACKAGE "/DocumentContainer");
}

jmethodID getTextWidthMethod(JNIEnv* env) {
	jclass lhdcc = getLiteHTMLDocumentContainerClass(env);
	if (textWidthMethod == NULL) {
		textWidthMethod = env->GetMethodID(lhdcc, "textWidth", "(Ljava/lang/String;J)I");
	}
	return textWidthMethod;
}

jmethodID getDrawTextMethod(JNIEnv* env) {
	jclass lhdcc = getLiteHTMLDocumentContainerClass(env);
	if (drawTextMethod == NULL) {
		drawTextMethod = env->GetMethodID(lhdcc, "drawText", "(JLjava/lang/String;JL" LITEHTML_PACKAGE "/WebColor;L" LITEHTML_PACKAGE "/Position;)V");
	}
	return drawTextMethod;
}

jmethodID getCreateFontMethod(JNIEnv* env) {
	jclass lhdcc = getLiteHTMLDocumentContainerClass(env);
	if (createFont == NULL) {
		createFont = env->GetMethodID(lhdcc, "createFont", "(Ljava/lang/String;IIZ)J");
	}
	return createFont;
}

jmethodID getGetFontMetricsMethod(JNIEnv* env) {
	jclass lhdcc = getLiteHTMLDocumentContainerClass(env);
	if (getFontMetrics == NULL) {
		getFontMetrics = env->GetMethodID(lhdcc, "getFontMetrics", "(J)L" LITEHTML_PACKAGE "/FontMetrics;");
	}
	return getFontMetrics;
}

jmethodID getDeleteFontMethod(JNIEnv* env) {
	jclass lhdcc = getLiteHTMLDocumentContainerClass(env);
	if (deleteFont == NULL) {
		deleteFont = env->GetMethodID(lhdcc, "deleteFont", "(L)V");
	}
	return deleteFont;
}

jmethodID getGetClientRectMethod(JNIEnv* env) {
	jclass lhdcc = getLiteHTMLDocumentContainerClass(env);
	if (getClientRect == NULL) {
		getClientRect = env->GetMethodID(lhdcc, "getClientRect", "()L" LITEHTML_PACKAGE "/Position;");
	}
	return getClientRect;
}

jmethodID getPtToPxMethod(JNIEnv* env) {
	jclass lhdcc = getLiteHTMLDocumentContainerClass(env);
	if (ptToPx == NULL) {
		ptToPx = env->GetMethodID(lhdcc, "ptToPx", "()I");
	}
	return ptToPx;
}

jmethodID getGetDefaultFontSizeMethod(JNIEnv* env) {
	jclass lhdcc = getLiteHTMLDocumentContainerClass(env);
	if (getDefaultFontSize == NULL) {
		getDefaultFontSize = env->GetMethodID(lhdcc, "getDefaultFontSize", "()I");
	}
	return getDefaultFontSize;
}

jmethodID getGetDefaultFontNameMethod(JNIEnv* env) {
	jclass lhdcc = getLiteHTMLDocumentContainerClass(env);
	if (getDefaultFontName == NULL) {
		getDefaultFontName = env->GetMethodID(lhdcc, "getDefaultFontName", "()Ljava/lang/String;");
	}
	return getDefaultFontName;
}

jmethodID getDrawBackgroundMethod(JNIEnv* env) {
	jclass lhdcc = getLiteHTMLDocumentContainerClass(env);
	if (getDrawBackground == NULL) {
		getDrawBackground = env->GetMethodID(lhdcc, "drawBackground", "(JL" LITEHTML_PACKAGE "/BackgroundPaint;)V");
	}
	return getDrawBackground;
}

class JNI_LiteHTMLDocumentContainer : public litehtml::document_container {
private:
	JNIEnv* env;
	jobject javaLiteHTMLDocumentContainer_weak;

	char* buffer = NULL;
public:
	JNI_LiteHTMLDocumentContainer(JNIEnv* env, jobject javaContainer) {
		this->env = env;
		this->javaLiteHTMLDocumentContainer_weak = env->NewWeakGlobalRef(javaContainer);
	}

	virtual ~JNI_LiteHTMLDocumentContainer() {
		if (buffer != NULL) {
			free(buffer);
		}
		env->DeleteWeakGlobalRef(javaLiteHTMLDocumentContainer_weak);
	}

	virtual uint_ptr create_font(const tchar_t* faceName, int size, int weight, font_style italic, unsigned int decoration, litehtml::font_metrics* fm) {
		jobject javaLiteHTMLDocumentContainer = env->NewLocalRef(javaLiteHTMLDocumentContainer_weak);

		jstring faceNameString = env->NewStringUTF(faceName);
		jboolean isItalic = (italic == fontStyleItalic);

		jlong hFont = env->CallLongMethod(javaLiteHTMLDocumentContainer, getCreateFontMethod(env), faceNameString, size, weight, isItalic);
		jobject jfm = env->CallObjectMethod(javaLiteHTMLDocumentContainer, getGetFontMetricsMethod(env), hFont);
		*fm = fontMetricsToNative(env, jfm);
		return (uint_ptr)hFont;

	}

	virtual void delete_font(uint_ptr hFont) {
		jobject javaLiteHTMLDocumentContainer = env->NewLocalRef(javaLiteHTMLDocumentContainer_weak);

		return env->CallVoidMethod(javaLiteHTMLDocumentContainer, getDeleteFontMethod(env), (jlong)hFont);
	}

	virtual int text_width(const tchar_t* text, uint_ptr hFont) {
		jobject javaLiteHTMLDocumentContainer = env->NewLocalRef(javaLiteHTMLDocumentContainer_weak);

		jstring textString = env->NewStringUTF(text);
		jlong fontPtr = (jlong)hFont;

		return env->CallIntMethod(javaLiteHTMLDocumentContainer, getTextWidthMethod(env), textString, fontPtr);
	}

	virtual void draw_text(uint_ptr hdc, const tchar_t* text, uint_ptr hFont, litehtml::web_color color, const litehtml::position& pos) {
		jobject javaLiteHTMLDocumentContainer = env->NewLocalRef(javaLiteHTMLDocumentContainer_weak);

		jlong hdcPtr = (jlong)hdc;
		jstring textString = env->NewStringUTF(text);
		jlong fontPtr = (jlong)hFont;
		jobject jwclr = webColorFromNative(env, color);
		jobject positionObj = positionFromNative(env, pos);

		return env->CallVoidMethod(javaLiteHTMLDocumentContainer, getDrawTextMethod(env), hdcPtr, textString, fontPtr, jwclr, positionObj);
	}

	virtual int	pt_to_px(int pt) {
		jobject javaLiteHTMLDocumentContainer = env->NewLocalRef(javaLiteHTMLDocumentContainer_weak);

		return env->CallIntMethod(javaLiteHTMLDocumentContainer, getPtToPxMethod(env));
	}

	virtual int get_default_font_size() {
		jobject javaLiteHTMLDocumentContainer = env->NewLocalRef(javaLiteHTMLDocumentContainer_weak);

		return env->CallIntMethod(javaLiteHTMLDocumentContainer, getGetDefaultFontSizeMethod(env));
	}
	virtual const tchar_t* get_default_font_name() {
		jobject javaLiteHTMLDocumentContainer = env->NewLocalRef(javaLiteHTMLDocumentContainer_weak);
		if (buffer != NULL) {
			delete [] buffer;
		}
		jstring jfns = (jstring)env->CallObjectMethod(javaLiteHTMLDocumentContainer, getGetDefaultFontNameMethod(env));
		const char* tmp = env->GetStringUTFChars(jfns, NULL);
		buffer = strdup(tmp);
		env->ReleaseStringUTFChars(jfns, tmp);
		return buffer;
	}
	virtual void				draw_list_marker(uint_ptr hdc, const litehtml::list_marker& marker) {
		// TODO Implement
	}
	virtual void				load_image(const tchar_t* src, const tchar_t* baseurl, bool redraw_on_ready) {
		// TODO Implement
	}
	virtual void				get_image_size(const tchar_t* src, const tchar_t* baseurl, litehtml::size& sz) {
		// TODO Implement
	}
	virtual void draw_background(uint_ptr hdc, const litehtml::background_paint& bg) {
		jobject javaLiteHTMLDocumentContainer = env->NewLocalRef(javaLiteHTMLDocumentContainer_weak);

		jlong hdcPtr = (jlong)hdc;
		jobject backgroundObj = backgroundPaintFromNative(env, bg);

		return env->CallVoidMethod(javaLiteHTMLDocumentContainer, getDrawBackgroundMethod(env), hdcPtr, backgroundObj);
	}
	virtual void				draw_borders(uint_ptr hdc, const css_borders& borders, const litehtml::position& draw_pos, bool root) {
		// TODO Implement
	}
	virtual	void set_caption(const tchar_t* caption) {
		// TODO Implement
	}
	virtual	void				set_base_url(const tchar_t* base_url) {
		// TODO Implement
	}
	virtual void				link(litehtml::document* doc, litehtml::element::ptr el) {
		// TODO Implement
	}
	virtual void				on_anchor_click(const tchar_t* url, litehtml::element::ptr el) {
		// TODO Implement
	}
	virtual	void				set_cursor(const tchar_t* cursor) {
		// TODO Implement
	}
	virtual	void				transform_text(litehtml::tstring& text, litehtml::text_transform tt) {
		// TODO Implement
	}
	virtual void				import_css(tstring& text, const tstring& url, tstring& baseurl) {
		// TODO Implement
	}
	virtual void				set_clip(const litehtml::position& pos, bool valid_x, bool valid_y) {
		// TODO Implement
	}
	virtual void				del_clip() {
		// TODO Implement
	}
	virtual void				get_client_rect(litehtml::position& client) {
		jobject javaLiteHTMLDocumentContainer = env->NewLocalRef(javaLiteHTMLDocumentContainer_weak);

		jobject jpos = env->CallObjectMethod(javaLiteHTMLDocumentContainer, getGetClientRectMethod(env));
		client = positionToNative(env, jpos);
	}
	virtual litehtml::element*	create_element(const tchar_t* tag_name, const string_map& attributes) {
		return NULL;
	}
	virtual void				get_media_features(litehtml::media_features& media) {
		// TODO Implement
	}

};

extern "C" {
	JNIEXPORT jlong JNICALL Java_zetes_wings_litehtml_jni_DocumentContainer_createNativeObject(JNIEnv* env, jobject obj) {
		jlong res = (jlong)(new JNI_LiteHTMLDocumentContainer(env, obj));
		return res;
	}

	JNIEXPORT void JNICALL Java_zetes_wings_litehtml_jni_DocumentContainer_destroyNativeObject(JNIEnv* env, jobject obj, jlong ptr) {
		delete (JNI_LiteHTMLDocumentContainer*)ptr;
	}
}
