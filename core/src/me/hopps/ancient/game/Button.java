package me.hopps.ancient.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import me.hopps.ancient.states.GameState;

public class Button {

    GameState state;
    Texture texture;
    Rectangle rect;
    int x, y;

    public Button(GameState state, int x, int y, Texture texture) {
        this.state = state;
        this.x = x;
        this.y = y;
        this.texture = texture;

        rect = new Rectangle(x, y, texture.getWidth(), texture.getHeight());
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    public boolean isSelected(int x, int y) {
        return rect.contains(x, y);
    }
}
