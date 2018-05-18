package yunnex.mdl;

/**
 * 模块运行时上下文
 * 
 * @author jingyiwang
 * @date 2018年3月27日
 */
public interface ModuleContext {

    /**
     * 获取模块管理器
     *
     * @return
     * @date 2018年3月29日
     * @author jiangyiwang
     */
    ModuleManager getMouleManager();

    /**
     * 获取状态监听器管理器
     *
     * @return
     * @date 2018年3月29日
     * @author jiangyiwang
     */
    ModuleStateChangeListenerManger getStateListenerManger();

    /**
     * 获取模块业务实现管理
     *
     * @return
     * @date 2018年3月29日
     * @author jiangyiwang
     */
    ModuleServiceImplementManager getServiceImplementManger();

    /**
     * 获取模块载入器
     *
     * @return
     * @date 2018年3月29日
     * @author jiangyiwang
     */
    ModuleLoader getModuleLoader();
    
    /**
     * 开启
     *
     * @date 2018年3月30日
     * @author jiangyiwang
     */
    void start();

    /**
     * 销毁所有管理器
     *
     * @date 2018年3月29日
     * @author jiangyiwang
     */
    void destory();
}
