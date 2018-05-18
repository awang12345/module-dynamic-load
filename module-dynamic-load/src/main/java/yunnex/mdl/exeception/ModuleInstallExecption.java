package yunnex.mdl.exeception;

import org.apache.commons.lang3.StringUtils;

import yunnex.mdl.ModuleConfig;

/**
 * 模块装载异常
 * 
 * @author jingyiwang
 * @date 2018年3月29日
 */
public class ModuleInstallExecption extends Exception {

    private static final long serialVersionUID = 5851286498388173130L;

    public ModuleInstallExecption(String arg0, Throwable arg1) {
        super(arg0, arg1);
    }

    public ModuleInstallExecption(String arg0) {
        super(arg0);
    }

    public ModuleInstallExecption(ModuleConfig config, String arg0, Throwable arg1) {
        super(formatMsg(config, arg0), arg1);
    }

    public ModuleInstallExecption(ModuleConfig config, String arg0) {
        super(formatMsg(config, arg0));
    }

    private static String formatMsg(ModuleConfig config, String msg) {
        if (config == null) {
            return msg;
        }
        return StringUtils.join("config:", config.toString(), "|errorMsg:", msg);
    }

}
