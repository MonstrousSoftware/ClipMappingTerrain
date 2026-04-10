package com.monstrous.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class GUI {

    public Stage stage;
    public Skin skin;
    public TerrainDemo main;

    public boolean showHeightmap = false;
    public boolean showGrid = true;
    public boolean showTerrainTexture = false;
    public boolean showSkybox = false;
    public boolean showCameraPath = false;
    public int gridsize = 16;
    public float xoffset = 0;
    public float yoffset = 0;
  //  public int octaves = 5;
  //  public float persistence = 0.45f;
    public float maxHeight = 600f;
    private Label fpsLabel;


    public GUI ( TerrainDemo main ) {

        this.main = main;

        // GUI elements via Stage class
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        stage = new Stage(new ScreenViewport());

        addActors();
    }
    private void addActors() {

        int yy = Gdx.graphics.getHeight() - 40;



        Table controls = new Table();
        controls.left();
        // todo fix alignment

        // show heightmap
        //
        final CheckBox checkbox = new CheckBox("show map", skin);
        checkbox.setChecked(showHeightmap);
        checkbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showHeightmap = checkbox.isChecked();
             }
        });
        controls.add(checkbox).row();


        // show grid
        //
        final CheckBox gridCheckbox = new CheckBox("show grid", skin);
        gridCheckbox.setChecked(showGrid);
        gridCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showGrid = gridCheckbox.isChecked();
                refresh();
            }
        });
        controls.add(gridCheckbox).row();

        final CheckBox terrainTextureCheckbox = new CheckBox("show terrain texture", skin);
        terrainTextureCheckbox.setChecked(showTerrainTexture);
        terrainTextureCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showTerrainTexture = terrainTextureCheckbox.isChecked();
                refresh();
            }
        });
        controls.add(terrainTextureCheckbox).row();


        // show skybox
        //
        final CheckBox skyCheckbox = new CheckBox("skybox", skin);
        skyCheckbox.setChecked(showSkybox);
        skyCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showSkybox = skyCheckbox.isChecked();
                refresh();
            }
        });
        controls.add(skyCheckbox).row();

        final CheckBox camPathCheckbox = new CheckBox("camera spline", skin);
        camPathCheckbox.setChecked(showCameraPath);
        camPathCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showCameraPath = camPathCheckbox.isChecked();
                refresh();
            }
        });
        controls.add(camPathCheckbox).row();

        controls.add(new Label("FPS: ", skin));

        fpsLabel = new Label("0", skin);
        controls.add(fpsLabel).row();

        controls.pack();
        stage.addActor(controls);


//        // perlin grid size
//        final Slider slider = new Slider(2, 256, 1, false, skin);
//        slider.setAnimateDuration(0.1f);
//        slider.setValue(gridsize);
//        slider.setSize(150, 20);
//        slider.setPosition(100, yy);
//        stage.addActor(slider);
//        final Label label = new Label("gridsize", skin);
//        label.setPosition(0, yy);
//        stage.addActor(label);
//        final Label label2 = new Label(String.valueOf(gridsize), skin);
//        label2.setPosition(0, yy+20);
//        stage.addActor(label2);
//        slider.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                gridsize = (int)slider.getValue();
//                label2.setText(String.valueOf(gridsize));
//                refresh();
//            }
//        });
//        yy-= 30;
//
//        // perlin xoffset
//        final Slider sliderX = new Slider(0, 4, 0.01f, false, skin);
//        sliderX.setAnimateDuration(0.1f);
//        sliderX.setValue(xoffset);
//        sliderX.setSize(150, 20);
//        sliderX.setPosition(100, yy);
//        stage.addActor(sliderX);
//        final Label labelx1 = new Label("xoffset", skin);
//        labelx1.setPosition(0, yy);
//        stage.addActor(labelx1);
//        final Label labelx2 = new Label(String.valueOf(xoffset), skin);
//        labelx2.setPosition(0, yy+20);
//        stage.addActor(labelx2);
//        sliderX.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                xoffset = sliderX.getValue();
//                labelx2.setText(String.valueOf(xoffset));
//                refresh();
//            }
//        });
//        yy-= 30;
//
//        // octaves
//        final Slider octavesSlider = new Slider(1, 12, 1, false, skin);
//        octavesSlider.setAnimateDuration(0.1f);
//        octavesSlider.setValue(Noise.octaves);
//        octavesSlider.setSize(150, 20);
//        octavesSlider.setPosition(100, yy);
//        stage.addActor(octavesSlider);
//        final Label label3 = new Label("octaves", skin);
//        label3.setPosition(0, yy);
//        stage.addActor(label3);
//        final Label label4 = new Label(String.valueOf(Noise.octaves), skin);
//        label4.setPosition(0, yy+20);
//        stage.addActor(label4);
//        octavesSlider.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                Noise.octaves = (int)octavesSlider.getValue();
//                label4.setText(String.valueOf(Noise.octaves));
//                refresh();
//            }
//        });
//        yy-= 30;
//
//        // persistence
//        final Slider persSlider = new Slider(.1f, 1.0f, 0.05f, false, skin);
//        persSlider.setAnimateDuration(0.1f);
//        persSlider.setValue(Noise.persistence);
//        persSlider.setSize(150, 20);
//        persSlider.setPosition(100, yy);
//        stage.addActor(persSlider);
//        final Label label5 = new Label("persistence", skin);
//        label5.setPosition(0, yy);
//        stage.addActor(label5);
//        final Label label6 = new Label(String.valueOf(Noise.persistence), skin);
//        label6.setPosition(0, yy+20);
//        stage.addActor(label6);
//        persSlider.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                Noise.persistence = persSlider.getValue();
//                label6.setText(String.valueOf(Noise.persistence));
//                refresh();
//            }
//        });
//        yy-= 30;
//
//        // amplitude
//        final Slider maxSlider = new Slider(1f, 500f, 10f, false, skin);
//        maxSlider.setAnimateDuration(0.1f);
//        maxSlider.setValue(maxHeight);
//        maxSlider.setSize(150, 20);
//        maxSlider.setPosition(100, yy);
//        stage.addActor(maxSlider);
//        final Label label7 = new Label("amplitude", skin);
//        label7.setPosition(0, yy);
//        stage.addActor(label7);
//        final Label label8 = new Label(String.valueOf(maxHeight), skin);
//        label8.setPosition(0, yy+20);
//        stage.addActor(label8);
//        maxSlider.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                maxHeight = maxSlider.getValue();
//                label8.setText(String.valueOf(maxHeight));
//                refresh();
//            }
//        });
//        yy-= 30;


    }





    private void refresh() {
        //main.regenerate();
    }

    public void resize (int width, int height) {
          stage.getViewport().update(width, height, true);
            // TODO ensure actors stay at top left
    }

    public void render( float delta ) {
        fpsLabel.setText(Gdx.graphics.getFramesPerSecond());
        stage.act(delta);
        stage.draw();
    }

    public void dispose () {

        stage.dispose();

    }
}
