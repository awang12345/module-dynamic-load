package yunnex.mdl.support;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;

import yunnex.mdl.ModuleContainer;
import yunnex.mdl.ModuleContainerListener;
import yunnex.mdl.ModuleContext;
import yunnex.mdl.ModuleScanner;
import yunnex.mdl.service.ClassPathModuleServiceInterfaceScanner;
import yunnex.mdl.service.ModuleServiceFactory;
import yunnex.mdl.utils.ModuleUtils;

/**
 * Spring技术实现模块容器
 * 
 * @author jingyiwang
 * @date 2018年3月29日
 */
public final class SpringModuleContainer
                implements
                    ModuleContainer,
                    DisposableBean,
                    ApplicationListener<ContextRefreshedEvent>,
                    ApplicationContextAware,
                    BeanDefinitionRegistryPostProcessor,
                    InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringModuleContainer.class);

    private static final int SHFIT = 1;
    private static final int NOT_START = SHFIT << 1;// 未启动
    private static final int STARTED = SHFIT << 2;// 已启动
    private static final int DESTORY = SHFIT << 3;// 已销毁
    // 容器状态
    private final AtomicInteger state = new AtomicInteger(NOT_START);
    // 模块上下文
    private ModuleContext moduleContext;
    // 模块扫描器
    private ModuleScanner moduleScanner;
    // 模块jar包所处文件夹路径
    private String moduleJarDirPath;
    // 模块业务接口包
    private String moduleServiceInterfacePackage;
    // 模块业务工厂
    private ModuleServiceFactory moduleServiceFactory;
    // 扫描特殊的加了注解的接口
    private Class<? extends Annotation> moduleServiceInterfaceAnnotationClass;
    // 容器监听器
    private List<ModuleContainerListener> moduleContainerListeners;

    public SpringModuleContainer() {
        super();
    }

    @Override
    public void start() {
        if (state.compareAndSet(NOT_START, STARTED)) {
            LOGGER.info("Module container starting.....");
            moduleScanner.start();
            moduleContext.start();
            LOGGER.info("Module container started success!");
            if (moduleContainerListeners != null) {
                for (ModuleContainerListener listener : moduleContainerListeners) {
                    listener.containerStart(this);
                }
            }
        }
    }

    @Override
    public void destory() {
        if (state.compareAndSet(STARTED, DESTORY)) {
            LOGGER.info("Start destory module container!");
            moduleScanner.shutdown();
            moduleContext.destory();
            if (moduleContainerListeners != null) {
                for (ModuleContainerListener listener : moduleContainerListeners) {
                    listener.containerDestory(this);
                }
            }
            LOGGER.info("End destory module container!");
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() == null) {
            // 判断是否为顶层容器
            start();
        }
    }

    @Override
    public void destroy() throws Exception {
        destory();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SystemConfig.setApplicationContext(applicationContext);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.moduleJarDirPath = ModuleUtils.formatFilePath(moduleJarDirPath);
        initAttrFromSpringCtxIfNull();
        Assert.hasLength(moduleJarDirPath, "moduleJarDirPath not null");
        Assert.hasLength(moduleServiceInterfacePackage, "moduleServiceInterfacePackage not null");
        Assert.notNull(moduleServiceFactory, "moduleServiceFactory not null!");
        SystemConfig.setModuleContext(moduleContext);
    }

    /**
     * 属性为空的时候,从spring的上下文中进行初始化
     *
     * @date 2018年4月3日
     * @author jiangyiwang
     */
    private void initAttrFromSpringCtxIfNull() {
        if (this.moduleServiceFactory == null) {
            this.moduleServiceFactory = SystemConfig.getApplicationContext().getBean(ModuleServiceFactory.class);
        }
        if (CollectionUtils.isEmpty(moduleContainerListeners)) {
            // 从spring上下文中查找
            Map<String, ModuleContainerListener> listenerMap = SystemConfig.getApplicationContext().getBeansOfType(ModuleContainerListener.class);
            if (MapUtils.isNotEmpty(listenerMap)) {
                moduleContainerListeners = Lists.newLinkedList();
                moduleContainerListeners.addAll(listenerMap.values());
            }
        }
        if (this.moduleContext == null) {
            this.moduleContext = SystemConfig.getApplicationContext().getBean(ModuleContext.class);
        }
        if (this.moduleScanner == null) {
            this.moduleScanner = new DefaultModuleScanner(moduleJarDirPath);
        }
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        // 扫描模块业务接口
        ClassPathModuleServiceInterfaceScanner serviceInterfaceScanner = new ClassPathModuleServiceInterfaceScanner(registry);
        serviceInterfaceScanner.setAnnotationClass(moduleServiceInterfaceAnnotationClass);
        serviceInterfaceScanner.setModuleServiceFactory(moduleServiceFactory);
        serviceInterfaceScanner.registerFilters();
        serviceInterfaceScanner.scan(org.springframework.util.StringUtils.tokenizeToStringArray(moduleServiceInterfacePackage,
                        ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
    }

    public void setModuleServiceFactory(ModuleServiceFactory moduleServiceFactory) {
        this.moduleServiceFactory = moduleServiceFactory;
    }

    public void setModuleServiceInterfaceAnnotationClass(Class<? extends Annotation> moduleServiceInterfaceAnnotationClass) {
        this.moduleServiceInterfaceAnnotationClass = moduleServiceInterfaceAnnotationClass;
    }

    public void setModuleServiceInterfacePackage(String moduleServiceInterfacePackage) {
        this.moduleServiceInterfacePackage = moduleServiceInterfacePackage;
    }

    public void setModuleContainerListeners(List<ModuleContainerListener> moduleContainerListeners) {
        this.moduleContainerListeners = moduleContainerListeners;
    }

    public void setModuleContext(ModuleContext moduleContext) {
        this.moduleContext = moduleContext;
    }

    public void setModuleScanner(ModuleScanner moduleScanner) {
        this.moduleScanner = moduleScanner;
    }

    public void setModuleJarDirPath(String moduleJarDirPath) {
        this.moduleJarDirPath = moduleJarDirPath;
    }

    @Override
    public ModuleContext getContext() {
        return this.moduleContext;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // 忽略
    }

}
