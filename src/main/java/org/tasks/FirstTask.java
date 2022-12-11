package org.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.filter.GenericExtFilter;
import org.models.Fine;
import org.models.Fines;

import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class FirstTask {//maybe try big decimal
    private static Map<String, Float> fineMap = new HashMap<>();

    public static void jsonFilesToXml(String folderPath, int threadsNumber) {
        GenericExtFilter filter = new GenericExtFilter("json");
        File folder = new File(folderPath);
        if (folder.listFiles() == null) {
            throw new IllegalArgumentException("Current directory contains no files!");
        } else if (folderPath == null) {
            throw new IllegalArgumentException("Invalid path,input path was null!");
        } else if (!folder.isDirectory()) {
            throw new IllegalArgumentException("Directory does not exists!");
        } else if (Objects.requireNonNull(folder.list(filter)).length == 0) {
            throw new IllegalArgumentException("Provided folder contains no .json files!");
        }

        Map<String, Float> map = null;
        map = readJson(folderPath, threadsNumber);
        jacksonAnnotation2Xml(map);
        System.out.println("It is succeeded!");
    }

    private static void jacksonAnnotation2Xml(Map<String, Float> map) {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            xmlMapper.writeValue(new File("./src/main/java/org/out/penalties.xml"), getFines(map));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Map<String, Float> finalMap;

    public static Map<String, Float> readJson(String folderPath, int threadsNumber) {
//
        File dir = new File(folderPath);
        //getting files in directory
        File[] listOfFiles = dir.listFiles();
        assert listOfFiles != null;
        ExecutorService executors = Executors.newFixedThreadPool(threadsNumber);
        ReentrantLock lock = new ReentrantLock();
        //long timeStart = System.currentTimeMillis();
        for (File file : listOfFiles) {
            CompletableFuture.supplyAsync(() -> file, executors)
                    .thenAccept(e -> {
                        try (Scanner scanner = new Scanner(new FileReader(file)).useDelimiter("\\[*\\{")) {
                            ObjectMapper mapper = new ObjectMapper();
                            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                            while (scanner.hasNext()) {
                                String current = "{" + scanner.next();
                                // JsonNode node = mapper.readTree(current);
                                Fine fine = mapper.readValue(current, Fine.class);
                                lock.lock();

                                if (!fineMap.containsKey(fine.getType())) {
                                    fineMap.put(fine.getType(), fine.getFineAmount());
                                } else {
                                    fineMap.put(fine.getType(), fineMap.get(fine.getType()) + fine.getFineAmount());
                                }
                                lock.unlock();
                            }

                        } catch (FileNotFoundException ex) {
                            throw new RuntimeException(ex);
                        } catch (JsonMappingException ex) {
                            throw new RuntimeException(ex);
                        } catch (JsonProcessingException ex) {
                            throw new RuntimeException(ex);
                        }
                    });

        }
        executors.shutdown();
        try {
            executors.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        for (var entry : fineMap.entrySet()) {
            System.out.println(entry.getKey() + " " + new BigDecimal(entry.getValue()).toPlainString());
        }
        //long estimatedTime = System.currentTimeMillis() - timeStart;
        //sorting our map reversed order(biggest on top)
        //System.out.println(estimatedTime + " ms");
        return fineMap;
    }

    private static Fines getFines(Map<String, Float> map) {
        Fines fines = new Fines();
        Fine fine;
        for (var entry : map.entrySet()) {
            fine = new Fine();
            fine.setFineAmount(entry.getValue());
            fine.setType(entry.getKey());
            fines.getFines().add(fine);
        }
        return fines;
    }
}
