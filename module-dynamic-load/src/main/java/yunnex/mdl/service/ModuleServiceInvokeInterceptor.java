package yunnex.mdl.service;

import yunnex.mdl.bean.ModuleServiceInvokeParameter;

/**
 * 模块业务方法执行拦截器
 * 
 * @author jingyiwang
 * @date 2018年4月2日
 */
public interface ModuleServiceInvokeInterceptor {

    /**
     * 业务方法执行之前
     *
     * @param parameter 执行参数
     * @return
     * @date 2018年4月2日
     * @author jiangyiwang
     */
    boolean beforInvoke(ModuleServiceInvokeParameter parameter);

    /**
     * 业务方法执行之后
     * 
     *
     * @param parameter 执行参数
     * @param result 执行结果，null表示方法没返回或者执行报错
     * @param throwable 执行异常信息
     * @throws Throwable
     * @date 2018年4月2日
     * @author jiangyiwang
     */
    void afterInvoke(ModuleServiceInvokeParameter parameter, Object result, Throwable throwable) throws Throwable;

}
