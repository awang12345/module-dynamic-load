package yunnex.mdl.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import yunnex.mdl.ModuleContext;
import yunnex.mdl.ModuleLoader;
import yunnex.mdl.ModuleManager;
import yunnex.mdl.ModuleServiceImplementManager;
import yunnex.mdl.ModuleStateChangeListenerManger;

/**
 * 默认模块上下文实现
 * 
 * @author jingyiwang
 * @date 2018年3月29日
 */
public class DefaultModuleContext extends SpringInit implements ModuleContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultModuleContext.class);

    private ModuleStateChangeListenerManger moduleStateChangeListenerManger;// 模块状态监听管理器
    private ModuleServiceImplementManager moduleServiceImplementManager;// 默认模块业务服务管理器
    private ModuleManager moduleManager;// 模块管理器
    private ModuleLoader moduleLoader; // 默认为spring模块加载器

    public DefaultModuleContext() {
        super();
    }

    @Override
    public ModuleManager getMouleManager() {
        return moduleManager;
    }

    @Override
    public ModuleStateChangeListenerManger getStateListenerManger() {
        return moduleStateChangeListenerManger;
    }

    public void setModuleServiceImplementManager(ModuleServiceImplementManager moduleServiceImplementManager) {
        this.moduleServiceImplementManager = moduleServiceImplementManager;
    }

    @Override
    public ModuleLoader getModuleLoader() {
        return moduleLoader;
    }

    public ModuleStateChangeListenerManger getModuleStateChangeListenerManger() {
        return moduleStateChangeListenerManger;
    }

    public void setModuleStateChangeListenerManger(ModuleStateChangeListenerManger moduleStateChangeListenerManger) {
        this.moduleStateChangeListenerManger = moduleStateChangeListenerManger;
    }

    public ModuleServiceImplementManager getModuleServiceImplementManager() {
        return moduleServiceImplementManager;
    }

    public ModuleManager getModuleManager() {
        return moduleManager;
    }

    public void setModuleManager(ModuleManager moduleManager) {
        this.moduleManager = moduleManager;
    }

    public void setModuleLoader(ModuleLoader moduleLoader) {
        this.moduleLoader = moduleLoader;
    }

    @Override
    public void destory() {
        LOGGER.info("Start moduleContext destory..");
        if (moduleManager != null) {
            moduleManager.destory();
        }
        if (moduleServiceImplementManager != null) {
            moduleServiceImplementManager.destory();
        }
        if (moduleStateChangeListenerManger != null) {
            moduleStateChangeListenerManger.destory();
        }
        LOGGER.info("End moduleContext destory!!");
    }

    @Override
    public void start() {
//        if (moduleServiceImplementManager != null) {
//            moduleServiceImplementManager.refresh();
//        }
    }

    @Override
    public ModuleServiceImplementManager getServiceImplementManger() {
        return this.moduleServiceImplementManager;
    }

    @Override
    protected void afterInit(ApplicationContext applicationContext) {
        if (this.moduleStateChangeListenerManger == null) {
            this.moduleStateChangeListenerManger = getBean(ModuleStateChangeListenerManger.class, new SpringModuleStateChangeListenerManager());
        }
        if (this.moduleServiceImplementManager == null) {
            this.moduleServiceImplementManager = getBean(ModuleServiceImplementManager.class, new DefaultModuleServiceImplementManager());
        }
        if (this.moduleManager == null) {
            this.moduleManager = getBean(ModuleManager.class, new DefaultModuleManager());
        }
        if (this.moduleLoader == null) {
            this.moduleLoader = getBean(ModuleLoader.class, new SpringModuleLoader());
        }
    }

}
