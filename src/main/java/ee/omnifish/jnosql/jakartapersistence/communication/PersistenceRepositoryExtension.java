/*
 *  Copyright (c) 2022 Contributors to the Eclipse Foundation
 *   All rights reserved. This program and the accompanying materials
 *   are made available under the terms of the Eclipse Public License v1.0
 *   and Apache License v2.0 which accompanies this distribution.
 *   The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 *   and the Apache License v2.0 is available at http://www.opensource.org/licenses/apache2.0.php.
 *
 *   You may elect to redistribute this code under either of these licenses.
 *
 *   Contributors:
 *
 *   Otavio Santana
 */
package ee.omnifish.jnosql.jakartapersistence.communication;

import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.AfterBeanDiscovery;
import jakarta.enterprise.inject.spi.Extension;
import jakarta.enterprise.inject.spi.ProcessProducer;
import org.eclipse.jnosql.communication.semistructured.DatabaseManager;
import org.eclipse.jnosql.mapping.DatabaseMetadata;
import org.eclipse.jnosql.mapping.Databases;
import org.eclipse.jnosql.mapping.column.query.RepositoryColumnBean;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static org.eclipse.jnosql.mapping.DatabaseType.COLUMN;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import jakarta.data.repository.DataRepository;
import jakarta.data.repository.Repository;
import java.util.List;

/**
 * Extension to start up the Jakarta Persistence Repository.
 */
public class PersistenceRepositoryExtension implements Extension {

    private static final Logger LOGGER = Logger.getLogger(PersistenceRepositoryExtension.class.getName());

    private final Set<DatabaseMetadata> databases = new HashSet<>();

    <T, X extends DatabaseManager> void observes(@Observes final ProcessProducer<T, X> pp) {
        Databases.addDatabase(pp, COLUMN, databases);
    }

    void onAfterBeanDiscovery(@Observes final AfterBeanDiscovery afterBeanDiscovery) {

        Set<Class<?>> repositories = findRepositories();

        LOGGER.info(() -> "Processing Persistence Repository extension: " + databases.size()
                + " databases repositories " + repositories.size() + " found");
        LOGGER.info(() -> "Processing Jakarta Persistence repositories " + repositories);

        repositories.forEach(type -> {
            if (!databases.contains(DatabaseMetadata.DEFAULT_COLUMN)) {
                afterBeanDiscovery.addBean(new RepositoryColumnBean<>(type, ""));
            }
            databases.forEach(database -> afterBeanDiscovery
                    .addBean(new RepositoryColumnBean<>(type, database.getProvider())));
        });

    }

    private Set<Class<?>> findRepositories() {
        LOGGER.fine("Starting scan class to find entities, embeddable and repositories.");
        Set<Class<?>> repositories = new HashSet<>();
        try (ScanResult result = new ClassGraph().enableAllInfo().scan()) {
            repositories.addAll(loadRepositories(result));
        }
        LOGGER.fine(() -> "Finished the class scan with repositories: " + repositories.size());
        return repositories;
    }

    @SuppressWarnings("rawtypes")
    private static List<Class<DataRepository>> loadRepositories(ScanResult scan) {
        return scan.getClassesWithAnnotation(Repository.class)
                .getInterfaces()
                .filter(c -> c.implementsInterface(DataRepository.class))
                .loadClasses(DataRepository.class)
                .stream()
                .toList();
    }

}
