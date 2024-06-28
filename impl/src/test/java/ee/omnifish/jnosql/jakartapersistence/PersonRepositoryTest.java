/*
 * Copyright (c) 2024 OmniFish. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */
package ee.omnifish.jnosql.jakartapersistence;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import jakarta.persistence.EntityManager;
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
    private PersonRepository personRepo;

    @BeforeEach
    void init() {
        cdiContainer = SeContainerInitializer.newInstance()
                .addBeanClasses(EntityManagerProducer.class)
                .initialize();
        assertThat("repository can be resolved", cdiContainer.select(PersonRepository.class).isResolvable());
        personRepo = cdiContainer.select(PersonRepository.class).get();
        getEntityManager().getTransaction().begin();
    }

    private EntityManager getEntityManager() {
        return cdiContainer.select(EntityManager.class).get();
    }

    @AfterEach
    void cleanup() {
        getEntityManager().getTransaction().commit();
        cdiContainer.close();
    }

    @Test
    void findAll() {
        final List<Person> persons = personRepo.findAll().toList();
        assertThat("queryResult", persons, is(empty()));
        System.out.println("All persons: " + persons);
    }

    @Test
    void count() {
        personRepo.insert(new Person());
        final long count = personRepo.countAll();
        assertThat(count, greaterThan(0L));
    }

    @Test
    void countByNotNull() {
        final Person person = new Person();
        person.setName("Jakarta");
        personRepo.insert(person);
        final long count = personRepo.countByNameNotNull();
        assertThat(count, greaterThan(0L));
    }

    @Test
    void findByXAndYLessThanEqual() {
        final Person person = new Person();
        final String NAME = "Jakarta";
        person.setName(NAME);
        person.setAge(35);
        personRepo.insert(person);

        final List<Person> persons = personRepo.findByNameAndAgeLessThanEqual(NAME, 50);
        assertThat(persons, is(not(empty())));
    }

}
