package yunnex.mdl;

/**
 * 模块的状态变更监听
 * 
 * @author jingyiwang
 * @date 2018年3月26日
 */
public interface ModuleStateChangeListener {

    /**
     * 状态变更，具体状态通过module.getModuleState()进行获取
     *
     * @param module
     * @date 2018年3月26日
     * @author jiangyiwang
     */
    void onChange(Module module);

}
