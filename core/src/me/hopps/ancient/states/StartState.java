package me.hopps.ancient.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.hopps.ancient.AncientGame;

/**
 * Show an explanation, and if clicked Space(?) go to GameState
 */
public class StartState extends State {

    SpriteBatch batch;

    public StartState(AncientGame game) {
        super(game);
        batch = game.batch;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            game.setScreen(new GameState(game));
            return;
        }
        batch.begin();
        batch.draw(game.assets.get("start.png", Texture.class), 0, 0);
        batch.end();
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
