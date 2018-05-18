package yunnex.mdl;

/**
 * 模块资源
 * 
 * @author jingyiwang
 * @date 2018年3月27日
 */
public interface ModuleResource {

    /**
     * 获取模块资源配置信息 <br/>
     * 必填属性：name、version、moduleUrl
     *
     * @return 如果资源不存在，则返回NULL
     * @date 2018年3月27日
     * @author jiangyiwang
     */
    ModuleConfig getResuorce();

}
