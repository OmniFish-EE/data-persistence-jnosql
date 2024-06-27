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

import ee.omnifish.jnosql.jakartapersistence.communication.PersistenceDatabaseManager;
import jakarta.annotation.Priority;
import jakarta.data.page.CursoredPage;
import jakarta.data.page.PageRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptor;
import jakarta.nosql.QueryMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.metamodel.EntityType;
import java.time.Duration;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import org.eclipse.jnosql.communication.semistructured.DeleteQuery;
import org.eclipse.jnosql.communication.semistructured.SelectQuery;
import org.eclipse.jnosql.mapping.Database;
import org.eclipse.jnosql.mapping.DatabaseType;
import org.eclipse.jnosql.mapping.PreparedStatement;
import org.eclipse.jnosql.mapping.document.DocumentTemplate;

/**
 *
 * @author Ondro Mihalyi
 */
@Alternative
@Priority(Interceptor.Priority.APPLICATION)
@Default
@ApplicationScoped
@Database(DatabaseType.DOCUMENT)
public class PersistenceDocumentTemplate implements DocumentTemplate {

    private final PersistenceDatabaseManager manager;

    @Inject
    PersistenceDocumentTemplate(PersistenceDatabaseManager manager) {
        this.manager = manager;
    }

    PersistenceDocumentTemplate() {
        this(null);
    }

    @Override
    public long count(String entity) {
        final EntityType<?> entityType = manager.findEntityType(entity);
        return count(entityType.getJavaType());
    }

    @Override
    public <T> long count(Class<T> type) {
        TypedQuery<Long> query = buildQuery(type, Long.class, ctx -> ctx.query.select(ctx.builder.count(ctx.root)));
        return query.getSingleResult();
    }

    @Override
    public <T> Stream<T> findAll(Class<T> type) {
        TypedQuery<T> query = buildQuery(type, type, ctx -> ctx.query.select((Root<T>)ctx.root));
        return query.getResultStream();
    }

    record QuaryContext<FROM, RESULT>(CriteriaQuery<RESULT> query, Root<FROM> root, CriteriaBuilder builder) {}

    private <FROM, RESULT> TypedQuery<RESULT> buildQuery(Class<FROM> fromType, Class<RESULT> resultType,
            Function<QuaryContext<FROM, RESULT>, CriteriaQuery<RESULT>> queryModifier) {
        final EntityManager em = entityManager();
        final CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<RESULT> criteriaQuery = criteriaBuilder.createQuery(resultType);
        final Root<FROM> from = criteriaQuery.from(fromType);
        criteriaQuery = queryModifier.apply(new QuaryContext(criteriaQuery, from, criteriaBuilder));
        return em.createQuery(criteriaQuery);
    }

    @Override
    public <T> Stream<T> query(String query) {
        final EntityManager em = entityManager();
        return em.createQuery(query).getResultStream();
    }

    @Override
    public <T> Stream<T> query(String query, String entity) {
        return query(query);
    }

    @Override
    public <T> Optional<T> singleResult(String query) {
        final EntityManager em = entityManager();
        return Optional.ofNullable((T) em.createQuery(query).getSingleResultOrNull());
    }

    private EntityManager entityManager() {
        return manager.getEntityManager();
    }

    @Override
    public <T> Optional<T> singleResult(String query, String entity) {
        return singleResult(query);
    }

    @Override
    public <T, K> Optional<T> find(Class<T> type, K k) {
        return Optional.ofNullable(entityManager().find(type, k));
    }

    @Override
    public <T> T insert(T t) {
        entityManager().persist(t);
        return t;
    }

    @Override
    public <T> T update(T t) {
        return entityManager().merge(t);
    }

    @Override
    public PreparedStatement prepare(String query) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public PreparedStatement prepare(String query, String entity) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void delete(DeleteQuery query) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public <T> Stream<T> select(SelectQuery query) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public long count(SelectQuery query) {
        if (query.condition().isEmpty()) {
            return count(query.name());
        } else {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }

    @Override
    public boolean exists(SelectQuery query) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public <T> Optional<T> singleResult(SelectQuery query) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public <T> void deleteAll(Class<T> type) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public <T> CursoredPage<T> selectCursor(SelectQuery query, PageRequest pageRequest) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public <T> T insert(T t, Duration drtn) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public <T> Iterable<T> insert(Iterable<T> itrbl) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public <T> Iterable<T> insert(Iterable<T> itrbl, Duration drtn) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public <T> Iterable<T> update(Iterable<T> itrbl) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public <T, K> void delete(Class<T> type, K k) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public <T> QueryMapper.MapperFrom select(Class<T> type) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public <T> QueryMapper.MapperDeleteFrom delete(Class<T> type) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
