package cn.zhuhongqing.utils;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import cn.zhuhongqing.exception.UtilsException;

public class FieldUtil {

	public static void mapToBean(Map<String, Object> beanMap, Object target) {
		Iterator<Entry<String, Object>> originItr = beanMap.entrySet().iterator();
		while (originItr.hasNext()) {
			Entry<String, Object> entry = originItr.next();
			set(target, entry.getKey(), entry.getValue());
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T copy(T bean) {
		T returnT = (T) ReflectUtil.newInstanceWithoutArgs(bean.getClass());
		copy(bean, returnT);
		return returnT;
	}

	@SuppressWarnings("unchecked")
	public static void copy(Object origin, Object target) {
		if (ClassUtil.isMap(origin.getClass())) {
			mapToBean((Map<String, Object>) origin, target);
			return;
		}
		Field[] fields = ReflectUtil.getAccessibleFields(origin.getClass());
		for (Field of : fields) {
			Field tf = find(target.getClass(), of.getName());
			if (!ReflectUtil.isWriteable(tf))
				continue;
			Object v = get(of, origin);
			set(tf, target, v);
		}
	}

	public static void copy(Object origin, Object target, String name) {
		Object value = get(origin, name);
		set(target, name, value);
	}

	public static void set(Object target, String name, Object value) {
		set(find(target.getClass(), name), target, value);
	}

	public static Object get(Object target, String name) {
		return get(find(target.getClass(), name), target);
	}

	public static boolean set(Field field, Object obj, Object value) {
		if (!ReflectUtil.makeAccessible(field)) {
			return false;
		}
		try {
			field.set(obj, value);
			return true;
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new UtilsException(e);
		} finally {
			ReflectUtil.closeAccessible(field);
		}
	}

	public static Object get(Field field, Object obj) {
		if (!ReflectUtil.makeAccessible(field)) {
			return false;
		}
		try {
			return field.get(obj);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new UtilsException(e);
		} finally {
			ReflectUtil.closeAccessible(field);
		}
	}

	static Field find(Class<?> clazz, String name) {
		return ReflectUtil.getSupportedField(clazz, name);
	}

}
