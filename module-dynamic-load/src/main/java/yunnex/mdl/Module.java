package yunnex.mdl;

import java.util.Date;
import java.util.Map;

import yunnex.mdl.constans.ModuleState;

/**
 * 模块接口 <br/>
 * 模块接口的报错不能影响其他模块，所有的对外异常只能为ModuleRuntimeException类型或者其子类异常
 * 
 * @author jingyiwang
 * @date 2018年4月3日
 */
public interface Module {

    /**
     * 模块名
     *
     * @return
     */
    String getName();

    /**
     * 模块版本号
     *
     * @return
     */
    String getVersion();

    /**
     * 模块的创建时间
     *
     * @return
     */
    Date getCreation();

    /**
     * 获取模块状态
     *
     * @return
     * @date 2018年3月26日
     * @author jiangyiwang
     */
    ModuleState getModuleState();

    /**
     * 查找处理请求的Service
     *
     * @param service类型
     * @return
     */
    <T> T getService(Class<T> serviceClass);

    /**
     * 获取全部的Service
     *
     * @return service类型和service对象
     */
    Map<Class<?>, Object> getServices();

    /**
     * 获取加载当前module的classloader
     *
     * @return
     */
    ClassLoader getClassLoader();

    /**
     * 获取模块配置
     *
     * @return
     */
    ModuleConfig getModuleConfig();

    /**
     * 获取模块运行过程中的指标数据
     *
     * @return
     * @date 2018年3月26日
     * @author jiangyiwang
     */
    ModuleMetrics getModuleMetrics();

    /**
     * 停止模块进行服务提供，此时外面请求将会被拒绝，已经接收的请求继续处理，调用restart方法恢复(只有useable状态的才能进行此操作)
     *
     * @date 2018年3月26日
     * @author jiangyiwang
     */
    void stop();

    /**
     * 当模块状态处理stoped时，调用该方法来重新激活模块，使其能够对外提供服务
     *
     * @date 2018年3月26日
     * @author jiangyiwang
     */
    void start();

    /**
     * 销毁,不可重新启用服务
     */
    void destroy();

}
