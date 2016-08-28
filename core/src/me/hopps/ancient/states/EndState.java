package me.hopps.ancient.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.hopps.ancient.AncientGame;

public class EndState extends State {

    SpriteBatch batch;
    boolean win, replay;

    public EndState(AncientGame game, boolean win) {
        super(game);
        batch = game.batch;
        this.win = win;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        if(Gdx.input.isKeyPressed(Input.Keys.R)) {
            replay = true;
            game.setScreen(new GameState(game));
            return;
        }

        batch.begin();
        if(win) {
            batch.draw(game.assets.get("gamewin.png", Texture.class), 0 , 0);
        } else {
            batch.draw(game.assets.get("gameover.png", Texture.class), 0 , 0);
        }
        batch.end();
    }

    @Override
    public void show() {

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
    }
}
