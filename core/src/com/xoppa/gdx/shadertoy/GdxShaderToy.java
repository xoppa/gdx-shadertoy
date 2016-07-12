package com.xoppa.gdx.shadertoy;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;

public class GdxShaderToy extends ApplicationAdapter {
	Stage stage;
	Texture img;
	FullQuadToy toy;
	CollapsableTextWindow logWindow;
	CollapsableTextWindow vsWindow;
	CollapsableTextWindow fsWindow;
	float codeChangedTimer = -1f;
	long startTimeMillis;
	long fpsStartTimer;
	Logger logger;

	@Override
	public void create () {
		ShaderProgram.pedantic = false;
		startTimeMillis = TimeUtils.millis();
		fpsStartTimer = TimeUtils.nanoTime();
		img = new Texture(Gdx.files.internal("badlogic.jpg"));
		img.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
		VisUI.load();
		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);

		logger = new Logger("TEST", Logger.INFO) {
			com.badlogic.gdx.utils.StringBuilder sb = new StringBuilder();
			private void add(String message) {
				long time = TimeUtils.timeSinceMillis(startTimeMillis);
				long s = time / 1000;
				long m = s / 60;
				long h = m / 60;
				sb.setLength(0);
				sb.append(h, 2).append(':').append(m % 60, 2).append(':').append(s % 60, 2).append('.').append(time % 1000, 3);
				sb.append(" ").append(message).append("\n");
				logWindow.addText(sb.toString());
			}

			@Override
			public void info(String message) {
				super.info(message);
				//add("[#ffff00]" + message + "[]");
				add("INFO: " + message);
			}

			@Override
			public void error(String message) {
				super.error(message);
				//add("[red]" + message + "[]");
				add("ERROR: " + message);
			}
		};

		toy = new FullQuadToy();
		toy.create(logger);
		toy.setTexture(img);

		ChangeListener codeChangeListener = new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				codeChangedTimer = 3f;
			}
		};

		float w = Gdx.graphics.getWidth();
		float h = Gdx.graphics.getHeight();
		float hw = w * 0.5f;
		logWindow = new CollapsableTextWindow("Log", 0, 0, w, 100f);
		stage.addActor(logWindow);

        final String defaultVS = "";
		final String defaultFS = "";

		vsWindow = new CollapsableTextWindow("Vertex Shader", 0, 100f, hw, h - 100f);
		vsWindow.setText(defaultVS);
		vsWindow.addTextAreaListener(codeChangeListener);
		stage.addActor(vsWindow);
		fsWindow = new CollapsableTextWindow("Fragment Shader", hw, 100f, hw, h - 100f);
		fsWindow.setText(defaultFS);
		fsWindow.addTextAreaListener(codeChangeListener);
		stage.addActor(fsWindow);

		//toy.setShader(defaultVS, defaultFS);
	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height);
		toy.resize(width, height);
	}

	@Override
	public void render () {
        update();
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		toy.render();
		stage.act();
		stage.draw();
	}

    private void update() {
        if (codeChangedTimer > 0f) {
            codeChangedTimer -= Gdx.graphics.getDeltaTime();
            if (codeChangedTimer <= 0) {
				toy.setShader(vsWindow.getText(), fsWindow.getText());
            }
        }
		if (TimeUtils.nanoTime() - fpsStartTimer > 1000000000) /* 1,000,000,000ns == one second */{
			logger.info("fps: " + Gdx.graphics.getFramesPerSecond());
			fpsStartTimer = TimeUtils.nanoTime();
		}
    }

	@Override
	public void dispose() {
		VisUI.dispose();
		stage.dispose();
		img.dispose();
	}
}
