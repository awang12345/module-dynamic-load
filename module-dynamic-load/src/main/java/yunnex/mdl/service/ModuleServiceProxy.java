package yunnex.mdl.service;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yunnex.mdl.ModuleServiceImplementManager;
import yunnex.mdl.bean.ModuleServiceHolder;
import yunnex.mdl.bean.ModuleServiceInvokeParameter;
import yunnex.mdl.exeception.ModuleRuntimeException;
import yunnex.mdl.exeception.ModuleServiceImplementNotFoundException;

/**
 * 模块业务代理
 * 
 * @author jingyiwang
 * @date 2018年4月2日
 */
public class ModuleServiceProxy implements InvocationHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModuleServiceProxy.class);

    // 业务接口类型
    private Class<?> serviceInterfaceClass;

    // 业务接口是实现管理
    private ModuleServiceImplementManager moduleServiceImplementManager;

    // 模块业务执行拦截器管理
    private ModuleServiceInvokeInterceptor moduleServiceInvokeInterceptor;

    public ModuleServiceProxy(Class<?> serviceInterfaceClass, ModuleServiceImplementManager moduleServiceImplementManager,
                    ModuleServiceInvokeInterceptor moduleServiceInvokeInterceptor) {
        super();
        if (serviceInterfaceClass == null || moduleServiceImplementManager == null) {
            throw new ModuleRuntimeException(
                            "New ModuleServiceProxy instances error.Because of attribute serviceInterfaceClass or moduleServiceImplementManager is null!");
        }
        this.serviceInterfaceClass = serviceInterfaceClass;
        this.moduleServiceImplementManager = moduleServiceImplementManager;
        this.moduleServiceInvokeInterceptor = moduleServiceInvokeInterceptor;
    }

    @Override
    public Object invoke(Object target, Method method, Object[] args) throws Throwable {
        ModuleServiceInvokeParameter parameter = builderParameter(method, args);
        // 方法执行之前处理
        if (!beforInvoke(parameter)) {
            LOGGER.debug("Befor invoke service:{} method:{} not accpet interceptor!", serviceInterfaceClass, method.getName());
            // 没有通过调用前方法执行
            return null;
        }
        // 真正执行
        Object result = null;// 方法执行结果
        Throwable invokeError = null;// 方法执行异常
        try {
            result = actualInvoke(parameter);
            return result;
        } catch (Throwable e) {
            invokeError = e;
            throw e;
        } finally {
            // 方法执行之后
            afterInvoke(parameter, result, invokeError);
        }
    }

    /**
     * 构造业务方法执行参数
     *
     * @return
     * @date 2018年4月2日
     * @author jiangyiwang
     */
    private ModuleServiceInvokeParameter builderParameter(Method method, Object[] args) {
        // 这里每次都从manager里面去取没有直接取到之后做映射是有原因的，原因：1、模块替换之后manager里面的映射关系会更新，如果直接引用，那么替换之后拿不到最新的service实现
        // 2、替换module模块jar包时，可以有助于原来的类卸载
        ModuleServiceHolder moduleServiceHolder = moduleServiceImplementManager.getService(serviceInterfaceClass);
        if (moduleServiceHolder == null) {
            throw new ModuleServiceImplementNotFoundException(serviceInterfaceClass.getName() + " not found implement!");
        }
        return new ModuleServiceInvokeParameter(moduleServiceHolder.getService(), method, args, moduleServiceHolder.getModule());
    }

    /**
     * 真是执行模块业务
     *
     * @param parameter
     * @param moduleServiceHolder
     * @return
     * @throws Throwable
     * @date 2018年4月2日
     * @author jiangyiwang
     */
    private Object actualInvoke(ModuleServiceInvokeParameter parameter) throws Throwable {
        // 保存当前线程的类加载器
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try {
            // 这边要设置为模块的classloader，不然会报classNotFound错误，因为当前线程的classloader是moduler的parent，
            // parent是无法找到子里面的类，所以执行模块里面的方法需要重新设置当前的类加载器
            Thread.currentThread().setContextClassLoader(parameter.getTarget().getClass().getClassLoader());
            return parameter.getMethod().invoke(parameter.getTarget(), parameter.getArgs());
        } catch (Throwable e) {
            LOGGER.warn(StringUtils.join("Module service invoke error!service=", serviceInterfaceClass, "|method=", parameter.getMethod().getName(),
                            "|args=", parameter.getArgs()), e);
            throw e;
        } finally {
            // 用完之后，需要重新将当前类加载器还原回去
            Thread.currentThread().setContextClassLoader(classLoader);
        }
    }

    /**
     * 方法执行之前
     *
     * @param parameter
     * @date 2018年4月2日
     * @author jiangyiwang
     */
    private boolean beforInvoke(ModuleServiceInvokeParameter parameter) {
        if (moduleServiceInvokeInterceptor != null) {
            return moduleServiceInvokeInterceptor.beforInvoke(parameter);
        }
        return true;
    }

    /**
     * 方法执行之后
     *
     * @param parameter
     * @param result
     * @param invokeError
     * @throws Throwable
     * @date 2018年4月2日
     * @author jiangyiwang
     */
    private void afterInvoke(ModuleServiceInvokeParameter parameter, Object result, Throwable invokeError) throws Throwable {
        if (moduleServiceInvokeInterceptor != null) {
            moduleServiceInvokeInterceptor.afterInvoke(parameter, result, invokeError);
        }
    }

}
