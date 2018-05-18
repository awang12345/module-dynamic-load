package yunnex.mdl.support;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yunnex.mdl.Module;
import yunnex.mdl.ModuleContext;
import yunnex.mdl.ModuleServiceImplementManager;
import yunnex.mdl.ModuleStateChangeListener;
import yunnex.mdl.bean.ModuleServiceHolder;
import yunnex.mdl.constans.ModuleState;
import yunnex.mdl.exeception.ModuleRuntimeException;
import yunnex.mdl.exeception.ModuleServiceDuplicateException;

/**
 * 默认模块业务管理器，使用的时候必须要要设置Context
 * 
 * @author jingyiwang
 * @date 2018年3月29日
 */
public class DefaultModuleServiceImplementManager implements ModuleServiceImplementManager, ModuleStateChangeListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultModuleServiceImplementManager.class);

    /** 服务缓存 */
    private Map<Class<?>, ModuleServiceHolder> serviceCache = new ConcurrentHashMap<>();

    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final Lock writeLock = rwLock.writeLock();

    @Override
    public ModuleServiceHolder getService(Class<?> serviceClass) {
        LOGGER.debug("Get module service implement by class:{}", serviceClass);
        if (serviceClass != null && serviceClass.isInterface()) {
            try {
                readLock.lockInterruptibly();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
            try {
                return serviceCache.get(serviceClass);
            } finally {
                readLock.unlock();
            }
        }
        return null;
    }

    @Override
    public synchronized void refresh() {
        ModuleContext moduleContext = SystemConfig.getModuleContext();
        if (moduleContext == null || moduleContext.getMouleManager() == null) {
            throw new ModuleRuntimeException("DefaultModuleServiceManager must set moduleContext and the moduleContext exist mouleManager!");
        }
        LOGGER.info("Start refresh service of module.The current service count is {}", serviceCache.size());
        // 这里使用一个新的集合进行加载，为了减少锁的竞争
        ConcurrentHashMap<Class<?>, ModuleServiceHolder> serviceUpdate = new ConcurrentHashMap<>();
        List<Module> allModules = moduleContext.getMouleManager().getModules();
        if (CollectionUtils.isNotEmpty(allModules)) {
            for (Module module : allModules) {
                // 可用的状态才需要进行put
                if (ModuleState.USEABLE == module.getModuleState()) {
                    putService(serviceUpdate, module);
                }
            }
        }
        LOGGER.info("End refresh service of module.The new service count is {}", serviceUpdate.size());
        // 执行替换操作
        try {
            writeLock.lockInterruptibly();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        try {
            Map<Class<?>, ModuleServiceHolder> orgCache = this.serviceCache;
            this.serviceCache = serviceUpdate;// 将已加载的最新的服务列表进行替换
            orgCache.clear();// clear org service cache
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * 将模块业务put到集合中
     *
     * @param services
     * @param module
     * @date 2018年3月29日
     * @author jiangyiwang
     */
    private void putService(ConcurrentHashMap<Class<?>, ModuleServiceHolder> services, Module module) {
        if (module == null || MapUtils.isEmpty(module.getServices())) {
            return;
        }
        Set<Entry<Class<?>, Object>> entSet = module.getServices().entrySet();
        ModuleServiceHolder oldHold = null;
        for (Entry<Class<?>, Object> ent : entSet) {
            oldHold = services.putIfAbsent(ent.getKey(), new ModuleServiceHolder(ent.getValue(), module));
            if (oldHold != null) {
                // 如果存在多个，则需要报错
                throw new ModuleServiceDuplicateException(ent.getKey(), module, oldHold.getModule());
            }
        }
    }

    @Override
    public void destory() {
        serviceCache.clear();
        serviceCache = null;
    }

    @Override
    public void onChange(Module module) {
        // 当模块发生改变时，需要重新刷新
        refresh();
    }

}
