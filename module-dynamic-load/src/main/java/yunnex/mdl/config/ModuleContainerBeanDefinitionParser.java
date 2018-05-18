package yunnex.mdl.config;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

import yunnex.mdl.support.SpringModuleContainer;

public class ModuleContainerBeanDefinitionParser implements BeanDefinitionParser {

    private static final String MODULE_JAR_LOCATION = "module-jar-location";

    private static final String MODULE_INTERFACE_PACKAGE = "module-interface-package";

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        BeanDefinitionRegistry beanDefinitionRegistry = parserContext.getRegistry();
        BeanDefinition beanDefinition = new RootBeanDefinition(SpringModuleContainer.class);
        beanDefinition.getPropertyValues().add("moduleJarDirPath", element.getAttribute(MODULE_JAR_LOCATION));
        beanDefinition.getPropertyValues().add("moduleServiceInterfacePackage", element.getAttribute(MODULE_INTERFACE_PACKAGE));
        beanDefinition.setScope(BeanDefinition.SCOPE_SINGLETON);
        beanDefinitionRegistry.registerBeanDefinition(SpringModuleContainer.class.getName(), beanDefinition);
        return beanDefinition;
    }

}
