package dohyun22.jst3.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.world.gen.structure.StructureMineshaftPieces.Cross;

public class ReflectionUtils {
	private ReflectionUtils() {}
	
	@Nullable
	public static Field getPublicField(Object obj, String name) {
		Field r = null;
		try {
			r = obj.getClass().getDeclaredField(name);
		} catch (Throwable t) {
		}
		return r;
	}

	@Nullable
	public static Field getField(Object obj, String name) {
		Field r = null;
		try {
			r = getClassObj(obj).getDeclaredField(name);
			r.setAccessible(true);
		} catch (Throwable t) {}
		return r;
	}

	@Nullable
	public static Field getFieldAndRemoveFinal(Object obj, String name) {
		Field r = getField(obj, name);
		removeFinal(r);
		return r;
	}

	@Nullable
	public static Method getMethod(Object obj, String method, Class<?>... param) {
		Method r = null;
		try {
			r = getClassObj(obj).getDeclaredMethod(method, param);
			r.setAccessible(true);
		} catch (Throwable t) {}
		return r;
	}

	@Nullable
	public static Object getFieldValue(Object obj, String field, Object tgt) {
		try {
			return getField(obj, field).get(tgt);
		} catch (Throwable t) {}
		return null;
	}

	public static void setFieldValue(Object obj, String field, Object tgt, Object set) {
		try {
			getField(obj, field).set(tgt, set);
		} catch (Throwable t) {}
	}

	@Nullable
	public static Object callMethod(Object obj, String method, Object... param) {
		return callMethod(obj, obj, method, param);
	}

	@Nullable
	public static Object callMethod(Object obj, Object tgt, String name, Object... param) {
		try {
			Class<?>[] pt = new Class[param.length];
			for (byte i = 0; i < param.length; i = (byte) (i + 1)) {
				if (param[i] instanceof Class) {
					pt[i] = (Class) param[i];
					param[i] = null;
				} else {
					pt[i] = param[i].getClass();
				}
				if (pt[i] == Boolean.class) {
					pt[i] = Boolean.TYPE;
				} else if (pt[i] == Byte.class) {
					pt[i] = Byte.TYPE;
				} else if (pt[i] == Short.class) {
					pt[i] = Short.TYPE;
				} else if (pt[i] == Integer.class) {
					pt[i] = Integer.TYPE;
				} else if (pt[i] == Long.class) {
					pt[i] = Long.TYPE;
				} else if (pt[i] == Float.class) {
					pt[i] = Float.TYPE;
				} else if (pt[i] == Double.class) {
					pt[i] = Double.TYPE;
				}
			}
			Method m = getClassObj(obj).getMethod(name, pt);
			m.setAccessible(true);
			return m.invoke(tgt, param);
		} catch (Throwable e) {}
		return null;
	}

	@Nullable
	public static Constructor getConstructor(Object obj, Class<?>... param) {
		Constructor ret = null;
		try {
			ret = getClassObj(obj).getDeclaredConstructor(param);
			ret.setAccessible(true);
		} catch (Throwable t) {
		}
		return ret;
	}

	@Nullable
	public static Object callConstructor(Object obj, Object... param) {
		Object ret = null;
		try {
			Class<?>[] cls = new Class<?>[param.length];
			for (int n = 0; n < cls.length; n++)
				cls[n] = param[n].getClass();
			return getConstructor(obj, cls).newInstance(param);
		} catch (Throwable t) {}
		return ret;
	}

	@Nonnull
	public static String getClassName(Object o) {
		if (o == null)
			return "null";
		return o.getClass().getName().substring(o.getClass().getName().lastIndexOf(".") + 1);
	}
	
	public static boolean checkClassExists(String str) {
		try {
			Class.forName(str);
			return true;
		} catch (Throwable t) {}
		return false;
	}
	
	public static boolean checkFieldExists(String clz, String fld) {
		try {
			Class.forName(clz).getField(fld);
			return true;
		} catch (Throwable t) {}
		return false;
	}
	
	public static boolean checkMethodExists(String clz, String fnc, Class... param) {
		try {
			Class.forName(clz).getMethod(fnc, param);
			return true;
		} catch (Throwable t) {}
		return false;
	}

	public static boolean removeFinal(Field field) {
		if (field == null) return false;
		try {
			Field md = Field.class.getDeclaredField("modifiers");
			md.setAccessible(true);
			md.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		} catch (Throwable t) {
			return false;
		}
		return true;
	}

	@Nullable
	public static Class getClassObj(Object obj) {
		try {
			return obj instanceof Class ? ((Class)obj) : obj instanceof String ? Class.forName((String) obj) : obj.getClass();
		} catch (Throwable t) {}
		return null;
	}

	@Nullable
	public static Field getFieldObf(Object c, String... names) {
		Class cl = getClassObj(c);
		if (cl != null)
			for (String n : names) {
				try {
					Field r = cl.getDeclaredField(n);
					r.setAccessible(true);
					return r;
				} catch (Throwable t) {}
			}
		return null;
	}

	@Nullable
	public static Method getMethodObf(Object c, String[] names, Class... par) {
		Class cl = getClassObj(c);
		if (cl != null)
			for (String n : names) {
				try {
					Method r = cl.getDeclaredMethod(n, par);
					r.setAccessible(true);
					return r;
				} catch (Throwable t) {}
			}
		return null;
	}
}
