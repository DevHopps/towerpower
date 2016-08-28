package me.hopps.ancient.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import me.hopps.ancient.AncientGame;

/**
 * Load Everything and then go to StartState
 */
public class LoadingState extends State {

    Texture text;
    SpriteBatch batch;

    public LoadingState(AncientGame game) {
        super(game);
        batch = game.batch;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        if(game.assets.update()) {
            game.setScreen(new StartState(game));
        }

        if(game.assets.isLoaded("loading.png")) {
            text = game.assets.get("loading.png", Texture.class);
        }

        if(text != null) {
            batch.begin();
            batch.draw(text, 0, 0);
            batch.end();
        }
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
