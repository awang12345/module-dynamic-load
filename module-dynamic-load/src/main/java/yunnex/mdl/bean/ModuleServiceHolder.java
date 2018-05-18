package yunnex.mdl.bean;

import yunnex.mdl.Module;

/**
 * 模块业务绑定器
 * 
 * @author jingyiwang
 * @date 2018年3月29日
 */
public class ModuleServiceHolder {

    /** 业务 */
    private final Object service;

    /** 业务所属模块 */
    private final Module module;

    /**
     * @param service 模块业务
     * @param module 业务所属模块
     */
    public ModuleServiceHolder(Object service, Module module) {
        super();
        this.service = service;
        this.module = module;
    }

    @SuppressWarnings("unchecked")
    public <T> T getService() {
        return (T) service;
    }

    public Module getModule() {
        return module;
    }

}
