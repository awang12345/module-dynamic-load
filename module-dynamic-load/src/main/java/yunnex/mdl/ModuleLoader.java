package yunnex.mdl;

/**
 * 模块加载器
 * 
 * @author jingyiwang
 * @date 2018年3月28日
 */
public interface ModuleLoader {

    /**
     * 根据配置加载一个模块<br/>
     * 此时没有使用ModuleManager进行register的话是不能被调用到的，这个loader方法只是激活模块，使它有提供服务的能力
     * 
     * @param moduleConfig 模块配置信息
     *
     * @return 加载成功的模块,如果加载失败，则返回NULL
     */
    Module load(ModuleConfig moduleConfig);

    /**
     * 资源释放
     *
     * @date 2018年3月29日
     * @author jiangyiwang
     */
    void destory();

}
