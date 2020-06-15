package com.taoge.es.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author taoxuefeng
 * @date 2019/06/21
 */
public class EsTypeUtil {

    public static Map<String,String> getTypesByClass(Class clazz){
        Map<String,String> result=new HashMap<>();
        Field[] fields=clazz.getDeclaredFields();
        for(int i=0;i<fields.length;i++){
            Field field=fields[i];
            Class fieldType=field.getType();
            String fieldName=field.getName();
            result.put(fieldName,getNameByType(fieldType));
        }
        return result;
    }

    private static String getNameByType(Class clazz){
        if(Integer.class==clazz||Integer.TYPE==clazz){
            return "integer";
        }
        if(Long.class==clazz||Long.TYPE==clazz){
            return "long";
        }
        if(String.class==clazz){
            return "text";
        }
        if(Double.class==clazz||Double.TYPE==clazz){
            return "double";
        }
        if(Float.class==clazz||Float.TYPE==clazz){
            return "float";
        }
        if(Byte.class==clazz||Byte.TYPE==clazz){
            return "byte";
        }
        if(Short.class==clazz||Short.TYPE==clazz){
            return "short";
        }
        return "object";
    }
}
