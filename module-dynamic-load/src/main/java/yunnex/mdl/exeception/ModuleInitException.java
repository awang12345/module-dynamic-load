package yunnex.mdl.exeception;

import yunnex.mdl.Module;

/**
 * 模块初始化异常
 * 
 * @author jingyiwang
 * @date 2018年3月26日
 */
public class ModuleInitException extends ModuleRuntimeException {

    private static final long serialVersionUID = 3064537444969202877L;

    public ModuleInitException(Module module, String message, Throwable cause) {
        super(module, message, cause);
    }

    public ModuleInitException(Module module, String message) {
        super(module, message);
    }

    public ModuleInitException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleInitException(String message) {
        super(message);
    }

}
