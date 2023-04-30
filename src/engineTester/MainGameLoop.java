package engineTester;

import java.io.IOException;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import entities.*;
import models.RawModel;
import models.TexturedModel;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

import physics.AirResistanceForce;
import physics.BrakeForce;
import physics.EngineForce;
import physics.RollingFrictionForce;
import renderEngine.DisplayManager;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import renderEngine.OBJLoader;
import socket.CarValues;
import socket.Client;
import socket.config.Config;
import socket.repositories.ConfigRepoImpl;
import socket.repositories.IConfigRepo;
import terrains.Terrain;
import textures.ModelTexture;
import textures.TerrainTexture;
import textures.TerrainTexturePack;

public class MainGameLoop {

    public static void main(String[] args) throws IOException {

        Client client = Client.getInstance();
        client.run();

        IConfigRepo configRepo = new ConfigRepoImpl();
        Config config = configRepo.getConfigData();
        System.out.println(config);

        DisplayManager.createDisplay();
        Loader loader = new Loader();
        //********************************************************//
        int asphalt = loader.loadTexture("asphalt");
        int mud = loader.loadTexture("mud");
        int snow = loader.loadTexture("snow");
        int grass = loader.loadTexture("grass");
        TerrainTexture backGroundTexture = new TerrainTexture(grass);
        TerrainTexture rTexture = new TerrainTexture(asphalt);
        TerrainTexture gTexture = new TerrainTexture(mud);
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("white"));
        TerrainTexture blendMapTexture1 = new TerrainTexture(loader.loadTexture("map1"));
        TerrainTexture blendMapTexture2 = new TerrainTexture(loader.loadTexture("map2"));
        TerrainTexturePack texturePack = new TerrainTexturePack(backGroundTexture,
                rTexture, gTexture, bTexture);
        //********************************************************//
        RawModel model = OBJLoader.loadObjectModel("car", loader);

        TexturedModel staticModel = new TexturedModel(model, new ModelTexture(loader.loadTexture("redColor")));
        Car car1 = new Car(
            staticModel,
            new Vector3f(50, 0, -50),
            0, 0, 0, 15,
            0.34f,
            loader,
            Float.parseFloat(config.getCarMass())
        );
        car1.addForce(new EngineForce(Float.parseFloat(config.getEngineTorque())));
        car1.addForce(new BrakeForce(Float.parseFloat(config.getBrakeCoefficient())));
        car1.addForce(new RollingFrictionForce(Float.parseFloat(config.getRollingFrictionForce())));
        car1.addForce(new AirResistanceForce(
            2,
            new Vector3f(
                Float.parseFloat(config.getAirResistanceX()),
                Float.parseFloat(config.getAirResistanceY()),
                Float.parseFloat(config.getAirResistanceZ())
            )
        ));

        Light light = new Light(new Vector3f(20000, 20000, 0), new Vector3f(1, 1, 1));

        Terrain terrain = new Terrain(0, 0, 4000, loader, texturePack, blendMapTexture1, null);
        Terrain terrain2 = new Terrain(0, -4000, 4000, loader, texturePack, blendMapTexture2, null);
        terrain.setRotY(90);
        terrain2.setRotY(90);
        terrain2.setRotZ(Float.parseFloat(config.getRoadDegree()));

        Camera camera = new Camera(car1);
        MasterRenderer renderer = new MasterRenderer(loader);

        // SEND DATA TO INTERFACE
        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run(){
                Gson gson = new GsonBuilder().create();
                String json = gson.toJson(new CarValues(
                        car1.getCurrentSpeed(),
                        car1.getCurrentAcceleration()
                ));
                client.sendMessage(json);
            }
        },0,1000);

        while (!Display.isCloseRequested()) {
            float x = Float.parseFloat(config.getRollingFrictionForce());
            if((x < 0.2 && x >= 0) || (x >= 0.6)) {
                texturePack.setgTexture(mud);
                texturePack.setBackGroundTexture(grass);
                texturePack.setrTexture(asphalt);
            } else if(x >= 0.2 && x < 0.4) {
                texturePack.setgTexture(mud);
                texturePack.setBackGroundTexture(mud);
                texturePack.setrTexture(mud);
            } else if(x >= 0.4 && x < 0.6) {
                texturePack.setgTexture(grass);
                texturePack.setBackGroundTexture(snow);
                texturePack.setrTexture(snow);
            }

            camera.move();
            car1.move(terrain2);
            renderer.processCar(car1);
            renderer.processTerrain(terrain);
            renderer.processTerrain(terrain2);
            renderer.render(light, camera);
            DisplayManager.updateDisplay();
        }

        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}
