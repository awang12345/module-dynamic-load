package yunnex.mdl.support;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.toArray;
import static org.apache.commons.lang.StringUtils.isBlank;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.google.common.base.Throwables;
import com.google.common.collect.Lists;

import yunnex.mdl.Module;
import yunnex.mdl.ModuleConfig;
import yunnex.mdl.ModuleLoader;

/**
 * 模块加载器实现
 * 
 * @author jingyiwang
 * @date 2018年3月30日
 */
public class SpringModuleLoader implements ModuleLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringModuleLoader.class);

    /**
     * Spring bean文件所在目录
     */
    public final static String SPRING_XML_PATTERN = "classpath*:META-INF/spring/*.xml";
    /**
     * Spring bean文件所在目录,不同的路径确保能取到资源
     */
    public final static String SPRING_XML_PATTERN2 = "classpath*:*META-INF/spring/*.xml";

    /**
     * 模块版本属性
     */
    private static final String MODULE_PROPERTY_VERSION = "module_version";

    /**
     * 模块名属性
     */
    private static final String MODULE_PROPERTY_NAME = "module_name";

    /**
     * 不加载的Spring配置文件
     */
    private static final String MODULE_EXCLUSION_CONFIGE_NAME = "exclusion_confige_name";

    /**
     * 创建一个新的ClassLoadr加载jar里的class，初始化Spring ApplicationContext等
     */
    @Override
    public Module load(ModuleConfig moduleConfig) {
        try {
            LOGGER.info("Loading module: {}", moduleConfig);
            if (moduleConfig == null || !moduleConfig.isValid()) {
                return null;
            }
            List<String> tempFileJarURLs = moduleConfig.getModuleUrlPath();
            LOGGER.info("Local jars: {}", tempFileJarURLs);
            ConfigurableApplicationContext moduleApplicationContext = loadModuleApplication(moduleConfig, tempFileJarURLs);
            LOGGER.info("Loading module  complete：{}", moduleConfig);
            return new SpringModule(moduleConfig, moduleApplicationContext);
        } catch (Exception ex) {
            LOGGER.error("Load module error!ModuleConfig=" + moduleConfig.toString(), ex);
        }
        return null;
    }

    /**
     * 根据本地临时文件Jar，初始化模块自己的ClassLoader，初始化Spring Application
     * Context，同时要设置当前线程上下文的ClassLoader问模块的ClassLoader
     *
     * @param moduleConfig
     * @param tempFileJarURLs
     * @return
     */
    private ClassPathXmlApplicationContext loadModuleApplication(ModuleConfig moduleConfig, List<String> tempFileJarURLs) {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        // 获取模块的ClassLoader
        ClassLoader moduleClassLoader = new ModuleClassLoader(moduleConfig.getModuleUrl(), SystemConfig.getApplicationContext().getClassLoader(),
                        getOverridePackages(moduleConfig));
        try {
            // 把当前线程的ClassLoader切换成模块的
            Thread.currentThread().setContextClassLoader(moduleClassLoader);
            ModuleSpringApplicationContext moduleApplicationContext = new ModuleSpringApplicationContext(SystemConfig.getApplicationContext());
            Properties properties = getProperties(moduleConfig);
            moduleApplicationContext.setProperties(properties);
            moduleApplicationContext.setClassLoader(moduleClassLoader);
            moduleApplicationContext
                            .setConfigLocations(findSpringConfigs(tempFileJarURLs, moduleClassLoader, getExclusionConfigeNameList(properties)));
            moduleApplicationContext.refresh();
            return moduleApplicationContext;
        } catch (Throwable e) {
            CachedIntrospectionResults.clearClassLoader(moduleClassLoader);
            throw Throwables.propagate(e);
        } finally {
            // 还原当前线程的ClassLoader
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }

    /**
     * 获取不加载的spring配置文件名称
     * 
     * @param properties
     * @return
     */
    private List<String> getExclusionConfigeNameList(Properties properties) {
        String property = properties.getProperty(MODULE_EXCLUSION_CONFIGE_NAME);
        if (property != null) {
            return Lists.newArrayList(property.split(","));
        }
        return Lists.newArrayList();

    }

    /**
     * 获得模块的配置，并会增加一些模块的常量信息
     *
     * @param moduleConfig
     * @return
     */
    private Properties getProperties(ModuleConfig moduleConfig) {
        Properties properties = toProperties(moduleConfig.getProperties());
        properties.setProperty(MODULE_PROPERTY_NAME, moduleConfig.getName());
        properties.setProperty(MODULE_PROPERTY_VERSION, moduleConfig.getVersion());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Module Properties: {}", properties);
        }
        return properties;
    }

    /**
     * 查找资源(JAR)中的Spring配置文件
     *
     * @param tempFileJarURLs
     * @param moduleClassLoader
     * @return
     */
    private String[] findSpringConfigs(List<String> tempFileJarURLs, ClassLoader moduleClassLoader, List<String> exclusionConfigeNameList) {
        try {
            PathMatchingResourcePatternResolver pmr = new PathMatchingResourcePatternResolver(moduleClassLoader);
            List<Resource> resourceList = new LinkedList<>();
            CollectionUtils.addAll(resourceList, pmr.getResources(SPRING_XML_PATTERN));
            CollectionUtils.addAll(resourceList, pmr.getResources(SPRING_XML_PATTERN2));
            Resource[] resources = resourceList.toArray(new Resource[] {});
            checkNotNull(resources, "resources is null");
            checkArgument(resources.length > 0, "resources length is 0");
            // 因为ClassLoader是树形结构，这里会找到ModuleClassLoader以及其父类中所有符合规范的spring配置文件，所以这里需要过滤，只需要ModuleJar中的
            return filterURLsIncludedResources(tempFileJarURLs, resources, exclusionConfigeNameList);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to find spring configs from " + tempFileJarURLs, e);
        }
    }

    /**
     * 过滤查找到的spring配置文件资源，只查找tempFileJarURLs中的spring配置文件
     *
     * @param tempFileJarURLs
     * @param resources
     * @param exclusionConfigeNameList
     * @return
     * @throws IOException
     */
    private String[] filterURLsIncludedResources(List<String> tempFileJarURLs, Resource[] resources, List<String> exclusionConfigeNameList)
                    throws IOException {
        List<String> configLocations = Lists.newArrayList();
        for (Resource resource : resources) {
            String configLocation = resource.getURL().toString();
            for (String url : tempFileJarURLs) {
                if (isExclusionConfig(configLocation, exclusionConfigeNameList)) {
                    if (LOGGER.isInfoEnabled()) {
                        LOGGER.info("exclusion url: {}", configLocation);
                    }
                    continue;
                }
                if (configLocation.contains(url)) {
                    configLocations.add(configLocation);
                }
            }
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Config locations: {}", configLocations);
        }
        return toArray(configLocations, String.class);
    }

    /**
     * 是否是需要不载入的spring配置
     *
     * @param url
     * @param exclusionConfigeNameList
     * @return
     */
    private boolean isExclusionConfig(String url, List<String> exclusionConfigeNameList) {
        for (String tmp : exclusionConfigeNameList) {
            if (url.contains(tmp)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 去除list中的空白元素，string.startWith("")==true
     *
     * @param moduleConfig
     * @return
     */
    private List<String> getOverridePackages(ModuleConfig moduleConfig) {
        List<String> list = Lists.newArrayList();
        for (String s : moduleConfig.getOverridePackages()) {
            if (!StringUtils.isBlank(s)) {
                list.add(s);
            }
        }
        return list;
    }

    /**
     * Map 转换为Properties
     *
     * @param map
     * @return
     */
    private static Properties toProperties(Map<String, Object> map) {
        Properties properties = new Properties();
        for (Entry<String, Object> each : map.entrySet()) {
            if (each.getKey() == null || each.getValue() == null) {
                if (LOGGER.isWarnEnabled()) {
                    LOGGER.warn("Ignore null properties: {}={}", each.getKey(), each.getValue());
                }
                continue;
            }
            if (isBlank(each.getKey())) {
                continue;
            }
            properties.setProperty(each.getKey(), each.getValue().toString());
        }
        return properties;
    }

    @Override
    public void destory() {

    }

}
