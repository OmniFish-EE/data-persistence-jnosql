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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.EntityType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.eclipse.jnosql.communication.Value;
import org.eclipse.jnosql.communication.semistructured.CommunicationEntity;
import org.eclipse.jnosql.communication.semistructured.Element;
import org.eclipse.jnosql.communication.semistructured.SelectQuery;

/**
 *
 * @author Ondro Mihalyi
 */
@ApplicationScoped
public class PersistenceDatabaseManager {

    private final EntityManager em;

    private final Map<String, EntityType<?>> entityTypesByName = new HashMap<>();

    @Inject
    public PersistenceDatabaseManager(EntityManager em) {
        this.em = em;
        cacheEntityTypes();
    }

    PersistenceDatabaseManager() {
        em = null;
    }

    public EntityManager getEntityManager() {
        return em;
    }

    public Stream<CommunicationEntity> select(SelectQuery sq) {
        final String entityName = sq.name();
        final EntityType<?> entityType = findEntityType(entityName);
        final CriteriaQuery<Object> criteriaQuery = em.getCriteriaBuilder().createQuery();
        final Root<?> from = criteriaQuery.from(entityType.getJavaType());
        criteriaQuery.select(from);

        final TypedQuery<Object> query = em.createQuery(criteriaQuery);
        return query.getResultStream()
                .map(persistenceEntity -> CommunicationEntity.of(entityName, List.of(
                        Element.of("1", Value.of(persistenceEntity))
                )));
    }

    public void close() {
    }

    public <T> EntityType<T> findEntityType(String entityName) {
        try {
            return (EntityType<T>) em.getMetamodel().entity(entityName);
        } catch (IllegalArgumentException e) {
            // EclipseLink expects full class name in MM.entity() method. We need to find out the type otherwise
            EntityType<?> entityType = entityTypesByName.get(entityName);
            if (entityType != null) {
                return (EntityType<T>)entityType;
            } else {
                final IllegalArgumentException ex = new IllegalArgumentException("Entity with name " + entityName + " not found in the list of known entities");
                ex.addSuppressed(e);
                throw ex;
            }
        }
    }

    private void cacheEntityTypes() {
        em.getMetamodel().getEntities().forEach(type -> {
            entityTypesByName.put(type.getName(), type);
        });
    }

}
