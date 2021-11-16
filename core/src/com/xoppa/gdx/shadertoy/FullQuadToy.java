package com.xoppa.gdx.shadertoy;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class FullQuadToy {
    private Viewport viewport;
    private SpriteBatch batch;
    private Texture texture;
    private ShaderProgram shader;
    private Logger logger;
    private OrthographicCamera cam;

    // Uniforms
    private int u_time;
    private int u_cursor;

    public void create(Logger logger) {
        this.logger = logger;
    
        float renderScale = 30f;
        int width = 1280;
        int height = 720;
        float invScale = 1.0f / renderScale;
        float viewportWidth = width * invScale;
        float viewportHeight = height * invScale;
    
        cam = new OrthographicCamera();
        batch = new SpriteBatch();
        //viewport = new ExtendViewport(width, height, cam);
        viewport = new ExtendViewport(viewportWidth, viewportHeight, cam);
        //viewport = new FitViewport(1000,1000);
        //viewport = new ScreenViewport();
        
        viewport.apply();
        
        batch = new SpriteBatch();
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
        batch.setProjectionMatrix(viewport.getCamera().combined);
        logger.info("Resize: " + width + " x " + height);
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public void setTexture(final Texture texture) {
        this.texture = texture;
    }

    float time;
    public void render() {
        if (texture != null) {
            batch.begin();
            if (shader != null) {
                if (u_time >= 0)
                    shader.setUniformf(u_time, time += Gdx.graphics.getDeltaTime());
                if (u_cursor >= 0) {
                    final float cursorX = (float)Gdx.input.getX() / (float)Gdx.graphics.getWidth();
                    final float cursorY = 1f - ((float)Gdx.input.getY() / (float)Gdx.graphics.getHeight());
                    shader.setUniformf(u_cursor, cursorX, cursorY);
                }
                //FIXME: set other uniforms if required by the shader
                shader.setUniformf("u_resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                //shader.setUniformf("u_resolution", 100, 5);
            }
            //batch.draw(texture, 0, 0, viewport.getWorldWidth(), viewport.getWorldHeight());
            //batch.draw(texture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            batch.draw(texture, 0, 0, viewport.getScreenWidth(), viewport.getScreenHeight());
            
            batch.end();
        }
    }

    public void setShader(ShaderProgram newShader) {
        String log = newShader.getLog().trim();
        if (!newShader.isCompiled()) {
            logger.error("Shader failed to compile: "+log);
            newShader.dispose();
            return;
        }
        if (log.length() > 0) {
            logger.info("Shader compiled with message: "+log);
        } else {
            logger.info("Shader compiled successfully");
        }
        batch.setShader(newShader);
        if (shader != null)
            shader.dispose();
        shader = newShader;

        u_time = shader.fetchUniformLocation("u_time", false);
        u_cursor = shader.fetchUniformLocation("u_cursor", false);
        //FIXME: fetch other uniforms
    }

    public void setShader(String vs, String fs) {
        setShader(new ShaderProgram(vs, fs));
    }
}
