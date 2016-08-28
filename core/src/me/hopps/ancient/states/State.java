package me.hopps.ancient.states;

import com.badlogic.gdx.Screen;
import me.hopps.ancient.AncientGame;

public abstract class State implements Screen {

    public AncientGame game;

    public State(AncientGame game) {
        this.game = game;
    }
}
