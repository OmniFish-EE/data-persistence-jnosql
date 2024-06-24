/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package ee.omnifish.jnosql.jakartapersistence;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Ondro Mihalyi
 */
public class PersonRepositoryTest {

    private SeContainer cdiContainer;

    @BeforeEach
    public void init() {
        cdiContainer = SeContainerInitializer.newInstance().initialize();
    }

    @AfterEach
    public void cleanup() {
        cdiContainer.close();
    }

    @Test
    public void findAll() {
        assertThat("repository can be resolved", cdiContainer.select(PersonRepository.class).isResolvable());
        final PersonRepository personRepo = cdiContainer.select(PersonRepository.class).get();
        final List<Person> persons = personRepo.findAll().toList();
        assertThat("queryResult", persons, is(empty()));
        System.out.println("All persons: " + persons);
    }

    @Test
    public void count() {
        final PersonRepository personRepo = cdiContainer.select(PersonRepository.class).get();
        final long count = personRepo.countAll();
        System.out.println("Number of all persons: " + count);
    }
}
