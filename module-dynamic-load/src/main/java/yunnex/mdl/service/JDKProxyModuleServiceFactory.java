package yunnex.mdl.service;

import java.lang.reflect.Proxy;

import yunnex.mdl.ModuleServiceImplementManager;

/**
 * 使用jdk代理为模块业务生成实现
 * 
 * @author jingyiwang
 * @date 2018年4月2日
 */
public class JDKProxyModuleServiceFactory implements ModuleServiceFactory {

    // 业务接口是实现管理
    private ModuleServiceImplementManager moduleServiceImplementManager;

    // 模块业务执行拦截器管理
    private ModuleServiceInvokeInterceptor moduleServiceInvokerInterceptor;

    @SuppressWarnings("unchecked")
    @Override
    public <T> T newInstance(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[] {serviceClass},
                        new ModuleServiceProxy(serviceClass, moduleServiceImplementManager, moduleServiceInvokerInterceptor));
    }

    public void setModuleServiceImplementManager(ModuleServiceImplementManager moduleServiceImplementManager) {
        this.moduleServiceImplementManager = moduleServiceImplementManager;
    }

    public void setModuleServiceInvokerInterceptor(ModuleServiceInvokeInterceptor moduleServiceInvokerInterceptor) {
        this.moduleServiceInvokerInterceptor = moduleServiceInvokerInterceptor;
    }

}
