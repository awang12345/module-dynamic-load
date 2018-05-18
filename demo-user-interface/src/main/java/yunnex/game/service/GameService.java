package yunnex.game.service;

import yunnex.game.bean.Game;

public interface GameService {
    
    public void saveGame(Game game);
    
    public Game getGame(String gameName);
    
}
