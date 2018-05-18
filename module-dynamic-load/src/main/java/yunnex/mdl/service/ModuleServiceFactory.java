package yunnex.mdl.service;

/**
 * 模块业务代理工厂类
 * 
 * @author jingyiwang
 * @date 2018年4月2日
 */
public interface ModuleServiceFactory {

    /**
     * 为接口创建一个代理
     *
     * @param serviceClass
     * @return
     * @date 2018年4月2日
     * @author jiangyiwang
     */
    <T> T newInstance(Class<T> serviceInterfaceClass);

}
