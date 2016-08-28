package me.hopps.ancient.states;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import me.hopps.ancient.AncientGame;
import me.hopps.ancient.game.*;
import me.hopps.ancient.stuff.OpenSimplexNoise;

import java.util.LinkedList;
import java.util.Random;

/**
 * Game stuff
 */
public class GameState extends State implements InputProcessor{

    Tile[][] tiles;
    int mapSize = 64;
    TextureRegion[][] textures;
    float time = 0f;
    float lastRepairTick = 0f;
    float lastSmallSpawn = 0f;
    float lastMediumSpawn = 0f;
    float lastLargeSpawn = 0f;
    Random rand;
    public LinkedList<GameObject> objects = new LinkedList<>();

    private final Array<Enemy> activeEnemies = new Array<>();
    private final Pool<Enemy> enemyPool = new Pool<Enemy>() {
        @Override
        protected Enemy newObject() {
            return new Enemy();
        }
    };
    public int money = 500;

    boolean buildBunker, buildRocket, buildZapper;

    public UiRenderer uiRenderer;

    public EffectRenderer effectRenderer;

    ShapeRenderer shapeRenderer;
    SpriteBatch batch;

    public GameState(AncientGame game) {
        super(game);
        batch = game.batch;
        shapeRenderer = game.shapeRenderer;

        uiRenderer = new UiRenderer(this);
        effectRenderer = new EffectRenderer(game);

        tiles = new Tile[mapSize][mapSize];

        rand = new Random();
        OpenSimplexNoise noise = new OpenSimplexNoise(rand.nextLong());

        TextureRegion region = new TextureRegion(game.assets.get("tiles.png", Texture.class));
        textures = region.split(16,16);

        // Generate the backgound tiles.
        for(int i = 0; i < mapSize; i++) {
            for(int k = 0; k < mapSize; k++) {
                double n = noise.eval(i, k);
                if(n < -0.5d) {
                    tiles[i][k] = new Tile(textures[0][0], i, k);
                } else if(n > -0.5d && n < -0.25) {
                    tiles[i][k] = new Tile(textures[1][0], i, k);
                } else if(n > -0.25 && n < 0) {
                    tiles[i][k] = new Tile(textures[2][0], i, k);
                } else {
                    tiles[i][k] = new Tile(textures[3][0], i, k);
                }
            }
        }

        //Place broken ship
        objects.add(new GameObject("spaceship", 500, 350, 64, 35, 0, 5000f, 0,false, false, game.assets.get("spaceship.png", Texture.class)));
        objects.get(0).initialHealth = 100;
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if(objects.get(0).health >= 100 && objects.get(0).name == "spaceship") {
            game.setScreen(new EndState(game, true));
            game.assets.get("sound/winSound.wav", Sound.class).play(0.25f);
            return;
        }

        time += delta;
        /**
         * Enemy Spawning
         */
        if(time > 5f && time < 30f) { //TODO: First Wave
            if(lastSmallSpawn < 0) {
                Enemy enemy = enemyPool.obtain();
                if(rand.nextBoolean()) { //Up or Down
                    if(rand.nextBoolean()) {
                        enemy.init(2, 4, 4, rand.nextInt(1024), 0, 50f, Color.BROWN);
                    } else {
                        enemy.init(2, 4, 4, rand.nextInt(1024), 768, 50f, Color.BROWN);
                    }
                } else {
                    if(rand.nextBoolean()) { //left or right
                        enemy.init(2, 4, 4, 0, rand.nextInt(768), 50f, Color.BROWN);
                    } else {
                        enemy.init(2, 4, 4, 1024, rand.nextInt(768), 50f, Color.BROWN);
                    }
                }
                activeEnemies.add(enemy);
                lastSmallSpawn = 1f;
            }
            lastSmallSpawn -= delta;
        } else if(time > 30f && time < 60) { //TODO: 2. Wave
            if(lastSmallSpawn < 0) {
                Enemy enemy = enemyPool.obtain();
                if(rand.nextBoolean()) { //Up or Down
                    if(rand.nextBoolean()) {
                        enemy.init(2, 4, 8, rand.nextInt(1024), 0, 50f, Color.BROWN);
                    } else {
                        enemy.init(2, 4, 8, rand.nextInt(1024), 768, 50f, Color.BROWN);
                    }
                } else {
                    if(rand.nextBoolean()) { //left or right
                        enemy.init(2, 4, 8, 0, rand.nextInt(768), 50f, Color.BROWN);
                    } else {
                        enemy.init(2, 4, 8, 1024, rand.nextInt(768), 50f, Color.BROWN);
                    }
                }
                activeEnemies.add(enemy);
                lastSmallSpawn = 0.75f;
            }
            lastSmallSpawn -= delta;

            if(lastMediumSpawn < 0) {
                Enemy enemy = enemyPool.obtain();
                if(rand.nextBoolean()) { //Up or Down
                    if(rand.nextBoolean()) {
                        enemy.init(4, 8, 16, rand.nextInt(1024), 0, 30f, Color.CORAL);
                    } else {
                        enemy.init(4, 8, 16, rand.nextInt(1024), 768, 30f, Color.CORAL);
                    }
                } else {
                    if(rand.nextBoolean()) { //left or right
                        enemy.init(4, 8, 16, 0, rand.nextInt(768), 30f, Color.CORAL);
                    } else {
                        enemy.init(4, 8, 16, 1024, rand.nextInt(768), 30f, Color.CORAL);
                    }
                }
                activeEnemies.add(enemy);
                lastMediumSpawn = 2.5f;
            }
            lastMediumSpawn -= delta;
        } else if(time > 60f && time < 120) { //TODO: Third Wave
            if(lastSmallSpawn < 0) {
                Enemy enemy = enemyPool.obtain();
                if(rand.nextBoolean()) { //Up or Down
                    if(rand.nextBoolean()) {
                        enemy.init(2, 4, 12, rand.nextInt(1024), 0, 50f, Color.BROWN);
                    } else {
                        enemy.init(2, 4, 12, rand.nextInt(1024), 768, 50f, Color.BROWN);
                    }
                } else {
                    if(rand.nextBoolean()) { //left or right
                        enemy.init(2, 4, 12, 0, rand.nextInt(768), 50f, Color.BROWN);
                    } else {
                        enemy.init(2, 4, 12, 1024, rand.nextInt(768), 50f, Color.BROWN);
                    }
                }
                activeEnemies.add(enemy);
                lastSmallSpawn = 0.5f;
            }
            lastSmallSpawn -= delta;

            if(lastMediumSpawn < 0) {
                Enemy enemy = enemyPool.obtain();
                if(rand.nextBoolean()) { //Up or Down
                    if(rand.nextBoolean()) {
                        enemy.init(4, 8, 24, rand.nextInt(1024), 0, 30f, Color.CORAL);
                    } else {
                        enemy.init(4, 8, 24, rand.nextInt(1024), 768, 30f, Color.CORAL);
                    }
                } else {
                    if(rand.nextBoolean()) { //left or right
                        enemy.init(4, 8, 24, 0, rand.nextInt(768), 30f, Color.CORAL);
                    } else {
                        enemy.init(4, 8, 24, 1024, rand.nextInt(768), 30f, Color.CORAL);
                    }
                }
                activeEnemies.add(enemy);
                lastMediumSpawn = 1.75f;
            }
            lastMediumSpawn -= delta;
        } else if(time > 60f && time < 120) { //TODO: Fifth Wave
            if(lastSmallSpawn < 0) {
                Enemy enemy = enemyPool.obtain();
                if(rand.nextBoolean()) { //Up or Down
                    if(rand.nextBoolean()) {
                        enemy.init(2, 4, 16, rand.nextInt(1024), 0, 40f, Color.BROWN);
                    } else {
                        enemy.init(2, 4, 16, rand.nextInt(1024), 768, 40f, Color.BROWN);
                    }
                } else {
                    if(rand.nextBoolean()) { //left or right
                        enemy.init(2, 4, 16, 0, rand.nextInt(768), 40f, Color.BROWN);
                    } else {
                        enemy.init(2, 4, 16, 1024, rand.nextInt(768), 40f, Color.BROWN);
                    }
                }
                activeEnemies.add(enemy);
                lastSmallSpawn = 0.35f;
            }
            lastSmallSpawn -= delta;

            if(lastMediumSpawn < 0) {
                Enemy enemy = enemyPool.obtain();
                if(rand.nextBoolean()) { //Up or Down
                    if(rand.nextBoolean()) {
                        enemy.init(4, 8, 32, rand.nextInt(1024), 0, 30f, Color.CORAL);
                    } else {
                        enemy.init(4, 8, 32, rand.nextInt(1024), 768, 30f, Color.CORAL);
                    }
                } else {
                    if(rand.nextBoolean()) { //left or right
                        enemy.init(4, 8, 32, 0, rand.nextInt(768), 30f, Color.CORAL);
                    } else {
                        enemy.init(4, 8, 32, 1024, rand.nextInt(768), 30f, Color.CORAL);
                    }
                }
                activeEnemies.add(enemy);
                lastMediumSpawn = 1.25f;
            }
            lastMediumSpawn -= delta;
        } else if (time > 120 && time < 150) { //TODO: Sixth Wave
            if (lastSmallSpawn < 0) {
                Enemy enemy = enemyPool.obtain();
                if (rand.nextBoolean()) { //Up or Down
                    if (rand.nextBoolean()) {
                        enemy.init(2, 4, 16, rand.nextInt(1024), 0, 75f, Color.TEAL);
                    } else {
                        enemy.init(2, 4, 16, rand.nextInt(1024), 768, 75f, Color.TEAL);
                    }
                } else {
                    if (rand.nextBoolean()) { //left or right
                        enemy.init(2, 4, 16, 0, rand.nextInt(768), 75f, Color.TEAL);
                    } else {
                        enemy.init(2, 4, 16, 1024, rand.nextInt(768), 75f, Color.TEAL);
                    }
                }
                activeEnemies.add(enemy);
                lastSmallSpawn = 0.175f;
            }
            lastSmallSpawn -= delta;

            if (lastMediumSpawn < 0) {
                Enemy enemy = enemyPool.obtain();
                if (rand.nextBoolean()) { //Up or Down
                    if (rand.nextBoolean()) {
                        enemy.init(8, 12, 32, rand.nextInt(1024), 0, 30f, Color.CORAL);
                    } else {
                        enemy.init(8, 12, 32, rand.nextInt(1024), 768, 30f, Color.CORAL);
                    }
                } else {
                    if (rand.nextBoolean()) { //left or right
                        enemy.init(8, 12, 32, 0, rand.nextInt(768), 30f, Color.CORAL);
                    } else {
                        enemy.init(8, 12, 32, 1024, rand.nextInt(768), 30f, Color.CORAL);
                    }
                }
                activeEnemies.add(enemy);
                lastMediumSpawn = 1f;
            }
            lastMediumSpawn -= delta;

            if (lastLargeSpawn < 0) {
                Enemy enemy = enemyPool.obtain();
                if (rand.nextBoolean()) { //Up or Down
                    if (rand.nextBoolean()) {
                        enemy.init(16, 16, 128, rand.nextInt(1024), 0, 25f, Color.RED);
                    } else {
                        enemy.init(16, 16, 128, rand.nextInt(1024), 768, 25f, Color.RED);
                    }
                } else {
                    if (rand.nextBoolean()) { //left or right
                        enemy.init(16, 16, 128, 0, rand.nextInt(768), 25f, Color.RED);
                    } else {
                        enemy.init(16, 16, 128, 1024, rand.nextInt(768), 25f, Color.RED);
                    }
                }
                activeEnemies.add(enemy);
                lastLargeSpawn = 2f;
            }
            lastLargeSpawn -= delta;
        } else if(time > 150) {
            if (lastSmallSpawn < 0) {
                Enemy enemy = enemyPool.obtain();
                if (rand.nextBoolean()) { //Up or Down
                    if (rand.nextBoolean()) {
                        enemy.init(2, 4, 16, rand.nextInt(1024), 0, 75f, Color.TEAL);
                    } else {
                        enemy.init(2, 4, 16, rand.nextInt(1024), 768, 75f, Color.TEAL);
                    }
                } else {
                    if (rand.nextBoolean()) { //left or right
                        enemy.init(2, 4, 16, 0, rand.nextInt(768), 75f, Color.TEAL);
                    } else {
                        enemy.init(2, 4, 16, 1024, rand.nextInt(768), 75f, Color.TEAL);
                    }
                }
                activeEnemies.add(enemy);
                lastSmallSpawn = 0.05f;
            }
            lastSmallSpawn -= delta;

            if (lastMediumSpawn < 0) {
                Enemy enemy = enemyPool.obtain();
                if (rand.nextBoolean()) { //Up or Down
                    if (rand.nextBoolean()) {
                        enemy.init(8, 12, 32, rand.nextInt(1024), 0, 30f, Color.CORAL);
                    } else {
                        enemy.init(8, 12, 32, rand.nextInt(1024), 768, 30f, Color.CORAL);
                    }
                } else {
                    if (rand.nextBoolean()) { //left or right
                        enemy.init(8, 12, 32, 0, rand.nextInt(768), 30f, Color.CORAL);
                    } else {
                        enemy.init(8, 12, 32, 1024, rand.nextInt(768), 30f, Color.CORAL);
                    }
                }
                activeEnemies.add(enemy);
                lastMediumSpawn = 0.5f;
            }
            lastMediumSpawn -= delta;

            if (lastLargeSpawn < 0) {
                Enemy enemy = enemyPool.obtain();
                if (rand.nextBoolean()) { //Up or Down
                    if (rand.nextBoolean()) {
                        enemy.init(16, 16, 128, rand.nextInt(1024), 0, 25f, Color.RED);
                    } else {
                        enemy.init(16, 16, 128, rand.nextInt(1024), 768, 25f, Color.RED);
                    }
                } else {
                    if (rand.nextBoolean()) { //left or right
                        enemy.init(16, 16, 128, 0, rand.nextInt(768), 25f, Color.RED);
                    } else {
                        enemy.init(16, 16, 128, 1024, rand.nextInt(768), 25f, Color.RED);
                    }
                }
                activeEnemies.add(enemy);
                lastLargeSpawn = 1f;
            }
            lastLargeSpawn -= delta;
        }

        /**
         * Start drawing
         */
        batch.begin();
        /**
         * Draw Background tiles
         */
        for(int i = 0; i < mapSize; i++) {
            for(int k = 0; k < mapSize; k++) {
                tiles[i][k].render(batch);
            }
        }

        /**
         * Draw Effects
         */
        effectRenderer.renderParticlesUnder(batch, delta);

        /**
         * Draw Game Objects
         */
        for(int i = 0; i < objects.size(); i++) {
            GameObject obj = objects.get(i);
            if(obj.health > 0) {
                obj.shootAtClosestEnemies(activeEnemies, effectRenderer, delta);
                obj.render(batch);
            } else {
                effectRenderer.destroyedBuilding(obj);
                objects.remove(i);
            }
        }

        /**
         * Draw Enemies
         */
        batch.end();
        shapeRenderer.setAutoShapeType(true);
        shapeRenderer.begin();
        if(activeEnemies.size > 0) {
            for (int i = 0; i < activeEnemies.size; i++) {
                Enemy e = activeEnemies.get(i);
                if(e.health > 64) {
                    e.health = 0;
                }
                if (e.health > 0) {
                    e.moveTowardsClosestBuilding(objects, effectRenderer, delta);
                    e.draw(shapeRenderer);
                } else {
                    effectRenderer.killedEnemy(e);
                    money += e.money;
                    activeEnemies.removeIndex(i);
                    enemyPool.free(e);

                }
            }
        }
        shapeRenderer.end();
        batch.begin();

        /**
         * Draw Effects
         */
        effectRenderer.renderParticlesAbove(batch, delta);

        Texture text = null;
        if(buildBunker) {
            text = game.assets.get("bunker.png", Texture.class);
        }
        if(buildRocket) {
            text = game.assets.get("rocketlauncher.png", Texture.class);
        }

        if(text != null) {
            batch.setColor(1,1,1,0.5f);
            batch.draw(text, Gdx.input.getX() - text.getWidth()/2, Gdx.graphics.getHeight() - Gdx.input.getY() - text.getHeight()/2);
            batch.setColor(1,1,1,1);
        }

        batch.end();
        /**
         * UI stuff
         */
        uiRenderer.render();


        /**
         * Debug Stuff
         */
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin();
        for(GameObject obj : objects) {
            obj.renderDebug(shapeRenderer);
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        if(objects.size() == 0 || objects.get(0).health <= 0 || objects.get(0).name != "spaceship") {
            game.setScreen(new EndState(game, false));
            game.assets.get("sound/gameOver.wav", Sound.class).play(0.33f);
            return;
        }

        this.lastRepairTick += delta;

        if(lastRepairTick >= 5.2f) {
            this.objects.get(0).health += 2;
            this.lastRepairTick = 0;
        }

        /**
         * Display Money
         */
        batch.begin();
        game.assets.get("font.fnt", BitmapFont.class).draw(batch, "Money: " + money + "$", 20, 132);
        batch.end();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) { //Don't care
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(button == Input.Buttons.LEFT) {
            GameObject obj = null;

            int pay = 0;
            if(Gdx.input.getY() < 688) {
                if (buildBunker) {
                    if(money >= 100) {
                        pay = 100;
                        obj = new GameObject("blub", Gdx.input.getX() - 16, Gdx.graphics.getHeight() - Gdx.input.getY() - 16, 32, 4, 1, 1.5f, 200, false, false, game.assets.get("bunker.png", Texture.class));
                    } else {
                        game.assets.get("sound/nope.wav", Sound.class).play(0.25f);
                    }
                }
                if (buildRocket) {
                    if(money >= 250) {
                        pay = 250;
                        obj = new GameObject("blub", Gdx.input.getX() - 16, Gdx.graphics.getHeight() - Gdx.input.getY() - 16, 32, 8, 10, 6f, 600, true, false, game.assets.get("rocketlauncher.png", Texture.class));
                    } else {
                        game.assets.get("sound/nope.wav", Sound.class).play(0.25f);
                    }
                }
            }

            if(obj != null) {
                boolean place = true;
                for (GameObject o : objects) {
                    if(o.overlaps(obj)) {
                        place = false;
                    }
                }
                if(place) {
                    money -= pay;
                    objects.add(obj);
                    effectRenderer.buildingBuild(obj);
                } else {
                    effectRenderer.buildingFail();
                }
            } else {
                for(int i = 0; i < uiRenderer.buttons.size(); i++) {
                    if(uiRenderer.buttons.get(i).isSelected(Gdx.input.getX(), Gdx.graphics.getHeight() - Gdx.input.getY())) {
                        switch(i) {
                            case 0:
                                if(money >= 100) {
                                    buildBunker = true; buildRocket = buildZapper = false;
                                } else {
                                    game.assets.get("sound/nope.wav", Sound.class).play(0.25f);
                                } break;
                            case 1:
                                if(money >= 250) {
                                    buildRocket = true; buildBunker = buildZapper = false;
                                } else {
                                    game.assets.get("sound/nope.wav", Sound.class).play(0.25f);
                                } break;
                            case 2:
                                if(money >= 500) {
                                    buildZapper = true; buildRocket = buildBunker = false;
                                } else {
                                    game.assets.get("sound/nope.wav", Sound.class).play(0.25f);
                                } break;
                        }
                    }
                }
            }
        } else if(button == Input.Buttons.RIGHT) {
            buildBunker = false;
            buildRocket = false;
            buildZapper = false;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) { //Don't care about that
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) { //Don't care about that
        return false;
    }

    @Override
    public boolean scrolled(int amount) { //Don't care about that
        return false;
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
        effectRenderer.dispose();
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public void dispose() {
        this.hide();
    }
}
