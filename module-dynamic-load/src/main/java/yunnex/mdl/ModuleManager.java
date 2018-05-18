package yunnex.mdl;

import java.util.List;
import java.util.Map;

import yunnex.mdl.exeception.ModuleInstallExecption;

/**
 * 模块管理器，提供安装、卸载、停止、重启、销毁能力、查找
 * 
 * @author jingyiwang
 * @date 2018年3月29日
 */
public interface ModuleManager {

    /**
     * 安装模块，安装之后才能被使用,会有以下情况出现:<br/>
     * 1、如果已存在相同的模块，并且将要安装的模块版本与已存在版本相同，则不进行安装<br/>
     * 2、如果已存在相同的模块，并且将要安装的模块版本比已存在版本要新，则进行替换，将原来的模块进行destory，然后新的模块载入代替<br/>
     * 3、如果已存在相同的模块，并且将要安装的模块版本比已存在版本要旧，非强制更新的情况下，该版本模块不进行安装；反之，如果强制更新，则进行载入替换<br/>
     * <b>注意<b/>：如果替换失败了，则恢复原来版本
     * 
     * @param moduleConfig
     * @throws ModuleInstallExecption
     * @date 2018年3月29日
     * @author jiangyiwang
     */
    boolean installModule(ModuleConfig moduleConfig);

    /**
     * 暂停模块服务提供，被暂停的模块只是不能提供服务，但是未销毁还可重新启用;如需启用，则需要调用restartModule方法
     *
     * @param moduleName
     * @return
     * @date 2018年3月29日
     * @author jiangyiwang
     */
    boolean stopModule(String moduleName);

    /**
     * 启用暂停状态的模块
     *
     * @param moduleName
     * @return
     * @date 2018年3月29日
     * @author jiangyiwang
     */
    boolean restartModule(String moduleName);

    /**
     * 卸载模块,对应的模块将进行销毁操作
     *
     * @param moduleName
     * @date 2018年3月29日
     * @author jiangyiwang
     */
    boolean uninstallModule(String moduleName);

    /**
     * 获取所有已安装的模块
     *
     * @return 没有就返回NULL
     * @date 2018年3月29日
     * @author jiangyiwang
     */
    List<Module> getModules();

    /**
     * 根据模块名称获取已装载模块
     *
     * @param moduleName
     * @return 没有返回NULL
     * @date 2018年3月29日
     * @author jiangyiwang
     */
    Module findModule(String moduleName);

    /**
     * 获取模块安装异常Map
     *
     * @return
     * @date 2018年3月29日
     * @author jiangyiwang
     */
    Map<ModuleConfig, String> getModuleInstallErrorMap();

    /**
     * 销毁操作
     *
     * @date 2018年3月29日
     * @author jiangyiwang
     */
    void destory();

}
