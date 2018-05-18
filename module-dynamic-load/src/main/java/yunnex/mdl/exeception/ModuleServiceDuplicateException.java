package yunnex.mdl.exeception;

import org.apache.commons.lang3.StringUtils;

import yunnex.mdl.Module;

/**
 * 业务接口有多个模块实现
 * 
 * @author jingyiwang
 * @date 2018年3月29日
 */
public class ModuleServiceDuplicateException extends ModuleRuntimeException {

    private static final long serialVersionUID = 1L;

    public ModuleServiceDuplicateException(Class<?> serviceClazz, Module md1, Module md2) {
        super(StringUtils.join(serviceClazz.getName(), " exist ", md1.getName(), "-", md1.getVersion(), " and ", md2.getName(), "-", md2.getVersion(),
                        " implement.Please check it!"));
    }

}
