package editor;

import Listener.MouseListener;
import components.NonPickable;
import imgui.flag.ImGuiWindowFlags;
import imgui.internal.ImGui;
import jade.GameObject;
import renderer.PickingTexture;
import scenes.Scene;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow {
    private GameObject activeGameObject = null;
    private PickingTexture pickingTexture;

    private float debounce = 0.2f;

    private GameViewWindow gameViewWindow = new GameViewWindow();

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.pickingTexture = pickingTexture;
    }

    public void update(float dt, Scene currentScene) {
        debounce -= dt;

        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0) {
                // FILTRO 2: ¿Está el ratón dentro de los límites del GameViewPort?
            if (gameViewWindow.isMouseInsideViewport()) {
                int x = (int) MouseListener.getScreenX();
                int y = (int) MouseListener.getScreenY();
                int gameObjectId = pickingTexture.readPixel(x, y);
                GameObject pickedObj = currentScene.getGameObject(gameObjectId);
                if (pickedObj != null && pickedObj.getComponent(NonPickable.class) == null) {
                    activeGameObject = pickedObj;
                } else if (pickedObj == null && !MouseListener.isDragging()) {
                    activeGameObject = null;
                }
                this.debounce = 0.2f;
            }
        }
    }

    public void imgui(){
        if (activeGameObject != null) {
            ImGui.begin("Inspector", ImGuiWindowFlags.NoFocusOnAppearing);
            activeGameObject.imgui();
            ImGui.end();
        }
    }

    public GameObject getActiveGameObject() {
        return activeGameObject;
    }
}
