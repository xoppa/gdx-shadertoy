package com.xoppa.gdx.shadertoy;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector3;

public class Test extends ApplicationAdapter {
	SpriteBatch batch, backgroundBatch;
	Texture img;
    Texture backgroundTex;
	
	private ShaderProgram shader = null;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		backgroundBatch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
        
        Pixmap pixmap = new Pixmap(0, 0, Pixmap.Format.RGBA4444);
        //pixmap.drawCircle(5, 5, 50);
        //pixmap.setColor(Color.RED);
        //pixmap.fill();
		backgroundTex = new Texture(pixmap);
		
		//shader = new ShaderProgram(Gdx.files.internal("grayscale.vsh"), Gdx.files.internal("grayscale.fsh"));
		shader = new ShaderProgram(Gdx.files.internal("grayscale.vsh"), Gdx.files.internal("grayscale.fsh"));
		
		ShaderProgram.pedantic = false;
		
		if (shader.isCompiled()) {
		    backgroundBatch.setShader(shader);
            Gdx.app.log(this.getClass().getSimpleName(), "Shader compiled!");
        } else {
		   //Gdx.app.error(this.getClass().getSimpleName(), '');
            Gdx.app.error(this.getClass().getSimpleName(), "Shader Compile Error: " + shader.getLog());
        }
	}
    Vector3 color = new Vector3(1, 1, 1);

	@Override
	public void render () {
	    /*
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
            color.set(0, 0, 0);
        } else {
            color.set(1,1,1);
        }*/
        
        
        
	    
	    Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
		
		shader.begin();
		shader.setUniformf("u_resolution", Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        //shader.setUniformf("u_distort", color);
        
		backgroundBatch.begin();
		backgroundBatch.draw(backgroundTex, 0,0);
		backgroundBatch.end();
		shader.end();
		

		
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
