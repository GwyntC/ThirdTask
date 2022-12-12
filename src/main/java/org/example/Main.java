package org.example;

import org.tasks.FirstTask;
import org.tasks.SecondTask;
import org.testclasses.TestClass;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class Main {
    private static Map<String, Float> fineMap = new HashMap<>();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        long timeStart = System.currentTimeMillis();
       FirstTask.jsonFilesToXml("src/main/java/org/jsonfilesinput", 8);
       long estimatedTime = System.currentTimeMillis() - timeStart;
       System.out.println(estimatedTime + " ms");
       // Path path= Paths.get("src/main/java/org/propertiesinput/propertyFirst.property");
      //TestClass obj= SecondTask.loadFromProperties(TestClass.class,path);
    }
}