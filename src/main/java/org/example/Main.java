package org.example;

import org.tasks.FirstTask;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class Main {
    private static Map<String, Float> fineMap = new HashMap<>();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        long timeStart = System.currentTimeMillis();
        FirstTask.jsonFilesToXml("src/main/java/org/jsonfilesinput", 10);
        long estimatedTime = System.currentTimeMillis() - timeStart;
        System.out.println(estimatedTime + " ms");
    }
}