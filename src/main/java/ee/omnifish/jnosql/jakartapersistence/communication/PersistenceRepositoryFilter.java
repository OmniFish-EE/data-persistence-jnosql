/*
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
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

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * A filter to validate Repository that either Eclipse JNoSQL or the Jakarta Persistence extension support. It will
 * check the first parameter on the repository, and if the entity has not had an unsupported annotation, it will return
 * false and true to supported Repository.
 */
enum PersistenceRepositoryFilter implements Predicate<Class<?>> {

    INSTANCE;

    @Override
    public boolean test(Class<?> type) {
        Optional<Class<?>> entity = getEntityClass(type);
        return entity.map(this::toSupportedAnnotation)
                .isPresent();
    }

    private Annotation toSupportedAnnotation(Class<?> c) {
        final Annotation annotation = c.getAnnotation(jakarta.persistence.Entity.class);
        return annotation != null ? annotation : c.getAnnotation(jakarta.nosql.Entity.class);
    }

    private Optional<Class<?>> getEntityClass(Class<?> repository) {
        Type[] interfaces = repository.getGenericInterfaces();
        if (interfaces.length == 0) {
            return Optional.empty();
        }
        if (interfaces[0] instanceof ParameterizedType interfaceType) {
            return Optional.ofNullable(getEntityFromInterface(interfaceType));
        } else {
            return Optional.empty();
        }
    }

    private Class<?> getEntityFromInterface(ParameterizedType param) {
        Type[] arguments = param.getActualTypeArguments();
        if (arguments.length == 0) {
            return null;
        }
        Type argument = arguments[0];
        if (argument instanceof Class<?> entity) {
            return entity;
        }
        return null;
    }

}
