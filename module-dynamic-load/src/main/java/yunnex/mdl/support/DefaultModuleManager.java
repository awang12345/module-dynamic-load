package yunnex.mdl.support;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import yunnex.mdl.Module;
import yunnex.mdl.ModuleConfig;
import yunnex.mdl.ModuleManager;
import yunnex.mdl.utils.ModuleUtils;

/**
 * 模块管理，包含获取模块，执行模块里的方法
 * 
 * @author jingyiwang
 * @date 2018年4月3日
 */
public class DefaultModuleManager implements ModuleManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultModuleManager.class);

    /**
     * 运行时模块,模块名:模块对象
     */
    private final ConcurrentHashMap<String, Module> modules = new ConcurrentHashMap<>();

    /**
     * 加载模块错误信息
     */
    private final ConcurrentHashMap<ModuleConfig, String> errorContext = new ConcurrentHashMap<>();

    @Override
    public List<Module> getModules() {
        return ImmutableList.copyOf(modules.values());
    }

    @Override
    public boolean installModule(ModuleConfig moduleConfig) {
        LOGGER.info("Install module by config:{}", moduleConfig);
        if (moduleConfig == null || !moduleConfig.isValid()) {
            LOGGER.info("Install module fail.Because of moduleConfig invalid!");
            errorContext.put(moduleConfig, "Install module fail.Because of moduleConfig invalid!");
            return false;
        }
        Module oldModule = modules.get(moduleConfig.getName());
        if (oldModule != null) {
            if (moduleConfig.isForceOverrideVersion() || ModuleUtils.isNewVersion(oldModule.getVersion(), moduleConfig.getVersion())) {
                // 如果是强制覆盖，或者版本比较新，则进行替换
                if (loadModule(moduleConfig)) {
                    oldModule.destroy();// 老的模块进行销毁
                    LOGGER.info("Update module:{} version from {} to {}", moduleConfig.getName(), oldModule.getVersion(), moduleConfig.getVersion());
                    return true;
                }
            }
            // 如果版本一样，则不进行更新操作
            LOGGER.info("Not need install the module.Same module verion existed!Name:{}|Version:{}", moduleConfig.getName(),
                            moduleConfig.getVersion());
            return false;
        }
        boolean success = loadModule(moduleConfig);
        LOGGER.info("Add module:{}|version:{}|success:{}", moduleConfig.getName(), moduleConfig.getVersion(), success);
        return success;
    }

    /**
     * 载入模块
     *
     * @param moduleConfig
     * @return
     * @date 2018年3月29日
     * @author jiangyiwang
     */
    private boolean loadModule(ModuleConfig moduleConfig) {
        try {
            Module newModule = SystemConfig.getModuleContext().getModuleLoader().load(moduleConfig);
            if (newModule != null) {
                modules.put(newModule.getName(), newModule);
                onModuleChangeEvent(newModule);
                return true;
            }
        } catch (Exception ex) {
            LOGGER.warn("Load module error!ModuleConfig:" + moduleConfig.toString(), ex);
        }
        return false;
    }

    @Override
    public boolean stopModule(String moduleName) {
        Module module = findModule(moduleName);
        if (module != null) {
            module.stop();
            onModuleChangeEvent(module);
            return true;
        }
        return false;
    }

    @Override
    public boolean restartModule(String moduleName) {
        Module module = findModule(moduleName);
        if (module != null) {
            module.start();
            onModuleChangeEvent(module);
            return true;
        }
        return false;
    }

    @Override
    public boolean uninstallModule(String moduleName) {
        Module module = findModule(moduleName);
        if (module != null) {
            modules.remove(module);
            module.destroy();
            return true;
        }
        return false;
    }

    @Override
    public Module findModule(String moduleName) {
        if (StringUtils.isNoneBlank(moduleName)) {
            modules.get(moduleName.toUpperCase());
        }
        return null;
    }

    /**
     * 触发模块变更事件
     *
     * @param module
     * @date 2018年3月30日
     * @author jiangyiwang
     */
    private void onModuleChangeEvent(Module module) {
        SystemConfig.getModuleContext().getStateListenerManger().onFireModuleStateChange(module);
    }

    @Override
    public void destory() {
        for (Module each : modules.values()) {
            try {
                each.destroy();
            } catch (Exception e) {
                LOGGER.error("Failed to destroy module: " + each.getName(), e);
            }
        }
        modules.clear();
    }

    @Override
    public Map<ModuleConfig, String> getModuleInstallErrorMap() {
        return errorContext;
    }

}
