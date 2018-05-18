package yunnex.mdl.service;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.springframework.context.ApplicationContext;

import com.google.common.collect.Lists;

import yunnex.mdl.bean.ModuleServiceInvokeParameter;
import yunnex.mdl.support.SpringInit;

/**
 * 组合模块业务执行拦截器
 * 
 * @author jingyiwang
 * @date 2018年4月2日
 */
public class CompositeModuleServiceInvokeInterceptor extends SpringInit implements ModuleServiceInvokeInterceptor {

    private List<ModuleServiceInvokeInterceptor> interceptors = Lists.newLinkedList();

    @Override
    public boolean beforInvoke(ModuleServiceInvokeParameter parameter) {
        if (!interceptors.isEmpty()) {
            for (ModuleServiceInvokeInterceptor interceptor : interceptors) {
                if (!interceptor.beforInvoke(parameter)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void afterInvoke(ModuleServiceInvokeParameter parameter, Object result, Throwable throwable) throws Throwable {
        if (!interceptors.isEmpty()) {
            for (ModuleServiceInvokeInterceptor interceptor : interceptors) {
                interceptor.afterInvoke(parameter, result, throwable);
            }
        }
    }

    @Override
    protected void afterInit(ApplicationContext applicationContext) {
        Map<String, ModuleServiceInvokeInterceptor> interceptorMap = applicationContext.getBeansOfType(ModuleServiceInvokeInterceptor.class);
        if (MapUtils.isNotEmpty(interceptorMap)) {
            interceptors.addAll(interceptorMap.values());
        }
    }

}
