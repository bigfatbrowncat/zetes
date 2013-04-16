/*******************************************************************************
 * Copyright (c) 2010 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package crossbase.ui.extensions.cocoa;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.eclipse.swt.internal.C;
import org.eclipse.swt.internal.cocoa.NSControl;
import org.eclipse.swt.internal.cocoa.NSMenu;
import org.eclipse.swt.internal.cocoa.NSMenuItem;
import org.eclipse.swt.internal.cocoa.OS;

/**
 * @since 3.7
 * 
 */

public class CocoaUtil {

	static Class PTR_CLASS = C.PTR_SIZEOF == 8 ? long.class : int.class;

	public static long convertToLong(Object object) {
		if (object instanceof Integer) {
			Integer i = (Integer) object;
			return i.longValue();
		}
		if (object instanceof Long) {
			Long l = (Long) object;
			return l.longValue();
		}
		return 0;
	}

	public static NSMenuItem getItemAtIndex(NSMenu menu, int index)
			throws IllegalArgumentException, SecurityException, IllegalAccessException,
			InvocationTargetException, NoSuchMethodException {
		return (NSMenuItem) invokeMethod(NSMenu.class, menu, "itemAtIndex", makeArgs(index));
	}

	public static Object[] makeArgs(long pointer) {
		return new Object[] { CocoaUtil.wrapPointer(pointer) };
	}

	public static Object[] makeArgs(Object obj) {
		return new Object[] { obj };
	}

	public static Object[] makeArgs(Object obj1, Object obj2) {
		return new Object[] { obj1, obj2 };
	}

	public static Object[] makeArgs(Object obj1, Object obj2, Object obj3) {
		return new Object[] { obj1, obj2, obj3 };
	}

	public static Object[] makeArgs(Object obj1, Object obj2, Object obj3, Object obj4) {
		return new Object[] { obj1, obj2, obj3, obj4 };
	}

	public static Object[] makeArgs(Object obj1, Object obj2, Object obj3, Object obj4, Object obj5) {
		return new Object[] { obj1, obj2, obj3, obj4, obj5 };
	}

	public static Object invokeMethod(Class clazz, String methodName, Object[] args)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException,
			SecurityException, NoSuchMethodException {
		return invokeMethod(clazz, null, methodName, args);
	}

	public static Object invokeMethod(Class clazz, Object target, String methodName, Object[] args)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException,
			SecurityException, NoSuchMethodException {
		Class[] signature = new Class[args.length];
		for (int i = 0; i < args.length; i++) {
			Class thisClass = args[i].getClass();
			if (thisClass == Integer.class)
				signature[i] = int.class;
			else if (thisClass == Long.class)
				signature[i] = long.class;
			else if (thisClass == Byte.class)
				signature[i] = byte.class;
			else
				signature[i] = thisClass;
		}
		Method method = clazz.getMethod(methodName, signature);
		return method.invoke(target, args);
	}

	public static Object wrapPointer(long value) {
		if (PTR_CLASS == long.class)
			return new Long(value);
		else
			return new Integer((int) value);
	}

	// The following methods reflectively call corresponding methods in the OS
	// class, using ints or longs as required based on platform.

	public static NSControl new_NSControl(long arg0) throws NoSuchMethodException,
			InstantiationException, IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		Class clazz = NSControl.class;
		Class PTR_CLASS = C.PTR_SIZEOF == 8 ? long.class : int.class;
		Constructor constructor = clazz.getConstructor(new Class[] { PTR_CLASS });
		return (NSControl) constructor.newInstance(new Object[] { wrapPointer(arg0) });
	}

	/**
	 * Specialized method. It's behavior is isolated and different enough from
	 * the usual invocation that custom code is warranted.
	 */
	public static long[] OS_object_getInstanceVariable(long delegateId, byte[] name)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException,
			SecurityException, NoSuchMethodException {
		Class clazz = OS.class;
		Method method = null;
		Class PTR_CLASS = C.PTR_SIZEOF == 8 ? long.class : int.class;
		if (PTR_CLASS == long.class) {
			method = clazz.getMethod("object_getInstanceVariable", new Class[] { long.class,
					byte[].class, long[].class });
			long[] resultPtr = new long[1];
			method.invoke(null, new Object[] { new Long(delegateId), name, resultPtr });
			return resultPtr;
		} else {
			method = clazz.getMethod("object_getInstanceVariable", new Class[] { int.class,
					byte[].class, int[].class });
			int[] resultPtr = new int[1];
			method.invoke(null, new Object[] { new Integer((int) delegateId), name, resultPtr });
			return new long[] { resultPtr[0] };
		}
	}

}
