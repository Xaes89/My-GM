package editor;

import Listener.MouseListener;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import jade.Window;
import org.joml.Vector2f;

public class GameViewWindow {

    public void imgui() {
        ImGui.begin("Game ViewPort", ImGuiWindowFlags.NoScrollbar
                | ImGuiWindowFlags.NoScrollWithMouse);

        ImVec2 windowSize = getLargestSizeForViewport();
        ImVec2 windowPos = getCenteredPositionForViewport(windowSize);

        ImGui.setCursorPos(windowPos.x, windowPos.y);

        ImVec2 topLeft = new ImVec2();
        ImGui.getCursorScreenPos(topLeft);
        topLeft.x -= ImGui.getScrollX();
        topLeft.y -= ImGui.getScrollY();

        int textureId = Window.getFramebuffer().getTextureId();
        ImGui.image(textureId, windowSize.x, windowSize.y, 0,1,1,0);

        MouseListener.setGameViewportPos(new Vector2f(topLeft.x, topLeft.y - 25));
        MouseListener.setGameViewportSize(new Vector2f(windowSize.x, windowSize.y));

        ImGui.end();
    }

    private ImVec2 getLargestSizeForViewport() {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float aspectWidth = windowSize.x;
        float aspectHeight = aspectWidth / Window.getTargetAspectRatio();
        if (aspectHeight > windowSize.y) {
            // We must switch to pillarbox mode
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * Window.getTargetAspectRatio();
        }

        return new ImVec2(aspectWidth, aspectHeight);
    }

    private ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize) {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewportY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);

        return new ImVec2(viewportX + ImGui.getCursorPosX(),
                viewportY + ImGui.getCursorPosY());
    }

    public boolean isMouseInsideViewport() {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize); // El tamaño real del juego

        ImVec2 windowPos = new ImVec2();
        ImGui.getWindowPos(windowPos); // Dónde empieza la ventana de ImGui en la pantalla

        ImVec2 cursorPos = new ImVec2();
        ImGui.getCursorPos(cursorPos); // El margen interno (pestañas, etc.)

        // El "Punto Cero" real de tu juego en la pantalla es:
        float realOriginX = windowPos.x + cursorPos.x;
        float realOriginY = windowPos.y + cursorPos.y;

        // Ahora ajustamos el mouse para que sea RELATIVO a ese origen
        float localMouseX = MouseListener.getScreenX() - realOriginX;
        float localMouseY = MouseListener.getScreenY() - realOriginY;

        return localMouseX >= 8 && localMouseX <= windowSize.x &&
                localMouseY >= (windowSize.y - realOriginY) && localMouseY <= 40;
    }
}
