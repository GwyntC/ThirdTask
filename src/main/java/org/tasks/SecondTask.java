package org.tasks;

import org.testclasses.Property;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

public class SecondTask {

    public static <T> T loadFromProperties(Class<T> cls, Path propertiesPath) {
        if (propertiesPath == null || propertiesPath.toString().equals("")) {
            throw new IllegalArgumentException("Input path must not to be null or empty");
        }
        final T obj;
        Method [] methods = cls.getDeclaredMethods();
        Field[] fieldNames = cls.getDeclaredFields();
        Class[] parameterTypes;
        Properties prop = new Properties();
        try (FileReader reader = new FileReader(propertiesPath.toString())) {
            prop.load(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //checking numbers of properties
        if (prop.size() != 3) {
            throw new IllegalArgumentException("File must contain all properties!");
        }
        //Object creation
        try {
            obj = cls.getConstructor().newInstance();
        } catch (NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        String params = null;
        //Boolean to check if we have annotations with dots
        AtomicReference<Boolean> flag = new AtomicReference<>(false);
        for (Field fieldTest : fieldNames) {
            Property annotatedType
                    = fieldTest.getAnnotation(Property.class);
            //check for dots in properties without annotations
            if (annotatedType == null) {
                prop.forEach((key, value) -> {
                    if (key.toString().contains(".")) {
                        flag.set(true);
                    }
                });
            } else {
                prop.forEach((key, value) -> {
                    if (key.toString().contains(".") && key.toString().equals(annotatedType.name())) {
                        flag.set(false);
                    }
                });
            }
            if (flag.get()) {
                throw new IllegalArgumentException("No dots in properties without annotation support!");
            }
        }
        for (Field field : fieldNames) {
            String fieldType = field.getType().getTypeName();
            Property annotatedType
                    = field.getAnnotation(Property.class);
            //check for dots in properties without annotations
            //get properties params if field or annotation equals it
            if (prop.containsKey(field.getName())) {
                params = prop.getProperty(field.getName());
            } else if (prop.containsKey(annotatedType.name())) {
                params = prop.getProperty(annotatedType.name());
            }
            //looping methods
            for (Method method : methods) {
                //check if setter
                if (isSetter(method)) {
                    Class[] parameters = method.getParameterTypes();
                    String name = method.getName();
                    for (Class parameter : parameters) {
                        //getting return type
                        String methodReturnType = parameter.getTypeName();
                        //checking equality field and return type
                        if (fieldType.contains("int") && methodReturnType.contains("int") &&
                                //checking that is correct setter
                                (("set" + field.getName().toLowerCase()).equals(name.toLowerCase()))) {
                            try {
                                method.invoke(obj, Integer.parseInt(params));
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            } catch (InvocationTargetException e) {
                                throw new RuntimeException(e);
                            } catch (Exception ex) {
                                throw new IllegalArgumentException("Not provided correct Integer!");
                            }
                        } else if (fieldType.toLowerCase().contains("string") && methodReturnType.toLowerCase().contains("string")) {
                            try {
                                method.invoke(obj, params);
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            } catch (InvocationTargetException e) {
                                throw new RuntimeException(e);
                            }
                        } else if (fieldType.toLowerCase().contains("instant") && methodReturnType.toLowerCase().contains("instant")) {
                            try {
                                String dataFormat = null;
                                //if annotation contains new format
                                if (annotatedType.format() != null) {
                                    dataFormat = annotatedType.format();
                                } else {
                                    dataFormat = "dd.MM.yyyy HH:mm";
                                }
                                //providing dateformat for default patten
                                DateTimeFormatter f = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
                                //format if provided with reflections
                                DateTimeFormatter newFormat = DateTimeFormatter.ofPattern(dataFormat);
                                //LocalDateTime with default pattern
                                LocalDateTime ldt = LocalDateTime.parse(params, f);
                                //converting to String representing new format
                                String format = ldt.format(newFormat);
                                //creating LocalDateTime with new format
                                ldt = LocalDateTime.parse(format, newFormat);

                                method.invoke(obj, ldt.atZone(ZoneId.systemDefault()).toInstant());
                            } catch (IllegalAccessException e) {
                                throw new RuntimeException(e);
                            } catch (InvocationTargetException e) {
                                throw new RuntimeException(e);
                            } catch (Exception ex) {
                                throw new IllegalArgumentException("Properties' dataTime is not correct!");
                            }
                        }
                    }
                }
            }

        }
        return obj;
    }

    private static boolean isSetter(Method method) {
        if (!method.getName().startsWith("set"))
            return false;
        if (method.getParameterTypes().length != 1)
            return false;
        return true;
    }

}
