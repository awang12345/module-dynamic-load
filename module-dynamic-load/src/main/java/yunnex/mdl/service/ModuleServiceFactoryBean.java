package yunnex.mdl.service;

import org.springframework.beans.factory.FactoryBean;

/**
 * 模块业务工厂Bean
 * 
 * @author jingyiwang
 * @date 2018年4月2日
 * @param <T>
 */
public class ModuleServiceFactoryBean<T> implements FactoryBean<T> {

    // 业务接口类型
    private Class<T> serviceInterface;

    // 模块业务工厂
    private ModuleServiceFactory moduleServiceFactory;

    @Override
    public T getObject() throws Exception {
        return moduleServiceFactory.newInstance(serviceInterface);
    }

    @Override
    public Class<T> getObjectType() {
        return serviceInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public Class<T> getServiceInterface() {
        return serviceInterface;
    }

    public void setServiceInterface(Class<T> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    public ModuleServiceFactory getModuleServiceFactory() {
        return moduleServiceFactory;
    }

    public void setModuleServiceFactory(ModuleServiceFactory moduleServiceFactory) {
        this.moduleServiceFactory = moduleServiceFactory;
    }

}
