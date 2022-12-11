package org.tasks;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Path;

public class SecondTask {
    public static <T> T loadFromProperties(Class<T> cls, Path propertiesPath){
     //   <T>T cls=new <T>();
        Method methods[]=cls.getMethods();
        for(Method method:methods){
            if(isSetter(method)){
             // Parameter[] parameters= method.getParameterTypes()[0];
            }
        }
        return null;
    }
    private static boolean isSetter(Method method){
        if(!method.getName().startsWith("set")) return false;
        if(method.getParameterTypes().length!=1) return false;
        return true;
    }
}
