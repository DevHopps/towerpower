package me.hopps.ancient.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Tile {

    TextureRegion text;
    int x, y;

    public Tile(TextureRegion text, int x, int y) {
        this.text = text;
        this.x = x;
        this.y = y;
    }

    public void render(SpriteBatch batch) {
        batch.draw(text, x * 16, y * 16);
    }


}
