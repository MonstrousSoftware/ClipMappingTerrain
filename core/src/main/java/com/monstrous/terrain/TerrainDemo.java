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
	public ModelBatch modelBatch;
	public SpriteBatch batch;
	public GUI gui;
    private ModelInstance xyz;
    private ModelInstance character;
    public Terrain terrain;
    private CharacterController controller;
    public PerspectiveCamera characterCam;

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
		cam.near = 1f;
		cam.update();

        characterCam = new PerspectiveCamera(70, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        characterCam.position.set(0, 10, 0);
        characterCam.lookAt(0, 0, -100);
        characterCam.far = 20000f;
        characterCam.near = 0.1f;
        characterCam.update(true);

		// add camera controller
		camController = new CameraInputController(cam);
        camController.scrollFactor = -100f;

        addAxes();
        addCharacter();

        controller = new CharacterController(character);

		// input multiplexer to send inputs to GUI and to cam controller
		InputMultiplexer im = new InputMultiplexer();
		Gdx.input.setInputProcessor(im);
		im.addProcessor(gui.stage); // set stage as first input processor
        im.addProcessor(controller);
		im.addProcessor(camController);

		// define some lighting
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.5f, 0.5f, 0.5f, 1f));
		environment.add(new DirectionalLight().set(1, 1, 1f, -.4f, -0.4f, -0.2f));
        environment.set(new ColorAttribute(ColorAttribute.Fog, Color.SKY));

        modelBatch = new ModelBatch();

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

    private Vector3 pos = new Vector3();

	@Override
	public void render() {
		// update camera positioning
		//camController.update();
        float delta = Gdx.graphics.getDeltaTime();
		time += delta;
        if(gui.flyCamera)
		    moveCameraAlongSpline(time);

        controller.update(delta);
        character.transform.getTranslation(pos);
        pos.scl(1f/(8f*16128.0f));
        pos.add(0.5f, 0.0f, 0.5f);
        float h = terrain.heightMap.get(pos.x, pos.z);
        character.transform.getTranslation(pos);
        pos.y = h + 350f;
        character.transform.setTranslation(pos);
        //Gdx.app.log("position: ", pos.toString());
        character.transform.getTranslation(characterCam.position);
        characterCam.direction.set(0,0,-1);
        characterCam.direction.rotate(Vector3.Y, controller.angle);

        characterCam.update(true);

        if(!gui.freezeLoD && gui.showTerrain)
            terrain.update(cam);

		// clear screen
        ScreenUtils.clear(Color.SKY, true);

		modelBatch.begin(cam);
        modelBatch.render(xyz);
        modelBatch.render(character, environment);
        modelBatch.end();

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
		modelBatch.dispose();
		batch.dispose();
        terrain.dispose();
        gui.dispose();
	}


    public void addAxes(){
        ModelBuilder modelBuilder = new ModelBuilder();
        Model xyzModel = modelBuilder.createXYZCoordinates(50f, new Material(),
            VertexAttributes.Usage.Position|VertexAttributes.Usage.ColorPacked);
        xyz = new ModelInstance(xyzModel, Vector3.Zero);
	}

    public void addCharacter() {

        final float size = 20f;

        ModelBuilder modelBuilder = new ModelBuilder();
        Model model = modelBuilder.createCone(size, size, size, 4,
            new Material(ColorAttribute.createDiffuse(Color.CYAN)),
            VertexAttributes.Usage.Position|VertexAttributes.Usage.ColorPacked|VertexAttributes.Usage.Normal);
        character = new ModelInstance(model, 0, size*2, 0);
        character.transform.rotate(Vector3.X, 90);
    }



	private void buildCameraPath() {
        float ht = 2000f;
        float scl = 16f;

		Vector3[] controlPoints = {
				new Vector3(-2000*scl, ht+400f*scl, 2000*scl),
				new Vector3(2000*scl, ht+500*scl, 2500*scl),

				new Vector3(2500*scl, ht+800*scl, -3000*scl),

				new Vector3(-1500*scl, ht+400*scl, -2400*scl),
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
        tmp.y = -0.8f;
        tmp.nor();
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

