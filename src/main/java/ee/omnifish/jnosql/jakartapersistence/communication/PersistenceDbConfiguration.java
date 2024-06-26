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
package ee.omnifish.jnosql.jakartapersistence.communication;


import jakarta.persistence.Persistence;
import org.eclipse.jnosql.communication.Settings;
import org.eclipse.jnosql.communication.semistructured.DatabaseConfiguration;
import org.eclipse.jnosql.communication.semistructured.DatabaseManagerFactory;
import org.eclipse.microprofile.config.ConfigProvider;

/**
 *
 * @author Ondro Mihalyi
 */
public class PersistenceDbConfiguration implements DatabaseConfiguration {

    @Override
    public DatabaseManagerFactory apply(Settings t) {
        final String persistenceUnitName = ConfigProvider.getConfig().getValue("jnosql.column.database", String.class);
        return new PersistenceManagerFactory(Persistence.createEntityManagerFactory(persistenceUnitName));
    }

}
