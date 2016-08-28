package me.hopps.ancient.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import me.hopps.ancient.states.GameState;

import java.util.ArrayList;

public class UiRenderer {

    GameState state;

    ShapeRenderer shapeRenderer;
    SpriteBatch batch;

    public ArrayList<Button> buttons;

    public UiRenderer(GameState state) {
        this.state = state;

        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        buttons = new ArrayList<Button>();

        buttons.add(new Button(state, 25, 10, state.game.assets.get("menu/buybunker.png", Texture.class)));
        buttons.add(new Button(state, 25 + 10 + 64, 10, state.game.assets.get("menu/buyrockettower.png", Texture.class)));
    }

    public void render() {
        /**
         * Render Health
         */
        if(state.objects.size() > 0) {
            float health = state.objects.get(0).health;
            if (health > 75) {
                shapeRenderer.setColor(Color.GREEN);
            } else if (health > 50) {
                shapeRenderer.setColor(Color.YELLOW);
            } else if (health > 25) {
                shapeRenderer.setColor(Color.ORANGE);
            } else {
                shapeRenderer.setColor(Color.RED);
            }

            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.rect(20, Gdx.graphics.getHeight() - 40, 984 * (health / 100f), 30);
            shapeRenderer.end();
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.rect(20, Gdx.graphics.getHeight() - 40, 984, 30);
            shapeRenderer.end();
        }

        /**
         * Render Menu
         */
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0,0,0,0.75f);
        shapeRenderer.rect(0,0,1024,80);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();
        for(Button b : buttons) {
            b.render(batch);
        }
        batch.end();

        /**
         * Render Selection, if any
         */
    }
}
