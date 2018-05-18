package yunnex.mdl;

/**
 * 模块扫描器,根据实际扫描情况进行加载，有如下情况出现 <br/>
 * 1、如果删除Module资源，这个模块将进行destory<br/>
 * 2、如果新增Module资源，这个模块将进行load<br/>
 * 3、如果更新Module资源，这个模块的已加载的将会卸载，然后重新加载新的模块<br/>
 * 
 * @author jingyiwang
 * @date 2018年3月30日
 */
public interface ModuleScanner {

    /**
     * 开启扫描器
     *
     * @date 2018年3月30日
     * @author jiangyiwang
     */
    void start();

    /**
     * 关闭扫描器释放资源
     *
     * @date 2018年3月30日
     * @author jiangyiwang
     */
    void shutdown();

}
