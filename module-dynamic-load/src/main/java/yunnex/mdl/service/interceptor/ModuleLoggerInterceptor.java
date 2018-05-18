package yunnex.mdl.service.interceptor;

import org.apache.log4j.MDC;

import yunnex.mdl.Module;
import yunnex.mdl.bean.ModuleServiceInvokeParameter;
import yunnex.mdl.service.ModuleServiceInvokeInterceptor;

/**
 * 模块日志加上模块和版本号
 * 
 * @author jingyiwang
 * @date 2018年4月2日
 */
public class ModuleLoggerInterceptor implements ModuleServiceInvokeInterceptor {

    @Override
    public boolean beforInvoke(ModuleServiceInvokeParameter parameter) {
        Module module = parameter.getModule();
        MDC.put("moduleName", module.getName());
        MDC.put("moduleVersion", module.getVersion());
        return true;
    }

    @Override
    public void afterInvoke(ModuleServiceInvokeParameter parameter, Object result, Throwable throwable) throws Throwable {
        MDC.remove("moduleName");
        MDC.remove("moduleVersion");
    }

}
