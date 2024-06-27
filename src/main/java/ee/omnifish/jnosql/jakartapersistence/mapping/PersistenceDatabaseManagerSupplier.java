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
package ee.omnifish.jnosql.jakartapersistence.mapping;


import static ee.omnifish.jnosql.jakartapersistence.communication.PersistenceDbConfiguration.CONFIG_PERSISTENCE_UNIT;

import ee.omnifish.jnosql.jakartapersistence.communication.PersistenceDatabaseManager;
import ee.omnifish.jnosql.jakartapersistence.communication.PersistenceDbConfiguration;
import ee.omnifish.jnosql.jakartapersistence.communication.PersistenceManagerFactory;
import jakarta.data.exceptions.MappingException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.mapping.core.config.MicroProfileSettings;

/**
 *
 * @author Ondro Mihalyi
 */
@ApplicationScoped
class PersistenceDatabaseManagerSupplier implements Supplier<PersistenceDatabaseManager> {

    private static final Logger LOGGER = Logger.getLogger(PersistenceDatabaseManagerSupplier.class.getName());

    @Override
    @Produces
    @ApplicationScoped
    public PersistenceDatabaseManager get() {
        Settings settings = MicroProfileSettings.INSTANCE;
        PersistenceDbConfiguration configuration = new PersistenceDbConfiguration();
        final PersistenceManagerFactory factory = configuration.apply(settings);
        Optional<String> persistenceUnitMaybe = settings.get(CONFIG_PERSISTENCE_UNIT, String.class);
        String persistenceUnit = persistenceUnitMaybe.orElseThrow(() -> new MappingException("Please, select the persistence unit with the config property "
                + CONFIG_PERSISTENCE_UNIT));
        final PersistenceDatabaseManager manager = factory.apply(persistenceUnit);
        LOGGER.log(Level.FINEST, "Starting  a PersistenceDatabaseManager instance using Eclipse MicroProfile Config," +
                " persistence unit name: " + persistenceUnit);
        return manager;
    }
    public void close(@Disposes PersistenceDatabaseManager manager) {
        LOGGER.log(Level.FINEST, () -> "Closing PersistenceDatabaseManager resource, persistence unit name: " + manager.persistenceUnitName());
        manager.close();
    }

}

