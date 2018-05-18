package yunnex.mdl;

/**
 * 模块容器监听器
 * 
 * @author jingyiwang
 * @date 2018年4月2日
 */
public interface ModuleContainerListener {

    /**
     * 容器启动时执行
     *
     * @param container
     * @date 2018年4月2日
     * @author jiangyiwang
     */
    void containerStart(ModuleContainer container);

    /**
     * 容器销毁时执行
     *
     * @param moduleContainer
     * @date 2018年4月2日
     * @author jiangyiwang
     */
    void containerDestory(ModuleContainer moduleContainer);

}
