package yunnex.mdl;

/**
 * 模块容器，负责以下几个事情:<br/>
 * 1、容器启动时自动加载所有模块<br/>
 * 2、如果模块资源有更新，则根据最新资源自动加载<br/>
 * 3、destory的时候需要将所有已加载的模块进行销毁以释放资源
 * 
 * @author jingyiwang
 * @date 2018年3月29日
 */
public interface ModuleContainer {

    /**
     * 获取上下文
     *
     * @return
     * @date 2018年3月29日
     * @author jiangyiwang
     */
    ModuleContext getContext();

    /**
     * 启动容器,自动扫描模块并加载
     *
     * @date 2018年3月29日
     * @author jiangyiwang
     */
    void start();

    /**
     * 模块容器销毁，将执行module的destory方法
     *
     * @date 2018年3月29日
     * @author jiangyiwang
     */
    void destory();

}
