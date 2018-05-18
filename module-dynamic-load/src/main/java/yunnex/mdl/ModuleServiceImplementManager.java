package yunnex.mdl;

import yunnex.mdl.bean.ModuleServiceHolder;

/**
 * 模块提供的业务服务管理
 * 
 * @author jingyiwang
 * @date 2018年3月29日
 */
public interface ModuleServiceImplementManager {

    /**
     * 根据class类型获取对应的service<br/>
     * 为何没有根据module进行获取，是因为获取module需要预先知道模块名称，为了方便使用故实现直接通过class获取对应服务
     *
     * @param serviceClass
     * @return 没找到返回NULL
     * @date 2018年3月29日
     * @author jiangyiwang
     */
    ModuleServiceHolder getService(Class<?> serviceClass);

    /**
     * 当模块发生变化的时候需要调用该方法进行刷新
     *
     * @date 2018年3月29日
     * @author jiangyiwang
     */
    void refresh();

    /**
     * 销毁释放资源，调用该方法后所有的service关系将会被清除，外界将无法调用
     *
     * @date 2018年3月29日
     * @author jiangyiwang
     */
    void destory();

}
