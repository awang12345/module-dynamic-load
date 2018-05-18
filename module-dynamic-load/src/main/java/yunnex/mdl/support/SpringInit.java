package yunnex.mdl.support;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * 用于利用spring初始化时从applicationContext获取一些其他信息
 * 
 * @author jingyiwang
 * @date 2018年4月3日
 */
public abstract class SpringInit implements ApplicationContextAware, InitializingBean {

    protected ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        afterInit(applicationContext);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    protected abstract void afterInit(ApplicationContext applicationContext);

    protected <T> T getBean(Class<T> beanClass, T defaultValue) {
        T result = applicationContext.getBean(beanClass);
        if (result == null) {
            return defaultValue;
        }
        return result;
    }

}
