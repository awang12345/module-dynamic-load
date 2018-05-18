package yunnex.mdl.support;

import org.springframework.context.ApplicationContext;

import yunnex.mdl.Module;
import yunnex.mdl.ModuleContext;

/**
 * 系统运行时配置信息
 * 
 * @author jingyiwang
 * @date 2018年3月30日
 */
public final class SystemConfig {

    private SystemConfig() {}

    /**
     * 模块上下文
     */
    private volatile static ModuleContext moduleContext;

    /**
     * spring上下文
     */
    private volatile static ApplicationContext applicationContext;

    /**
     * 当前执行的模块
     */
    private static final ThreadLocal<Module> currentExcuteModule = new ThreadLocal<>();

    public static ModuleContext getModuleContext() {
        return moduleContext;
    }

    public static void setModuleContext(ModuleContext moduleContext) {
        SystemConfig.moduleContext = moduleContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        SystemConfig.applicationContext = applicationContext;
    }

    public static Module getCurrentExcuteModule() {
        return currentExcuteModule.get();
    }

    public static void setCurrentExcuteModule(Module currentExcuteModule) {
        SystemConfig.currentExcuteModule.set(currentExcuteModule);
    }

}
