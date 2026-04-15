package com.monstrous.terrain;



import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.CubemapAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;

public class TerrainDemo extends ApplicationAdapter {
	public PerspectiveCamera cam;
	public CameraInputController camController;
	public Environment environment;
	public SpriteBatch batch;
	public GUI gui;
    public Terrain terrain;

	CatmullRomSpline<Vector3> myCatmull;
	ShapeRenderer shapeRenderer;
	float time;
	private Vector3 tmp = new Vector3();
	private Vector3[] pathPoints = new Vector3[100];	// to render spline (debug)


	@Override
	public void create() {

        gui = new GUI(this);

        terrain = new Terrain(gui, 255, 7, 32f);

		// create perspective camera
		cam = new PerspectiveCamera(70, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0, 20000, 0);
		cam.lookAt(0, 0, 0);
		cam.far = 100000f;
		cam.near = 10f;
		cam.update();

		// add camera controller
		camController = new CameraInputController(cam);
        camController.scrollFactor = -100f;

		// input multiplexer to send inputs to GUI and to cam controller
		InputMultiplexer im = new InputMultiplexer();
		Gdx.input.setInputProcessor(im);
		im.addProcessor(gui.stage); // set stage as first input processor
		im.addProcessor(camController);

		// define some lighting
		environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.Fog, Color.SKY));

		batch = new SpriteBatch();
		buildCameraPath();
		shapeRenderer = new ShapeRenderer();
	}

	@Override
	public void resize(int width, int height) {
		cam.viewportWidth = Gdx.graphics.getWidth();
		cam.viewportHeight = Gdx.graphics.getHeight();
		cam.update();

        batch.getProjectionMatrix().setToOrtho2D(0,0,width, height);

		gui.resize(width, height);
	}

	@Override
	public void render() {
		// update camera positioning
		camController.update();
        float delta = Gdx.graphics.getDeltaTime();
		time += delta;
        if(gui.flyCamera)
		    moveCameraAlongSpline(time);
        else
            cam.lookAt(0, 0, 0);

        if(!gui.freezeLoD && gui.showTerrain)
            terrain.update(cam);

		// clear screen
        ScreenUtils.clear(Color.SKY, true);

        if(gui.showTerrain)
            terrain.render(cam, environment);

        if(gui.showCameraPath)
		    renderPath();

		if (gui.showHeightmap) {
			batch.begin();
			batch.draw(terrain.getHeightMapTexture(), Gdx.graphics.getWidth()-256, 0, 256, 256);
			batch.end();
		}
		gui.render(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void dispose() {
		batch.dispose();
        terrain.dispose();
        gui.dispose();
	}

	private void buildCameraPath() {
        float ht = 2000f;
        float scl = 16f;

		Vector3[] controlPoints = {
				new Vector3(-2000*scl, ht+400f*scl, 2000*scl),
				new Vector3(2000*scl, ht+500*scl, 2500*scl),

				new Vector3(2500*scl, ht+800*scl, -3000*scl),

				new Vector3(-1500*scl, ht+300*scl, -2400*scl),
                new Vector3(-500*scl, ht+800*scl, -500*scl),

                new Vector3(500*scl, ht+400*scl, 500*scl),

        };
		myCatmull = new CatmullRomSpline<Vector3>(controlPoints, true);

		// fill array of points for debug render
		for(int i = 0; i < 100; i++) {
			Vector3 out = new Vector3();
			myCatmull.valueAt(out, i/100f);
			pathPoints[i] = out;
		}
	}



	private void moveCameraAlongSpline(float time) {
		float t = 0.015f*time;
		if (t > 1)
			t -= (int)t;
		myCatmull.valueAt(tmp, t);
		cam.position.set(tmp);
		myCatmull.derivativeAt(tmp, t);
		cam.direction.set(tmp);
        cam.up.set(Vector3.Y);
		cam.update();
	}

	// render path as a red line (debug)
	private void renderPath() {
		shapeRenderer.setProjectionMatrix(cam.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(1,0,0,1);
    	for(int i = 0; i < 100-1; i++)
		{
			shapeRenderer.line(pathPoints[i], pathPoints[i+1]);
		}
		shapeRenderer.line(pathPoints[99], pathPoints[0]);
		shapeRenderer.end();
	}

}

