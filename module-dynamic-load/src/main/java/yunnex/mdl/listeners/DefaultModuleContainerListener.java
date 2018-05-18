package yunnex.mdl.listeners;

import java.io.File;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

import yunnex.mdl.Module;
import yunnex.mdl.ModuleContainer;
import yunnex.mdl.ModuleContainerListener;
import yunnex.mdl.ModuleManager;

/**
 * 模块容器启动完成后打印banner以及已经加载的版本号
 * 
 * @author jingyiwang
 * @date 2018年4月2日
 */
public class DefaultModuleContainerListener implements ModuleContainerListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultModuleContainerListener.class);

    private String bannerFilePath = "META-INF/banner.txt";

    private int columnCharLength = 20;

    @Override
    public void containerStart(ModuleContainer container) {
        StringBuilder sb = new StringBuilder("\r\n");
        putBanner(sb);
        putLine(sb);
        putTile(sb);
        putLine(sb);
        putModules(sb, container);
        LOGGER.info(sb.toString());
    }

    private void putTile(StringBuilder sb) {
        putContext(sb, "Module-Name");
        putContext(sb, "Module-Version");
        putContext(sb, "Module-State");
        sb.append("‖\r\n");
    }

    private void putModules(StringBuilder sb, ModuleContainer container) {
        ModuleManager moduleManager = container.getContext().getMouleManager();
        List<Module> modules = moduleManager.getModules();
        if (CollectionUtils.isNotEmpty(modules)) {
            for (Module module : modules) {
                putContext(sb, module.getName());
                putContext(sb, module.getVersion());
                putContext(sb, module.getModuleState().name());
                sb.append("‖\r\n");
                putLine(sb);
            }
        }
    }

    private void putContext(StringBuilder sb, String context) {
        int contextLen = context.length();
        try {
            contextLen = context.getBytes("utf-8").length;
        } catch (Exception ex) {}
        sb.append("‖").append(context);
        int diffLen = columnCharLength - contextLen;
        for (int i = 0; i < diffLen; i++) {
            sb.append(" ");
        }
    }

    private void putLine(StringBuilder sb) {
        for (int i = 0; i < columnCharLength * 3; i++) {
            sb.append("=");
        }
        sb.append("\r\n");
    }

    private void putBanner(StringBuilder sb) {
        try {
            URL bannerURL = Thread.currentThread().getContextClassLoader().getResource(bannerFilePath);
            if (bannerURL != null) {
                sb.append(Files.toString(new File(bannerURL.getFile()), Charset.forName("UTF-8")));
            }
        } catch (Exception ex) {
            LOGGER.info("Get banner error;path={};error={}", bannerFilePath, ex.getMessage());
        }
    }

    @Override
    public void containerDestory(ModuleContainer moduleContainer) {

    }

}
