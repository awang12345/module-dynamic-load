package yunnex.mdl.bean;

import java.lang.reflect.Method;

import yunnex.mdl.Module;

/**
 * 模块业务执行参数
 * 
 * @author jingyiwang
 * @date 2018年4月2日
 */
public class ModuleServiceInvokeParameter {

    // 目标对象
    private final Object target;
    // 执行方法
    private final Method method;
    // 执行参数
    private final Object[] args;
    // 执行的模块
    private final Module module;

    public ModuleServiceInvokeParameter(Object target, Method method, Object[] args, Module module) {
        super();
        this.target = target;
        this.method = method;
        this.args = args;
        this.module = module;
    }

    public Object getTarget() {
        return target;
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getArgs() {
        return args;
    }

    public Module getModule() {
        return module;
    }

}
