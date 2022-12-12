package org.firsttest;

import org.junit.jupiter.api.Test;
import org.tasks.SecondTask;
import org.testclasses.TestClass;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SecondTaskTest {

    @Test
    void loadFromPropertiesPositiveWithAnnotationDeclare() {
        Path path= Paths.get("src/main/java/org/propertiesinput/propertyFirst.property");
        TestClass testClass=SecondTask.loadFromProperties(TestClass.class,path);
        assertEquals(10,testClass.getNumber());
        assertEquals("value1",testClass.getStringProperty());
        assertNotEquals(null,testClass.getTimeProperty());
    }
    @Test
    void loadPropertiesWithNullOrEmptyPath(){
        Path path=Paths.get("");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
                    //when
                    TestClass testClass=SecondTask.loadFromProperties(TestClass.class,path);
                },
                ("Allowed null or empty path"));
        //then
        assertEquals("Input path must not to be null or empty", ex.getMessage());
    }
    @Test
    void loadPropertiesWithMissedValues(){
        Path path= Paths.get("src/main/java/org/propertiesinput/propertyFirstCorrupted.property");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
                    //when
                    TestClass testClass=SecondTask.loadFromProperties(TestClass.class,path);
                },
                ("Allowed corrupted properties!"));
        //then
        assertEquals("File must contain all properties!", ex.getMessage());
    }
    @Test
    void loadFieldsWithPrefix(){
        Path path= Paths.get("src/main/java/org/propertiesinput/propertyFirstWithDots.property");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
                    //when
                    TestClass testClass=SecondTask.loadFromProperties(TestClass.class,path);
                },
                ("Allowed corrupted properties!"));
        //then
        assertEquals("No dots in properties without annotation support!", ex.getMessage());
    }
}