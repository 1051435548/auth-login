package com.sendroids.auth.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BeanConversionMapUtil {
    public static Map<String, String> targetObj(Object o) throws Exception {
        Map<String, String> map = new HashMap<>();
        Field[] fields;
        fields = o.getClass().getDeclaredFields();
        for (Field field : fields) {
            // Allows access to private properties
            field.setAccessible(true);
            String key = field.getName();
            String value = (String) field.get(o);
            if (value != null) {
                map.put(key, value);
            }
        }
        return map;
    }
}
