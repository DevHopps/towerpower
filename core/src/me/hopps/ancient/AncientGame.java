package me.hopps.ancient;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import me.hopps.ancient.states.LoadingState;

public class AncientGame extends Game {

	public AssetManager assets;
	public ShapeRenderer shapeRenderer;
	public SpriteBatch batch;
	
	@Override
	public void create () {
		assets = new AssetManager();

		shapeRenderer = new ShapeRenderer();
		batch = new SpriteBatch();

		assets.load("loading.png", Texture.class);

		assets.load("start.png", Texture.class);
		assets.load("tiles.png", Texture.class);
		assets.load("spaceship.png", Texture.class);
		assets.load("bunker.png", Texture.class);
		assets.load("rocketlauncher.png", Texture.class);

		assets.load("menu/buybunker.png", Texture.class);
		assets.load("menu/buyrockettower.png", Texture.class);

		assets.load("gamewin.png", Texture.class);
		assets.load("gameover.png", Texture.class);

		assets.load("sound/alarmSound.wav", Sound.class);
		assets.load("sound/bigExplosion.wav", Sound.class);
		assets.load("sound/clickSound.wav", Sound.class);
		assets.load("sound/dieSound.wav", Sound.class);
		assets.load("sound/explosion.wav", Sound.class);
		assets.load("sound/gameOver.wav", Sound.class);
		assets.load("sound/nope.wav", Sound.class);
		assets.load("sound/PickUp.wav", Sound.class);
		assets.load("sound/shotFired.wav", Sound.class);
		assets.load("sound/winSound.wav", Sound.class);

		assets.load("font.fnt", BitmapFont.class);

		this.setScreen(new LoadingState(this));
	}
	
	@Override
	public void dispose () {
		assets.dispose();
	}
}
