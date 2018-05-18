package yunnex.mdl.support;

import java.io.File;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import yunnex.mdl.ModuleResource;
import yunnex.mdl.ModuleScanner;
import yunnex.mdl.exeception.ModuleRuntimeException;
import yunnex.mdl.resource.ModuleJarResource;
import yunnex.mdl.utils.ModuleUtils;

/**
 * 模块扫描器
 * 
 * @author jingyiwang
 * @date 2018年3月30日
 */
public class DefaultModuleScanner extends AbstractModuleRefresh implements FileListener, ModuleScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultModuleScanner.class);
    // 扫描是否已经启动
    private final AtomicBoolean started = new AtomicBoolean(false);
    // 是否存在刷新任务
    private final AtomicBoolean isExistRefreshTask = new AtomicBoolean(false);
    // 每次只有一个刷新任务
    private final Semaphore refreshSemaphore = new Semaphore(1);
    // 模块jar包所在文件夹地址
    private File moduleDir;
    // 文件监视器
    private DefaultFileMonitor fileMonitor;
    // 延时模块刷新任务,默认3秒钟之后进行刷新
    private final DelayRefreshTask refreshTask = new DelayRefreshTask(3000);

    /**
     * 构造函数
     * 
     * @param moduleJarDirPath 模块jar文件夹路径
     */
    public DefaultModuleScanner(String moduleJarDirPath) {
        super();
        this.moduleDir = checkModuleDirPath(moduleJarDirPath);
        initFileMonitor();
        refreshTask.start();// 启动刷新任务
    }

    /**
     * 初始化文件监控器
     *
     * @date 2018年3月30日
     * @author jiangyiwang
     */
    private void initFileMonitor() {
        try {
            // 需要注意一下，因为被监控的文件夹里面的文件只要有变动都会触发，每个文件都会触发一次，这就导致了多个文件同时变更的情况下导致多次触发，
            // 所以如果有变更需要进行延迟刷新，不应该有事件就进行触发一次
            FileSystemManager fsManager = VFS.getManager();
            FileObject listendir = fsManager.resolveFile(moduleDir.getAbsolutePath());
            fileMonitor = new DefaultFileMonitor(this);
            fileMonitor.setRecursive(true);
            fileMonitor.addFile(listendir);
        } catch (FileSystemException e) {
            throw new ModuleRuntimeException("Module scanner init error!ModulePath=" + moduleDir.getAbsolutePath(), e);
        }
    }

    /**
     * 启动扫描器
     *
     * @date 2018年3月30日
     * @author jiangyiwang
     */
    @Override
    public void start() {
        if (started.compareAndSet(false, true)) {
            LOGGER.info("Start module scanner by path:{}", moduleDir.getAbsolutePath());
            try {
                refreshModules();// 刷新一次
                fileMonitor.start();
            } catch (Exception e) {
                throw new ModuleRuntimeException("Module scanner start error!Path=" + moduleDir.getAbsolutePath(), e);
            }
            LOGGER.info("Success start module scanner by path:{}", moduleDir.getAbsolutePath());
        }
    }

    @Override
    public void shutdown() {
        if (started.compareAndSet(true, false) && fileMonitor != null) {
            LOGGER.info("Shutdown module scanner path={}", moduleDir.getAbsolutePath());
            try {
                fileMonitor.stop();
            } catch (Exception e) {
                LOGGER.warn("Shutdown module scanner error!", e);
            }
            // 终止刷新任务
            refreshTask.interrupt();
        }
    }

    @Override
    public void fileCreated(FileChangeEvent event) throws Exception {
        onfireRefresh();
    }

    @Override
    public void fileDeleted(FileChangeEvent event) throws Exception {
        onfireRefresh();
    }

    @Override
    public void fileChanged(FileChangeEvent event) throws Exception {
        onfireRefresh();
    }

    /**
     * 触发刷新动作
     *
     * @date 2018年3月30日
     * @author jiangyiwang
     */
    private void onfireRefresh() {
        if (isExistRefreshTask.compareAndSet(false, true)) {
            // 不存在刷新任务的情况下进行刷新动作
            refreshSemaphore.release();
        }
    }

    @Override
    protected List<ModuleResource> queryModuleResources() {
        File[] jarFiles = moduleDir.listFiles();
        if (jarFiles == null || jarFiles.length == 0) {
            return Lists.newArrayList();
        }
        List<ModuleResource> moduleResources = Lists.newLinkedList();
        for (File jarFile : jarFiles) {
            if (ModuleUtils.isJarFile(jarFile)) {
                CollectionUtils.addIgnoreNull(moduleResources, new ModuleJarResource(jarFile.getAbsolutePath()));
            }
        }
        return moduleResources;
    }

    /**
     * 检测文件夹路径是否存在
     *
     * @param moduleDirPath
     * @date 2018年3月28日
     * @author jiangyiwang
     */
    private File checkModuleDirPath(String moduleDirPath) {
        if (StringUtils.isEmpty(moduleDirPath)) {
            throw new ModuleRuntimeException("ModuleDirPath is not empt!");
        }
        File file = new File(moduleDirPath);
        if (!(file.exists() && file.isDirectory())) {
            throw new ModuleRuntimeException("ModuleDirPath is not exists or directory!Path=" + moduleDirPath);
        }
        return file;
    }

    /**
     * 延时刷新任务,为了合并多个变更任务为一个,减少刷新次数
     * 
     * @author jingyiwang
     * @date 2018年3月30日
     */
    private class DelayRefreshTask extends Thread {

        // 延迟刷新时间
        private long refreshDelayTime;

        public DelayRefreshTask(long refreshDelayTime) {
            super();
            this.refreshDelayTime = refreshDelayTime;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    refreshSemaphore.acquire();
                    // 休眠一段时间再做刷新，防止多文件改动时瞬时多个任务同时触发
                    Thread.sleep(refreshDelayTime);
                    refreshModules();// 进行刷新操作
                } catch (InterruptedException e) {
                    LOGGER.info("Rehresh module error.", e);
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception ex) {
                    LOGGER.warn("Rehresh module error.", ex);
                } finally {
                    isExistRefreshTask.set(false);
                }
            }
        }
    }

}
