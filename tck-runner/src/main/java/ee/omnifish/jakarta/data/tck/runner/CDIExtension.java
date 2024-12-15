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
package ee.omnifish.jakarta.data.tck.runner;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.InjectionTarget;
import jakarta.enterprise.inject.spi.InjectionTargetFactory;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

/**
 *
 * @author Ondro Mihalyi
 */
@ApplicationScoped
public class CDIExtension implements TestInstancePostProcessor {

    private final SeContainer cdiContainer = SeContainerInitializer.newInstance().initialize();

    @Override
    public void postProcessTestInstance(Object testInstance, ExtensionContext context) throws Exception {
        injectDependencies(testInstance);
    }

    public void injectDependencies(Object instance) {
        BeanManager beanManager = cdiContainer.getBeanManager();
        AnnotatedType<Object> annotatedType = (AnnotatedType<Object>) beanManager.createAnnotatedType(instance.getClass());
        InjectionTargetFactory<Object> injectionTargetFactory = beanManager.getInjectionTargetFactory(annotatedType);
        InjectionTarget<Object> injectionTarget = injectionTargetFactory.createInjectionTarget(null);
        CreationalContext<Object> creationalContext = beanManager.createCreationalContext(null);
        injectionTarget.inject(instance, creationalContext);
        injectionTarget.postConstruct(instance);
    }

}
