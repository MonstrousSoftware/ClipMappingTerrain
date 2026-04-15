package com.monstrous.terrain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
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

    public boolean freezeLoD = false;
    public boolean showTerrain = true;
    public boolean showWireFrame = false;
    public boolean culling = true;
    public boolean showCameraPath = false;
    public boolean flyCamera = true;
    public int gridsize = 16;
    public float xoffset = 0;
    public float yoffset = 0;
  //  public int octaves = 5;
  //  public float persistence = 0.45f;
    public float maxHeight = 600f;
    private Label fpsLabel;
    private Label instancesLabel;


    public GUI ( TerrainDemo main ) {

        this.main = main;

        // GUI elements via Stage class
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"));
        stage = new Stage(new ScreenViewport());

        addActors();
    }

    private void addActors() {

        Table controls = new Table();
        controls.left();

        controls.add(new Label("FPS: ", skin)).left();

        fpsLabel = new Label("0", skin);
        controls.add(fpsLabel).left().row();

        controls.add(new Label("ModelInstances: ", skin)).left();

        instancesLabel = new Label("0", skin);
        controls.add(instancesLabel).left().row();

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
        controls.add(checkbox).left().row();

        final CheckBox terrainCheckbox = new CheckBox("show terrain", skin);
        terrainCheckbox.setChecked(showTerrain);
        terrainCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showTerrain = terrainCheckbox.isChecked();
            }
        });
        controls.add(terrainCheckbox).left().row();


        final CheckBox linesCheckbox = new CheckBox("wire frame", skin);
        linesCheckbox.setChecked(showWireFrame);
        linesCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showWireFrame = linesCheckbox.isChecked();
                main.terrain.generateBlocks(showWireFrame ? GL20.GL_LINES : GL20.GL_TRIANGLES);
            }
        });
        controls.add(linesCheckbox).left().row();

        final CheckBox freezeCheckbox = new CheckBox("freeze Level of Detail", skin);
        freezeCheckbox.setChecked(freezeLoD);
        freezeCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                freezeLoD = freezeCheckbox.isChecked();

            }
        });
        controls.add(freezeCheckbox).left().row();

        final CheckBox cullingCheckbox = new CheckBox("frustum culling", skin);
        cullingCheckbox.setChecked(culling);
        cullingCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                culling = cullingCheckbox.isChecked();
                main.terrain.setCulling(culling);
            }
        });
        controls.add(cullingCheckbox).left().row();

        final CheckBox flyCheckbox = new CheckBox("fly camera", skin);
        flyCheckbox.setChecked(flyCamera);
        flyCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                flyCamera = flyCheckbox.isChecked();
            }
        });
        controls.add(flyCheckbox).left().row();

//        final CheckBox terrainTextureCheckbox = new CheckBox("show terrain texture", skin);
//        terrainTextureCheckbox.setChecked(showTerrainTexture);
//        terrainTextureCheckbox.addListener(new ChangeListener() {
//            @Override
//            public void changed(ChangeEvent event, Actor actor) {
//                showTerrainTexture = terrainTextureCheckbox.isChecked();
//            }
//        });
//        controls.add(terrainTextureCheckbox).left().row();




        final CheckBox camPathCheckbox = new CheckBox("camera spline", skin);
        camPathCheckbox.setChecked(showCameraPath);
        camPathCheckbox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                showCameraPath = camPathCheckbox.isChecked();
            }
        });
        controls.add(camPathCheckbox).left().row();



        controls.pack();
        controls.setPosition(0,stage.getHeight()-controls.getHeight());
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






    public void resize (int width, int height) {
          stage.getViewport().update(width, height, true);
            // TODO ensure actors stay at top left
    }

    public void render( float delta ) {
        fpsLabel.setText(Gdx.graphics.getFramesPerSecond());
        instancesLabel.setText(main.terrain.getNumInstances());
        stage.act(delta);
        stage.draw();
    }

    public void dispose () {

        stage.dispose();

    }
}
