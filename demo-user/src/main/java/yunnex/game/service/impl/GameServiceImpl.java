package yunnex.game.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import yunnex.game.bean.Game;
import yunnex.game.service.GameService;
import yunnex.mdl.annotations.ModuleService;

@ModuleService
public class GameServiceImpl implements GameService {

    private static final Logger log = LoggerFactory.getLogger(GameServiceImpl.class);

    public void saveGame(Game game) {
        log.info("测试更新啦啦啦啦啦绿绿绿:{}", game.getName());
    }

    public Game getGame(String gameName) {
        return new Game(String.valueOf(System.currentTimeMillis()));
    }

}
