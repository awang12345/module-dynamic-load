package yunnex.mdl.support;

import static com.google.common.collect.Maps.transformValues;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;

import yunnex.mdl.Module;
import yunnex.mdl.ModuleConfig;
import yunnex.mdl.ModuleContext;
import yunnex.mdl.ModuleResource;
import yunnex.mdl.exeception.ModuleRuntimeException;

/**
 * 模块重新加载,根据实际情况进行加载，有如下情况出现 <br/>
 * 1、如果删除Module资源，这个模块将进行destory<br/>
 * 2、如果新增Module资源，这个模块将进行load<br/>
 * 3、如果更新Module资源，这个模块的已加载的将会卸载，然后重新加载新的模块<br/>
 * 
 * @author jingyiwang
 * @date 2018年3月28日
 */
public abstract class AbstractModuleRefresh {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractModuleRefresh.class);

    // 最后一次刷新时间
    private Date lastRefreshTime;

    /**
     * 获取模块配置信息
     *
     * @return
     */
    protected abstract List<ModuleResource> queryModuleResources();

    /**
     * 重新载入模块,不支持多线程同时refresh
     */
    public synchronized void refreshModules() {
        LOGGER.info("Start refresh module.The last refresh time is {}", lastRefreshTime);
        ModuleContext moduleContext = SystemConfig.getModuleContext();
        if (moduleContext == null) {
            throw new ModuleRuntimeException("Reload module fail!Because of moduleContext is null.");
        }
        // 查找状态为ENABLED的ModuleConfig，并以模块名作为Key，放到Map中
        Map<String, ModuleConfig> moduleConfigs = indexModuleConfigByModuleName(queryModuleConfigs());

        // 转换Map的Value，提取Module的Version，Map的Key为name，Value为Version
        Map<String, String> configVersions = transformToConfigVersions(moduleConfigs);
        // 获取当前内存中，也就是ModuleManager已经加载的模板版本，同样Map的Key为name，Value为Version
        Map<String, String> moduleVersions = transformToModuleVersions(moduleContext.getMouleManager().getModules());
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Config size: {}", configVersions.size());
            LOGGER.info("Module size: {}", moduleVersions.size());
            LOGGER.info("now in map {}", moduleVersions);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Config versions: {}", configVersions);
            LOGGER.debug("Module versions: {}", moduleVersions);
        }
        // 找出配置与当前内存里配置的不同
        MapDifference<String, String> difference = Maps.difference(configVersions, moduleVersions);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Version difference: {}", difference);
        }
        // 配置新增的
        putModules(moduleConfigs, configAdds(difference));
        // 配置版本与模块不同的
        putModules(moduleConfigs, configDifference(difference));
        // 模块多余的
        removeModules(modulesRedundant(difference));
        // 更新刷新时间
        this.lastRefreshTime = new Date();
        LOGGER.info("Finished refresh module!");
    }

    /**
     * 获取需要更新的模块配置
     *
     * @return
     * @date 2018年3月28日
     * @author jiangyiwang
     */
    private List<ModuleConfig> queryModuleConfigs() {
        List<ModuleResource> moduleResources = queryModuleResources();
        if (moduleResources == null || moduleResources.isEmpty()) {
            return Lists.newArrayList();
        }
        List<ModuleConfig> moduleConfigs = Lists.newLinkedList();
        for (ModuleResource resource : moduleResources) {
            CollectionUtils.addIgnoreNull(moduleConfigs, resource.getResuorce());
        }
        return moduleConfigs;
    }

    /**
     * 根据dataProviders指定的ModuleConfig初始化模块，并放入ModuleManager中
     *
     * @param moduleConfigs
     * @param moduleNames
     */
    private void putModules(Map<String, ModuleConfig> moduleConfigs, Set<String> moduleNames) {
        for (String name : moduleNames) {
            ModuleConfig moduleConfig = moduleConfigs.get(name);
            try {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Load module config: {}", moduleConfig);
                }
                SystemConfig.getModuleContext().getMouleManager().installModule(moduleConfig);
            } catch (Exception e) {
                LOGGER.error("Failed to load module config: " + moduleConfig, e);
            } catch (Error e) {
                LOGGER.error("Failed to load module config: " + moduleConfig, e);
            }
        }
    }

    /**
     * 移除并且卸载模块
     *
     * @param modulesRedundant
     */
    private void removeModules(Set<String> modulesRedundant) {
        for (String moduleName : modulesRedundant) {
            try {} catch (Exception ex) {
                LOGGER.warn("Destory module:" + moduleName + " error!", ex);
            }
        }
    }

    /**
     * 根据对比的结果，查找多余的模块，
     *
     * @param difference
     * @return
     */
    private Set<String> modulesRedundant(MapDifference<String, String> difference) {
        return difference.entriesOnlyOnRight().keySet();
    }

    /**
     * 根据对比结果，查找版本不同的模块
     *
     * @param difference
     * @return
     */
    private Set<String> configDifference(MapDifference<String, String> difference) {
        return difference.entriesDiffering().keySet();
    }

    /**
     * 根据对比结果，查找新增的模块
     *
     * @param difference
     * @return
     */
    private Set<String> configAdds(MapDifference<String, String> difference) {
        return difference.entriesOnlyOnLeft().keySet();
    }

    /**
     * 将一个Module List，转换为Map，Key为 name，Value为Version
     *
     * @param modules
     * @return
     */
    private Map<String, String> transformToModuleVersions(List<Module> modules) {
        return ImmutableMap.copyOf(transformValues(Maps.uniqueIndex(modules, new Function<Module, String>() {
            @Override
            public String apply(Module input) {
                return input.getName();
            }
        }), new Function<Module, String>() {
            @Override
            public String apply(Module input) {
                return input.getVersion();
            }
        }));
    }

    /**
     * 提取Map中Value的Version，转换成新的Map，Key为name，Value为Version
     *
     * @param moduleConfigs
     * @return
     */
    private Map<String, String> transformToConfigVersions(Map<String, ModuleConfig> moduleConfigs) {
        return ImmutableMap.copyOf(transformValues(moduleConfigs, new Function<ModuleConfig, String>() {
            @Override
            public String apply(ModuleConfig input) {
                return input.getVersion();
            }
        }));
    }

    /**
     * 将ModuleConfig List转换成为Map，Key为name，Value为ModuleConfig
     *
     * @param list
     * @return
     */
    private Map<String, ModuleConfig> indexModuleConfigByModuleName(Collection<ModuleConfig> list) {

        return ImmutableMap.copyOf(Maps.uniqueIndex(list, new Function<ModuleConfig, String>() {
            @Override
            public String apply(ModuleConfig input) {
                return input.getName();
            }
        }));
    }

    public Date getLastRefreshTime() {
        return lastRefreshTime;
    }

}
