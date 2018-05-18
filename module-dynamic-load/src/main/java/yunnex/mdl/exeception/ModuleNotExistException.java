package yunnex.mdl.exeception;

import yunnex.mdl.Module;

/**
 * 模块资源不存在异常
 * 
 * @author jingyiwang
 * @date 2018年3月26日
 */
public class ModuleNotExistException extends ModuleRuntimeException {

    private static final long serialVersionUID = 3064537444969202877L;

    public ModuleNotExistException(Module module, String message, Throwable cause) {
        super(module, message, cause);
    }

    public ModuleNotExistException(Module module, String message) {
        super(module, message);
    }

    public ModuleNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public ModuleNotExistException(String message) {
        super(message);
    }

}
