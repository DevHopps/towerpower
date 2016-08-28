package me.hopps.ancient.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

import java.util.LinkedList;

public class Enemy implements Pool.Poolable {

    public int health;
    public int size;
    public int money;
    public float x;
    public float y;
    public float speed;
    public Color color;

    public Enemy() {

    }

    public void init(int health, int size, int money, float x, float y, float speed, Color color) {
        this.health = health;
        this.size = size;
        this.money = money;
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.color = color;
    }

    public void moveTowardsClosestBuilding(LinkedList<GameObject> objects, EffectRenderer effects, float delta) {
        if(health > 0) {
            Vector2 closest = null;
            float dist = 0;
            GameObject o = null;
            for(GameObject obj : objects) {
                Vector2 distance = new Vector2(obj.x + obj.size/2 - this.x, obj.y + obj.size/2 - this.y);
                float d = distance.len();

                if(closest == null || d < dist) {
                    closest = distance;
                    dist = d;
                    o = obj;
                }
            }

            if(objects.size() > 0) {
                Vector2 vec = closest;
                vec.nor();
                this.x += (speed * vec.x * delta);
                this.y += (speed * vec.y * delta);

                if (dist < 10f) {
                    float temp = o.health;
                    o.health -= this.health;
                    health -= (int) temp;

                    if(o.health > 0)
                        effects.damagedBuilding(o);
                }
            }
        }
    }

    public void draw(ShapeRenderer renderer) {
        if(health > 0) {
            renderer.set(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(color);
            renderer.rect(x - size / 2, y - size / 2, size, size);
        }
    }

    @Override
    public void reset() {
        health = 0;
        size = 0;
        x = -100;
        y = -100;
        speed = 0;
        color = Color.WHITE;
    }
}
