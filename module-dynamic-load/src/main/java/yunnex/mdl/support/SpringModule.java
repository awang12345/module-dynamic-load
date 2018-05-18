package yunnex.mdl.support;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

import yunnex.mdl.Module;
import yunnex.mdl.ModuleConfig;
import yunnex.mdl.ModuleMetrics;
import yunnex.mdl.annotations.ModuleService;
import yunnex.mdl.constans.ModuleState;
import yunnex.mdl.exeception.ModuleUnuseableException;

/**
 * 集成Spring上下文的模块,从Spring上下中找Service
 * 
 * @author jingyiwang
 * @date 2018年3月28日
 */
public class SpringModule implements Module {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringModule.class);

    /** 模块的配置信息 */
    private final ModuleConfig moduleConfig;

    /** 模块启动的时间 */
    private final Date creation;

    /** 模块状态 */
    private volatile ModuleState moduleState;

    /** 服务缓存 */
    private final Map<Class<?>, Object> serviceCache;

    private final ConfigurableApplicationContext applicationContext;

    public SpringModule(ModuleConfig moduleConfig, ConfigurableApplicationContext applicationContext) {
        this.moduleConfig = moduleConfig;
        this.applicationContext = applicationContext;
        this.creation = new Date();
        this.serviceCache = scanServices(applicationContext, ModuleService.class);
        this.moduleState = ModuleState.USEABLE;
    }

    /**
     * 扫描模块里的Service
     *
     * @param applicationContext
     * @param type
     * @param keyFunction
     * @param <T>
     * @return
     */
    private Map<Class<?>, Object> scanServices(ApplicationContext applicationContext, Class<? extends Annotation> type) {
        Map<Class<?>, Object> serviceCache = new HashMap<>();
        for (Object service : applicationContext.getBeansWithAnnotation(type).values()) {
            Class<?>[] serviceInterfaces = service.getClass().getInterfaces();
            if (serviceInterfaces == null || serviceInterfaces.length == 0) {
                LOGGER.info("{} not implements any interface!", service.getClass());
                continue;
            }
            for (Class<?> serviceClass : serviceInterfaces) {
                checkState(!serviceCache.containsKey(serviceClass), "Duplicated sercice %s", serviceClass.getSimpleName());
                if (LOGGER.isInfoEnabled()) {
                    LOGGER.info("Scan service: {}: bean: {}", serviceClass, service);
                }
                serviceCache.put(serviceClass, service);
            }
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Scan actions finish: {}", ToStringBuilder.reflectionToString(serviceCache));
        }
        return serviceCache;
    }

    @Override
    public String getName() {
        return moduleConfig.getName();
    }

    @Override
    public String getVersion() {
        return moduleConfig.getVersion();
    }

    @Override
    public Date getCreation() {
        return creation;
    }

    @Override
    public void destroy() {
        if (this.moduleState != ModuleState.DESTORY) {
            LOGGER.info("Close application context: {}", applicationContext);
            // close spring context
            closeQuietly(applicationContext);
            // clean classloader
            clear(applicationContext.getClassLoader());
            // clean service cache
            serviceCache.clear();
            // set state to destory
            this.moduleState = ModuleState.DESTORY;
            LOGGER.info("Module haven destory!");
        }
    }

    /**
     * 清除类加载器
     *
     * @param classLoader
     */
    private void clear(ClassLoader classLoader) {
        checkNotNull(classLoader, "classLoader is null");
        // Introspector缓存BeanInfo类来获得更好的性能。卸载时刷新所有Introspector的内部缓存。
        Introspector.flushCaches();
        // 从已经使用给定类加载器加载的缓存中移除所有资源包
        ResourceBundle.clearCache(classLoader);
        // Clear the introspection cache for the given ClassLoader
        CachedIntrospectionResults.clearClassLoader(classLoader);
        // clear logFactory resource
        LogFactory.release(classLoader);
    }

    /**
     * 关闭Spring上下文
     * 
     * @param applicationContext
     */
    private static void closeQuietly(ConfigurableApplicationContext applicationContext) {
        checkNotNull(applicationContext, "applicationContext is null");
        try {
            applicationContext.close();
        } catch (Exception e) {
            LOGGER.error("Failed to close application context", e);
        }
    }

    /**
     * 检查其可用性
     *
     * @date 2018年3月27日
     * @author jiangyiwang
     */
    private void checkUseable() {
        if (ModuleState.USEABLE != moduleState) {
            throw new ModuleUnuseableException(this, "Module not useable.The module state is " + moduleState.name());
        }
    }

    @Override
    public ModuleConfig getModuleConfig() {
        return moduleConfig;
    }

    @Override
    public ModuleState getModuleState() {
        return this.moduleState;
    }

    @SuppressWarnings("unchecked")
    public <T> T getService(Class<T> serviceClass) {
        checkUseable();
        return (T) serviceCache.get(serviceClass);
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.applicationContext.getClassLoader();
    }

    @Override
    public ModuleMetrics getModuleMetrics() {
        return null;
    }

    @Override
    public Map<Class<?>, Object> getServices() {
        checkUseable();
        return serviceCache;
    }

    @Override
    public void stop() {
        if (this.moduleState != ModuleState.USEABLE) {
            LOGGER.info("Stop module fail.Because of module not useable!The current state of module is {}", moduleState.name());
            return;
        }
        this.moduleState = ModuleState.STOPED;
    }

    @Override
    public void start() {
        if (this.moduleState != ModuleState.STOPED) {
            LOGGER.info("Start module fail.Because of module state is not stoped!");
        }
        this.moduleState = ModuleState.USEABLE;
    }

}
