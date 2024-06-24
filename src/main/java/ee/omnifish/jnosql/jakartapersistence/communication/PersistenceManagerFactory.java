/*
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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

import jakarta.persistence.EntityManagerFactory;
import org.eclipse.jnosql.communication.semistructured.DatabaseManager;
import org.eclipse.jnosql.communication.semistructured.DatabaseManagerFactory;

/**
 *
 * @author Ondro Mihalyi
 */
public class PersistenceManagerFactory implements DatabaseManagerFactory {

    private EntityManagerFactory emf;

    public PersistenceManagerFactory(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public void close() {
        emf.close();
    }

    @Override
    public DatabaseManager apply(String t) {
        return new PersistenceDatabaseManager(emf.createEntityManager(), emf.getName());
    }

}
