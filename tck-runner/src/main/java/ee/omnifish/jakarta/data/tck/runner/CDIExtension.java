/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
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
