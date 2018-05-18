package yunnex.mdl;

/**
 * 模块运行的一些指标信息
 * 
 * @author jingyiwang
 * @date 2018年3月26日
 */
public interface ModuleMetrics {

    /**
     * 模块运行过程处理请求次数
     *
     * @return
     * @date 2018年3月26日
     * @author jiangyiwang
     */
    int getTotalProcessCount();

    /**
     * 模块运行时处理异常次数
     *
     * @return
     * @date 2018年3月26日
     * @author jiangyiwang
     */
    int getErrorProcessCount();

    /**
     * 获取运行时间
     *
     * @return
     * @date 2018年3月26日
     * @author jiangyiwang
     */
    long getRuntime();

}
