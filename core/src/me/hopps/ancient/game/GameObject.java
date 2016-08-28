package me.hopps.ancient.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class GameObject {

    public String name;
    int x, y, size, damage, distance, hitRadius = 100;
    public int initialHealth, health;
    Rectangle rect;
    Texture text;
    boolean boom = false, zap = false;
    float lastShot, shootingspeed;
    Array<Vector2> zapFrom = new Array<>(), zapTarget= new Array<>();
    Array<Integer> zapFrames = new Array<>();

    public GameObject(String name, int x, int y, int size, int health, int damage, float shootingspeed, int distance, boolean boom, boolean zap,Texture text) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.size = size;
        this.health = health;
        this.initialHealth = health;
        this.damage = damage;
        this.shootingspeed = shootingspeed;
        this.boom = boom;
        this.distance = distance;
        this.text = text;

        rect = new Rectangle(x, y, size, size);
    }

    public void render(SpriteBatch batch) {
        batch.draw(text, x, y);
    }

    public void shootAtClosestEnemies(Array<Enemy> activeEnemies, EffectRenderer effectRenderer, float delta) {
        lastShot += delta;
        int dam = this.damage;
        if(damage > 0 && lastShot > shootingspeed) {
            while(dam > 0) {
                Vector2 closest = null;
                float dist = 0;
                Enemy target = null;
                for (Enemy e : activeEnemies) {
                    Vector2 distance = new Vector2(e.x + e.size / 2 - this.x, e.y + e.size / 2 - this.y);
                    float d = distance.len();

                    if (closest == null || d < dist) {
                        closest = distance;
                        dist = d;
                        target = e;
                    }
                }
                if(zap) {

                } else {
                    if (boom) {//Shoot Rocket at closest enemy, dealing the same damage to all Units in area
                        if (target != null && dist <= distance) {
                            effectRenderer.rocketHit(target);

                            Vector2 center = new Vector2(target.x + target.size / 2, target.y + target.size / 2);
                            this.zap(this.x + this.size / 2, this.y + this.size / 2, target.x + target.size / 2, target.y + target.size / 2, 60);
                            for (Enemy e : activeEnemies) {
                                Vector2 distance = new Vector2(e.x + e.size / 2 - center.x, e.y + e.size / 2 - center.y);
                                float d = distance.len();

                                if (d <= hitRadius) {
                                    e.health -= this.damage;
                                    effectRenderer.shotFired(e);
                                }
                            }
                            lastShot = 0;
                        }
                        dam = 0;
                    } else {
                        if (target != null && dist <= distance) {
                            int temp = target.health;
                            target.health -= dam;
                            if (target.health > temp)
                                target.health = 0;
                            dam -= temp;
                            lastShot = 0;
                            this.zap(this.x + this.size / 2, this.y + this.size / 2, target.x + target.size / 2, target.y + target.size / 2, 30);

                            effectRenderer.shotFired(target);
                        } else {
                            dam = 0;
                        }
                    }
                }
            }
        }
    }

    private void zap(int x, int y, float x2, float y2, int frames) {
        zapFrames.add(frames);
        zapFrom.add(new Vector2(x, y));
        zapTarget.add(new Vector2(x2, y2));
    }

    public void renderDebug(ShapeRenderer shapeRenderer) {
        if(health < initialHealth && health > 0) {
            shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.YELLOW);
            shapeRenderer.rect(x, y + size + 5, size * ((float) health / (float) initialHealth), 5);
            shapeRenderer.set(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.BLACK);
            shapeRenderer.rect(x, y + size + 5, size, 5);
        }
        if(health > 0) {
            shapeRenderer.set(ShapeRenderer.ShapeType.Line);
            for (int i = 0; i < zapFrames.size; i++) {
                if(zapFrames.get(i) > 0) {
                    shapeRenderer.setColor(0f,0f,0f, 1f - 1f * (1f/(float) zapFrames.get(i)));
                    shapeRenderer.line(zapFrom.get(i), zapTarget.get(i));
                    zapFrames.set(i, zapFrames.get(i) - 1);
                }
            }

            for (int i = 0; i < zapFrames.size; i++) {
                if(zapFrames.get(i) <= 0) {
                    zapFrames.removeIndex(i);
                    zapFrom.removeIndex(i);
                    zapTarget.removeIndex(i);
                }
            }
        }
    }

    public boolean overlaps(GameObject obj) {
        return this.rect.overlaps(obj.rect);
    }
}
