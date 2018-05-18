package yunnex.mdl.exeception;

import yunnex.mdl.Module;

/**
 * 模块业务实现未找到异常
 * 
 * @author jingyiwang
 * @date 2018年3月26日
 */
public class ModuleServiceImplementNotFoundException extends ModuleRuntimeException {

    private static final long serialVersionUID = 3064537444969202877L;

    public ModuleServiceImplementNotFoundException(Module module, String message, Throwable cause) {
        super(module, message, cause);
    }

    public ModuleServiceImplementNotFoundException(Module module, String message) {
        super(module, message);
    }

    public ModuleServiceImplementNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleServiceImplementNotFoundException(String message) {
        super(message);
    }

}
