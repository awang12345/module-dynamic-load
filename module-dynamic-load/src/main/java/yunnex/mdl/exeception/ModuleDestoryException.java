package yunnex.mdl.exeception;

import yunnex.mdl.Module;

/**
 * 模块销毁异常
 * 
 * @author jingyiwang
 * @date 2018年3月26日
 */
public class ModuleDestoryException extends ModuleRuntimeException {

    private static final long serialVersionUID = 3064537444969202877L;

    public ModuleDestoryException(Module module, String message, Throwable cause) {
        super(module, message, cause);
    }

    public ModuleDestoryException(Module module, String message) {
        super(module, message);
    }

    public ModuleDestoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleDestoryException(String message) {
        super(message);
    }

}
