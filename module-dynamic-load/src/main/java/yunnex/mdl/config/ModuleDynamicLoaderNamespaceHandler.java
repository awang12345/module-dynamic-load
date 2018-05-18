package yunnex.mdl.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class ModuleDynamicLoaderNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser("container", new ModuleContainerBeanDefinitionParser());
    }

}
