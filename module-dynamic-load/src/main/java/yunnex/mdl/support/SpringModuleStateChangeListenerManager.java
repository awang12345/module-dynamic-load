package yunnex.mdl.support;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import yunnex.mdl.Module;
import yunnex.mdl.ModuleStateChangeListener;
import yunnex.mdl.ModuleStateChangeListenerManger;

/**
 * 默认模块状态变更监听器
 * 
 * @author jingyiwang
 * @date 2018年3月28日
 */
public class SpringModuleStateChangeListenerManager extends SpringInit implements ModuleStateChangeListenerManger {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringModuleStateChangeListenerManager.class);

    // 监听器集合
    private List<ModuleStateChangeListener> listeners = Lists.newCopyOnWriteArrayList();

    @Override
    public boolean registerListener(ModuleStateChangeListener listener) {
        if (listener != null) {
            return listeners.add(listener);
        }
        return false;
    }

    @Override
    public boolean unregisterListenter(ModuleStateChangeListener listener) {
        if (listener != null) {
            return listeners.remove(listener);
        }
        return false;
    }

    @Override
    public List<ModuleStateChangeListener> getListenters() {
        return ImmutableList.copyOf(listeners);
    }

    @Override
    public void onFireModuleStateChange(Module module) {
        if (!listeners.isEmpty()) {
            for (ModuleStateChangeListener listener : listeners) {
                listener.onChange(module);
            }
        }
    }

    @Override
    public void destory() {
        LOGGER.info("Destory moduleStateChangeListenerManager.");
        listeners.clear();
        listeners = null;
    }

    @Override
    protected void afterInit(ApplicationContext applicationContext) {
        for (ModuleStateChangeListener listener : applicationContext.getBeansOfType(ModuleStateChangeListener.class).values()) {
            registerListener(listener);
        }
    }

}
