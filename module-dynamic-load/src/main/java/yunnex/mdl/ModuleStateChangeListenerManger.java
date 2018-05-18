package yunnex.mdl;

import java.util.List;

/**
 * 模块状态变更监听器管理
 * 
 * @author jingyiwang
 * @date 2018年3月26日
 */
public interface ModuleStateChangeListenerManger {

    /**
     * 注册监听器
     *
     * @param listener
     * @date 2018年3月26日
     * @author jiangyiwang
     * @return 是否注册成功
     */
    boolean registerListener(ModuleStateChangeListener listener);

    /**
     * 注销监听器
     *
     * @param listener
     * @date 2018年3月26日
     * @author jiangyiwang
     * @return 是否注销成功
     */
    boolean unregisterListenter(ModuleStateChangeListener listener);

    /**
     * 获取所有的监听器
     *
     * @return
     * @date 2018年3月26日
     * @author jiangyiwang
     */
    List<ModuleStateChangeListener> getListenters();

    /**
     * 触发模块状态更新
     *
     * @param module
     * @date 2018年3月26日
     * @author jiangyiwang
     */
    void onFireModuleStateChange(Module module);

    /**
     * 销毁操作，删除所有已注册的监听器
     *
     * @date 2018年3月29日
     * @author jiangyiwang
     */
    void destory();

}
