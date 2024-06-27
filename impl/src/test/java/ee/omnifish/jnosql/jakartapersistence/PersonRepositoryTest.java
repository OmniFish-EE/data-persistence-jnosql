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

    @BeforeEach
    public void init() {
        cdiContainer = SeContainerInitializer.newInstance()
                .addBeanClasses(EntityManagerProducer.class)
                .initialize();
        getEntityManager().getTransaction().begin();
    }

    private EntityManager getEntityManager() {
        return cdiContainer.select(EntityManager.class).get();
    }

    @AfterEach
    public void cleanup() {
        getEntityManager().getTransaction().commit();
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
        personRepo.insert(new Person());
        final long count = personRepo.countAll();
        assertThat(count, greaterThan(0L));
    }
}
