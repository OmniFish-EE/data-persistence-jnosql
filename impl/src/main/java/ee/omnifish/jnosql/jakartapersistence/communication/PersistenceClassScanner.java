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

import jakarta.data.repository.DataRepository;
import java.util.Set;
import org.eclipse.jnosql.mapping.metadata.ClassScanner;

/**
 *
 * @author Ondro Mihalyi
 */
public class PersistenceClassScanner implements ClassScanner {

    @Override
    public Set<Class<?>> entities() {
        return PersistenceClassScannerSingleton.INSTANCE.entities();
    }

    @Override
    public Set<Class<?>> repositories() {
        return PersistenceClassScannerSingleton.INSTANCE.repositories();
    }

    @Override
    public Set<Class<?>> embeddables() {
        return PersistenceClassScannerSingleton.INSTANCE.embeddables();
    }

    @Override
    public <T extends DataRepository<?, ?>> Set<Class<?>> repositories(Class<T> filter) {
        return PersistenceClassScannerSingleton.INSTANCE.repositories(filter);
    }

    @Override
    public Set<Class<?>> repositoriesStandard() {
        return PersistenceClassScannerSingleton.INSTANCE.repositoriesStandard();
    }

    @Override
    public Set<Class<?>> customRepositories() {
        return PersistenceClassScannerSingleton.INSTANCE.customRepositories();
    }

}
