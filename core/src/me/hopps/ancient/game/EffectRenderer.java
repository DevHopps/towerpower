package me.hopps.ancient.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import me.hopps.ancient.AncientGame;

import java.util.HashMap;

public class EffectRenderer {

    private AncientGame game;

    private boolean shotFired, killedEnemy, damagedBuilding, destroyedBuilding, buildingBuild;
    private boolean buildingFail;

    private ParticleEffectPool fireEffectPool;
    private ParticleEffectPool boomEffectPool;
    private ParticleEffectPool bloodEffectPool;
    private ParticleEffectPool truemmerEffectPool;
    private Array<ParticleEffectPool.PooledEffect> aboveEffects = new Array();
    private Array<ParticleEffectPool.PooledEffect> underEffects = new Array();

    private HashMap<GameObject, ParticleEffectPool.PooledEffect> objectFires = new HashMap<>();

    public EffectRenderer(AncientGame game) {
        this.game = game;

        /**
         * Setup the particle effects
         */
        ParticleEffect fireEffect = new ParticleEffect();
        fireEffect.load(Gdx.files.internal("particles/fire"), Gdx.files.internal("particles"));
        fireEffect.setEmittersCleanUpBlendFunction(false);
        fireEffect.scaleEffect(0.25f);

        ParticleEffect boomEffect = new ParticleEffect();
        boomEffect.load(Gdx.files.internal("particles/boom"), Gdx.files.internal("particles"));
        boomEffect.setEmittersCleanUpBlendFunction(false);
        boomEffect.scaleEffect(0.25f);

        ParticleEffect bloodEffect = new ParticleEffect();
        bloodEffect.load(Gdx.files.internal("particles/blood"), Gdx.files.internal("particles"));
        bloodEffect.setEmittersCleanUpBlendFunction(false);
        bloodEffect.scaleEffect(0.1f);

        ParticleEffect truemmerEffect = new ParticleEffect();
        truemmerEffect.load(Gdx.files.internal("particles/truemmer"), Gdx.files.internal("particles"));
        truemmerEffect.setEmittersCleanUpBlendFunction(false);
        truemmerEffect.scaleEffect(0.25f);

        /**
         * Set up the pools
         */
        fireEffectPool = new ParticleEffectPool(fireEffect, 10, 25);
        boomEffectPool = new ParticleEffectPool(boomEffect, 10, 25);
        bloodEffectPool = new ParticleEffectPool(bloodEffect, 50, 200);
        truemmerEffectPool = new ParticleEffectPool(truemmerEffect, 10, 50);

        ParticleEffectPool.PooledEffect effect = fireEffectPool.obtain();
        effect.setPosition(515, 355);
        aboveEffects.add(effect);
    }

    private void resetSounds() {
        shotFired = false;
        killedEnemy = false;
        damagedBuilding = false;
        destroyedBuilding = false;
        buildingBuild = false;
        buildingFail = false;
    }

    public void renderParticlesUnder(SpriteBatch batch, float delta) {
        int srcFunc = batch.getBlendSrcFunc();
        int dstFunc = batch.getBlendDstFunc();

        /**
         * Draw all particle effects
         */
        for (int i = underEffects.size - 1; i >= 0; i--) {
            ParticleEffectPool.PooledEffect effect = underEffects.get(i);
            effect.draw(batch, delta);
            if (effect.isComplete()) {
                effect.free();
                underEffects.removeIndex(i);
            }
        }

        batch.setBlendFunction(srcFunc, dstFunc);
    }

    public void renderParticlesAbove(SpriteBatch batch, float delta) {
        this.resetSounds();
        int srcFunc = batch.getBlendSrcFunc();
        int dstFunc = batch.getBlendDstFunc();

        /**
         * Draw all particle effects
         */
        for (int i = aboveEffects.size - 1; i >= 0; i--) {
            ParticleEffectPool.PooledEffect effect = aboveEffects.get(i);
            effect.draw(batch, delta);
            if (effect.isComplete()) {
                effect.free();
                aboveEffects.removeIndex(i);
            }
        }

        batch.setBlendFunction(srcFunc, dstFunc);
    }

    public void killedEnemy(Enemy e) {
        if(!killedEnemy) {
            game.assets.get("sound/dieSound.wav", Sound.class).play(0.075f);
            killedEnemy = true;
        }
    }

    public void shotFired(Enemy target) {
        if(!shotFired) {
            game.assets.get("sound/shotFired.wav", Sound.class).play(0.25f);
            shotFired = true;
        }
        ParticleEffectPool.PooledEffect effect = bloodEffectPool.obtain();
        effect.setPosition(target.x + target.size / 2, target.y + target.size / 2);
        underEffects.add(effect);
    }

    public void damagedBuilding(GameObject obj) {
        if(!damagedBuilding) {
            game.assets.get("sound/explosion.wav", Sound.class).play(0.25f);
            damagedBuilding = true;
        }
        if(!objectFires.containsKey(obj)) {
            ParticleEffectPool.PooledEffect effect = fireEffectPool.obtain();
            effect.setPosition(obj.x + obj.size / 2, obj.y + obj.size / 2);
            aboveEffects.add(effect);
            objectFires.put(obj, effect);
        }
    }

    public void destroyedBuilding(GameObject obj) {
        if(!destroyedBuilding) {
            game.assets.get("sound/bigExplosion.wav", Sound.class).play(0.25f);
            destroyedBuilding = true;
        }
        if(objectFires.containsKey(obj)) {
            objectFires.get(obj).free();
            aboveEffects.removeIndex(aboveEffects.indexOf(objectFires.get(obj), true));

            ParticleEffectPool.PooledEffect effect = truemmerEffectPool.obtain();
            effect.setPosition(obj.x + obj.size / 2, obj.y + obj.size / 2);
            underEffects.add(effect);

            effect = boomEffectPool.obtain();
            effect.setPosition(obj.x + obj.size / 2, obj.y + obj.size / 2);
            aboveEffects.add(effect);
        }
    }

    public void buildingBuild(GameObject obj) {
        if(!buildingBuild) {
            game.assets.get("sound/clickSound.wav", Sound.class).play(0.25f);
            buildingBuild = true;
        }
    }

    public void buildingFail() {
        if(!buildingFail) {
            game.assets.get("sound/nope.wav", Sound.class).play(0.25f);
            buildingFail = true;
        }
    }

    public void rocketHit(Enemy e) {
        game.assets.get("sound/explosion.wav", Sound.class).play(0.25f);
        ParticleEffectPool.PooledEffect effect = boomEffectPool.obtain();
        effect.setPosition(e.x + e.size / 2, e.y + e.size / 2);
        effect.scaleEffect(4f);
        aboveEffects.add(effect);
        effect.scaleEffect(0.25f);
    }

    public void dispose() {
        for (int i = underEffects.size - 1; i >= 0; i--)
            underEffects.get(i).dispose(); //free all the effects back to the pool
        underEffects.clear(); //clear the current effects array

        for (int i = aboveEffects.size - 1; i >= 0; i--)
            aboveEffects.get(i).dispose(); //free all the effects back to the pool
        aboveEffects.clear(); //clear the current effects array
    }
}
