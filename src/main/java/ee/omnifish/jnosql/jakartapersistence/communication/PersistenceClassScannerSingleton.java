/*
 * Copyright (c) 2023, 2024 Contributors to the Eclipse Foundation
 * Copyright (c) 2024 OmniFish
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
package ee.omnifish.jnosql.jakartapersistence.communication;


import static java.util.Collections.unmodifiableSet;
import static java.util.stream.Collectors.toUnmodifiableSet;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import jakarta.data.repository.BasicRepository;
import jakarta.data.repository.CrudRepository;
import jakarta.data.repository.DataRepository;
import jakarta.data.repository.Repository;
import jakarta.nosql.Embeddable;
import jakarta.nosql.Entity;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import org.eclipse.jnosql.mapping.NoSQLRepository;
import org.eclipse.jnosql.mapping.metadata.ClassScanner;

/**
 * Scanner classes that will load entities with both Entity and Embeddable
 * annotations and repositories: interfaces that extend DataRepository
 * and has the Repository annotation.
 */
enum PersistenceClassScannerSingleton implements ClassScanner {

    INSTANCE;

    private final Set<Class<?>> entities;
    private final Set<Class<?>> repositories;
    private final Set<Class<?>> embeddables;
    private final Set<Class<?>> customRepositories;


    PersistenceClassScannerSingleton() {
        entities = new HashSet<>();
        embeddables = new HashSet<>();
        repositories = new HashSet<>();
        customRepositories = new HashSet<>();

        Logger logger = Logger.getLogger(PersistenceClassScannerSingleton.class.getName());
        logger.fine("Starting scan class to find entities, embeddable and repositories.");
        try (ScanResult result = new ClassGraph().enableAllInfo().scan()) {
            var notSupportedRepositories = loadNotSupportedRepositories(result);
            logger.warning(() -> "The following repositories are not supported: " + notSupportedRepositories);
            this.entities.addAll(loadEntities(result));
            this.embeddables.addAll(loadEmbeddable(result));
            this.repositories.addAll(loadRepositories(result));
            this.customRepositories.addAll(loadCustomRepositories(result));
            notSupportedRepositories.forEach(this.repositories::remove);
        }
        logger.fine(String.format("Finished the class scan with entities %d, embeddables %d and repositories: %d"
                , entities.size(), embeddables.size(), repositories.size()));

    }


    @Override
    public Set<Class<?>> entities() {
        return unmodifiableSet(entities);
    }

  @Override
    public Set<Class<?>> repositories() {
        return unmodifiableSet(repositories);
    }


   @Override
    public Set<Class<?>> embeddables() {
        return unmodifiableSet(embeddables);
    }

    @Override
    public <T extends DataRepository<?, ?>> Set<Class<?>> repositories(Class<T> filter) {
        Objects.requireNonNull(filter, "filter is required");
        return repositories.stream().filter(filter::isAssignableFrom)
                .filter(c -> Arrays.asList(c.getInterfaces()).contains(filter))
                .collect(toUnmodifiableSet());
    }


    @Override
    public Set<Class<?>> repositoriesStandard() {
        return repositories.stream()
                .filter(c -> {
                    List<Class<?>> interfaces = Arrays.asList(c.getInterfaces());
                    return interfaces.contains(CrudRepository.class)
                            || interfaces.contains(BasicRepository.class)
                            || interfaces.contains(NoSQLRepository.class)
                            || interfaces.contains(DataRepository.class);
                }).collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<Class<?>> customRepositories() {
        return customRepositories;
    }


    @SuppressWarnings("rawtypes")
    private static List<Class<DataRepository>> loadRepositories(ScanResult scan) {
        return scan.getClassesWithAnnotation(Repository.class)
                .getInterfaces()
                .filter(c -> c.implementsInterface(DataRepository.class))
                .loadClasses(DataRepository.class)
                .stream().filter(PersistenceRepositoryFilter.INSTANCE)
                .toList();
    }

    private static List<Class<?>> loadCustomRepositories(ScanResult scan) {
        return scan.getClassesWithAnnotation(Repository.class)
                .getInterfaces()
                .filter(c -> !c.implementsInterface(DataRepository.class))
                .loadClasses().stream().toList();
    }

    @SuppressWarnings("rawtypes")
    private static List<Class<DataRepository>> loadNotSupportedRepositories(ScanResult scan) {
        return scan.getClassesWithAnnotation(Repository.class)
                .getInterfaces()
                .filter(c -> c.implementsInterface(DataRepository.class))
                .loadClasses(DataRepository.class)
                .stream().filter(PersistenceRepositoryFilter.INSTANCE.negate())
                .toList();
    }

    private static List<Class<?>> loadEmbeddable(ScanResult scan) {
        return scan.getClassesWithAnnotation(Embeddable.class).loadClasses();
    }

    private static List<Class<?>> loadEntities(ScanResult scan) {
        return scan.getClassesWithAnnotation(Entity.class).loadClasses();
    }
}
