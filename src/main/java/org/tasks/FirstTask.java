package org.tasks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.filter.GenericExtFilter;
import org.models.Fine;
import org.models.Fines;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class FirstTask {
    //change map to bigDecimal for correct output
    private static Map<String, BigDecimal> fineMap = new HashMap<>();

    public static void jsonFilesToXml(String folderPath, int threadsNumber) {
        //filter for json
        GenericExtFilter filter = new GenericExtFilter("json");
        File folder = new File(folderPath);
        if (folder.listFiles() == null) {
            throw new IllegalArgumentException("Current directory contains no files!");
        } else if (!folder.isDirectory()) {
            throw new IllegalArgumentException("Directory does not exists!");
        } else if (Objects.requireNonNull(folder.list(filter)).length == 0) {
            throw new IllegalArgumentException("Provided folder contains no .json files!");
        }
        Map<String, BigDecimal> map;
        map = readJson(folderPath, threadsNumber);
        jacksonAnnotation2Xml(map);
    }

    //xmlMapper
    private static void jacksonAnnotation2Xml(Map<String, BigDecimal> map) {
        XmlMapper xmlMapper = new XmlMapper();
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            xmlMapper.writeValue(new File("./src/main/java/org/out/penalties.xml"), getFines(map));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //field for big decimal format settings
    private static final DecimalFormat df = new DecimalFormat("0.00");

    //for method folderpath and number of threads must be provided
    public static Map<String, BigDecimal> readJson(String folderPath, int threadsNumber) {
        File dir = new File(folderPath);
        //getting files in directory
        File[] listOfFiles = dir.listFiles();
        assert listOfFiles != null;
        //service for creation fixed numbers of threads
        ExecutorService executors = Executors.newFixedThreadPool(threadsNumber);
        //lock for non-threadsafe operations
        ReentrantLock lock = new ReentrantLock();
        for (File file : listOfFiles) {
            //one future to read single file from file and put into shared map
            CompletableFuture.supplyAsync(() -> file, executors)
                    .thenAccept(e -> {
                        //using scanner for careful memory processing
                        //one json object-one block
                        try (Scanner scanner = new Scanner(new FileReader(file)).useDelimiter("\\[*\\{")) {
                            ObjectMapper mapper = new ObjectMapper();
                            //take only needed properties
                            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                            while (scanner.hasNext()) {
                                String current = "{" + scanner.next();
                                //map string to Model
                                Fine fine = mapper.readValue(current, Fine.class);
                                //locking unsafe put and get
                                lock.lock();
                                if (!fineMap.containsKey(fine.getType())) {
                                    fineMap.put(fine.getType(), fine.getFineAmount());
                                } else {
                                    fineMap.put(fine.getType(), fineMap.get(fine.getType()).add(fine.getFineAmount()));
                                }
                                lock.unlock();
                            }

                        } catch (FileNotFoundException | JsonProcessingException ex) {
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
        return fineMap;
    }

    // convert map to Fines object
    private static Fines getFines(Map<String, BigDecimal> map) {
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
