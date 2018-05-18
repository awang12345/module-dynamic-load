package yunnex.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import yunnex.game.bean.Game;
import yunnex.game.service.GameService;
import yunnex.mdl.support.SystemConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/spring.xml"})
public class Test1 {

    private static final Logger LOG = LoggerFactory.getLogger(Test1.class);

    @Autowired
    private GameService gameService;

    @SuppressWarnings("resource")
    @Test
    public void test() {
        while (true) {
            try {
                Thread.sleep(3000);
                gameService.saveGame(new Game("111111"));
            } catch (Exception e) {
                LOG.info("调用出错！{}", e.getMessage());
            }
        }
    }

}
