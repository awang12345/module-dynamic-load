package yunnex.mdl.service;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.util.StringUtils;

/**
 * 模块业务service接口扫描
 * 
 * @author jingyiwang
 * @date 2018年4月2日
 */
public class ClassPathModuleServiceInterfaceScanner extends ClassPathBeanDefinitionScanner {

    // 模块业务工厂
    private ModuleServiceFactory moduleServiceFactory;

    // 用于运行时指向模块业务工厂
    private String moduleServiceFactoryBeanName;

    // 扫描特殊的加了注解的接口
    private Class<? extends Annotation> annotationClass;

    public ClassPathModuleServiceInterfaceScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
    }

    /**
     * 注册业务接口扫描过滤器
     *
     * @date 2018年4月2日
     * @author jiangyiwang
     */
    public void registerFilters() {
        boolean acceptAllInterfaces = true;

        // if specified, use the given annotation and / or marker interface
        if (this.annotationClass != null) {
            addIncludeFilter(new AnnotationTypeFilter(this.annotationClass));
            acceptAllInterfaces = false;
        }

        if (acceptAllInterfaces) {
            // default include filter that accepts all classes
            addIncludeFilter(new TypeFilter() {
                public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
                    return true;
                }
            });
        }

        // exclude package-info.java
        addExcludeFilter(new TypeFilter() {
            public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
                String className = metadataReader.getClassMetadata().getClassName();
                return className.endsWith("package-info");
            }
        });
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
        if (beanDefinitions.isEmpty()) {
            logger.warn("No Module service was found in '" + Arrays.toString(basePackages) + "' package. Please check your configuration.");
        } else {
            for (BeanDefinitionHolder holder : beanDefinitions) {
                GenericBeanDefinition definition = (GenericBeanDefinition) holder.getBeanDefinition();
                if (logger.isDebugEnabled()) {
                    logger.debug("Creating ModuleServiceFactoryBean with name '" + holder.getBeanName() + "' and '" + definition
                                    .getBeanClassName() + "' serviceInterface");
                }
                definition.getPropertyValues().add("serviceInterface", definition.getBeanClassName());
                definition.setBeanClass(ModuleServiceFactoryBean.class);
                boolean explicitFactoryUsed = false;
                if (StringUtils.hasText(this.moduleServiceFactoryBeanName)) {
                    definition.getPropertyValues().add("moduleServiceFactory", new RuntimeBeanReference(this.moduleServiceFactoryBeanName));
                    explicitFactoryUsed = true;
                } else if (this.moduleServiceFactory != null) {
                    definition.getPropertyValues().add("moduleServiceFactory", this.moduleServiceFactory);
                    explicitFactoryUsed = true;
                }
                if (!explicitFactoryUsed) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Enabling autowire by type for ModuleServiceFactoryBean with name '" + holder.getBeanName() + "'.");
                    }
                    definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
                }
            }
        }
        return beanDefinitions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return (beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) throws IllegalStateException {
        if (super.checkCandidate(beanName, beanDefinition)) {
            return true;
        } else {
            logger.warn("Skipping ModuleServiceFactoryBean with name '" + beanName + "' and '" + beanDefinition
                            .getBeanClassName() + "' serviceInterface" + ". Bean already defined with the same name!");
            return false;
        }
    }

    public void setModuleServiceFactory(ModuleServiceFactory moduleServiceFactory) {
        this.moduleServiceFactory = moduleServiceFactory;
    }

    public Class<? extends Annotation> getAnnotationClass() {
        return annotationClass;
    }

    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
        this.annotationClass = annotationClass;
    }

    public ModuleServiceFactory getModuleServiceFactory() {
        return moduleServiceFactory;
    }

    public String getModuleServiceFactoryBeanName() {
        return moduleServiceFactoryBeanName;
    }

    public void setModuleServiceFactoryBeanName(String moduleServiceFactoryBeanName) {
        this.moduleServiceFactoryBeanName = moduleServiceFactoryBeanName;
    }

}
