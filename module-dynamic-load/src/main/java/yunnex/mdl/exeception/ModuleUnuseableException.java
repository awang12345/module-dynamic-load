package yunnex.mdl.exeception;

import yunnex.mdl.Module;

/**
 * 模块被停止后，继续调用模块中的服务将抛出该异常
 * 
 * @author jingyiwang
 * @date 2018年3月26日
 */
public class ModuleUnuseableException extends ModuleRuntimeException {

    private static final long serialVersionUID = 3064537444969202877L;

    public ModuleUnuseableException(Module module, String message, Throwable cause) {
        super(module, message, cause);
    }

    public ModuleUnuseableException(Module module, String message) {
        super(module, message);
    }

    public ModuleUnuseableException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleUnuseableException(String message) {
        super(message);
    }

}
