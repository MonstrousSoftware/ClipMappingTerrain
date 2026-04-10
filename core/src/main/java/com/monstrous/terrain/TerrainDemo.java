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
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

public class TerrainDemo extends ApplicationAdapter {
	public PerspectiveCamera cam;
	public OrthographicCamera orthoCam;
	public CameraInputController camController;
	public Environment environment;
	public ModelBatch modelBatch;
	public SpriteBatch batch;
	public GUI gui;
	public Cubemap cubemap;
	public ModelInstance skybox;
    private ModelInstance xyz;
    private ModelInstance character;
    private Terrain terrain;
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

        terrain = new Terrain(gui, 63, 8, 8f);

		// create perspective camera
		cam = new PerspectiveCamera(70, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0, 200, 100);
		cam.lookAt(0, 0, -100);
		cam.far = 200000f;
		cam.near = 0.1f;
		cam.update();

        characterCam = new PerspectiveCamera(70, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        characterCam.position.set(0, 10, 0);
        characterCam.lookAt(0, 0, -100);
        characterCam.far = 20000f;
        characterCam.near = 0.1f;
        characterCam.update(true);

		// add camera controller
		camController = new CameraInputController(cam);
        camController.scrollFactor = -10f;

        addSkybox();
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
		String prefix = "textures/elyvision/";
		String ext = ".png";

		cubemap = new Cubemap(Gdx.files.internal(prefix + "right" + ext),
				Gdx.files.internal(prefix + "left" + ext),
				Gdx.files.internal(prefix + "top" + ext),
				Gdx.files.internal(prefix + "bottom" + ext),
				Gdx.files.internal(prefix + "front" + ext),
				Gdx.files.internal(prefix + "back" + ext));
		environment.set(new CubemapAttribute(CubemapAttribute.EnvironmentMap, cubemap));


		if (false) {
			modelBatch = new ModelBatch(new DefaultShaderProvider() {
				@Override
				protected Shader createShader(final Renderable renderable) {
					Shader sh = new DefaultShader(renderable, new DefaultShader.Config(Gdx.files.internal("uv.vertex.glsl").readString(), Gdx.files.internal("uv.fragment.glsl").readString()));
					return sh;
				}
			});
		} else {
			modelBatch = new ModelBatch(new DefaultShaderProvider() {
				@Override
				protected Shader createShader(final Renderable renderable) {
					ColorAttribute colAttr = (ColorAttribute) renderable.material.get(ColorAttribute.Diffuse);
					if (colAttr != null && colAttr.color.equals(Color.SKY))    // special colour
					{
						return new DefaultShader(renderable, new DefaultShader.Config(Gdx.files.internal("skybox.vertex.glsl").readString(), Gdx.files.internal("skybox.fragment.glsl").readString()));
					}
					return new DefaultShader(renderable);
				}
			});
		}

		// create ortho camera for overlay
		orthoCam = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch = new SpriteBatch();
		buildCameraPath();
		shapeRenderer = new ShapeRenderer();

	}

	@Override
	public void resize(int width, int height) {
		cam.viewportWidth = Gdx.graphics.getWidth();
		cam.viewportHeight = Gdx.graphics.getHeight();
		cam.update();

		orthoCam.viewportHeight = height;
		orthoCam.viewportWidth = width;
		orthoCam.translate(width / 2, height / 2);
		orthoCam.update();

		batch.setProjectionMatrix(orthoCam.combined);

		gui.resize(width, height);
	}

	@Override
	public void render() {
		// update camera positioning
		//camController.update();
        float delta = Gdx.graphics.getDeltaTime();
		time += delta;
		//updateCamera(time);

        controller.update(delta);
        character.transform.getTranslation(characterCam.position);
        characterCam.direction.set(0,0,-1);
        characterCam.direction.rotate(Vector3.Y, -controller.angle);

        characterCam.update(true);
        terrain.update(character);

        // avoid the camera going under the terrain
//		float heightBelowCam = terrain.getHeight(cam.position.x, cam.position.z);
//		if (cam.position.y < heightBelowCam + 10f)
//			cam.position.y = heightBelowCam + 10f;

		// clear screen
        ScreenUtils.clear(Color.BLACK, true);

		// render model instance
		modelBatch.begin(cam);
		if (gui.showSkybox) {
			modelBatch.render(skybox, environment);
			Gdx.gl.glClear(GL20.GL_DEPTH_BUFFER_BIT);
		}
        modelBatch.render(xyz);
        terrain.render(characterCam, modelBatch, environment);
		//modelBatch.render(instances, environment);
        modelBatch.render(character, environment);
		modelBatch.end();

        if(gui.showCameraPath)
		    renderPath();

		if (gui.showHeightmap) {
			batch.begin();
			batch.draw(terrain.getHeightMapTexture(), 0, 0);
			batch.end();
		}
		gui.render(Gdx.graphics.getDeltaTime());
	}

	@Override
	public void dispose() {
		modelBatch.dispose();
		batch.dispose();
		//heightMapTexture.dispose();
        terrain.dispose();
	}


	public void addSkybox() {

        final float size = 150000f;

        ModelBuilder modelBuilder = new ModelBuilder();
        Model model = modelBuilder.createBox(size, size, size,
            new Material(ColorAttribute.createDiffuse(Color.SKY),    // color signal shader provider for special shader
                IntAttribute.createCullFace(GL20.GL_BACK)),     // don't cull back faces because we are viewing from inside the box
            VertexAttributes.Usage.Position);
        skybox = new ModelInstance(model);
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
//        Model model = modelBuilder.createBox(size, size, size,
//            new Material(ColorAttribute.createDiffuse(Color.CYAN)),
//                       VertexAttributes.Usage.Position|VertexAttributes.Usage.ColorPacked|VertexAttributes.Usage.Normal);
        character = new ModelInstance(model, 0, size*2, 0);
        character.transform.rotate(Vector3.X, 90);
    }



	private void buildCameraPath() {
		Vector3[] controlPoints = {
				new Vector3(-2000, 400f, 2000),
				new Vector3(2000, 500, 2500),

				new Vector3(2500, 2600, -3000),

				new Vector3(-1500, 400, -2400)};
		myCatmull = new CatmullRomSpline<Vector3>(controlPoints, true);

		// fill array of points for debug render
		for(int i = 0; i < 100; i++) {
			Vector3 out = new Vector3();
			myCatmull.valueAt(out, i/100f);
			pathPoints[i] = out;
		}
	}



	private void updateCamera(float time) {
		float t = 0.03f*time;
		if (t > 1)
			t -= (int)t;
		myCatmull.valueAt(tmp, t);
		cam.position.set(tmp);
		myCatmull.derivativeAt(tmp, t);
		cam.direction.set(tmp);
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

