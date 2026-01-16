package jade;

import editor.GameViewWindow;
import editor.MenuBar;
import editor.PropertiesWindow;
import imgui.ImFontAtlas;
import imgui.ImGuiIO;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiConfigFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.flag.ImGuiWindowFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.ImGui;
import imgui.type.ImBoolean;
import renderer.PickingTexture;
import scenes.Scene;

public class ImGuiLayer {

    private long glfwWindow;
    private GameViewWindow gameViewWindow;
    private PropertiesWindow propertiesWindow;
    private MenuBar menuBar;

    public ImGuiLayer(long windowPtr, PickingTexture pickingTexture) {
        this.glfwWindow = windowPtr;
        this.gameViewWindow = new GameViewWindow();
        this.propertiesWindow = new PropertiesWindow(pickingTexture);
        this.menuBar = new MenuBar();
    }

    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();

    public void initImGui() {
        // 1. Crear el contexto (Sigue siendo crítico)
        ImGui.createContext();

        // 2. Configuración básica de IO
        final ImGuiIO io = ImGui.getIO();
        io.setIniFilename("imgui.ini");
        io.addConfigFlags(ImGuiConfigFlags.NavEnableKeyboard |
                ImGuiConfigFlags.DockingEnable |
                ImGuiConfigFlags.ViewportsEnable); // Habilitar teclado


        // 3. INICIALIZACIÓN MODERNA (Aquí ocurre la magia)
        // Esto reemplaza todos los glfwSetKeyCallback, MouseCallback, etc.
        imGuiGlfw.init(glfwWindow, true);
        imGuiGl3.init("#version 330"); // O la versión de GLSL que uses

        // 4. Fuentes (He simplificado la carga básica)
        final ImFontAtlas fontAtlas = io.getFonts();
        fontAtlas.addFontDefault();

        // Si quieres cargar tu fuente personalizada:
        fontAtlas.addFontFromFileTTF("assets/fonts/font.ttf", 32);


        fontAtlas.build(); // Esto genera la textura de la fuente
    }

    public void update(float dt, Scene currentScene) {
        imGuiGlfw.newFrame();
        imGuiGl3.newFrame();
        ImGui.newFrame();
        setupDockSpace();

        ImGui.begin("Text Window");
        ImGui.text("jijo");
        ImGui.end();
        currentScene.imgui();
        gameViewWindow.imgui();

        propertiesWindow.update(dt, currentScene);
        propertiesWindow.imgui();
        menuBar.imgui();
        ImGui.end();
        ImGui.render();

        // 2. Dibuja ImGui en tu ventana principal
        imGuiGl3.renderDrawData(ImGui.getDrawData());

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            // Guardamos el contexto actual de la ventana de Windows
            long backupWindowPtr = org.lwjgl.glfw.GLFW.glfwGetCurrentContext();

            // Estos son los métodos que el error te pedía
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();

            // Restauramos el contexto para que OpenGL no se confunda
            org.lwjgl.glfw.GLFW.glfwMakeContextCurrent(backupWindowPtr);
        }
    }

    private void setupDockSpace() {
        int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking;

        imgui.ImGuiViewport mainViewport = ImGui.getMainViewport();
        ImGui.setNextWindowPos(mainViewport.getPosX(), mainViewport.getPosY(), ImGuiCond.Always);
        ImGui.setNextWindowSize(mainViewport.getSizeX(), mainViewport.getSizeY(), ImGuiCond.Always);
        ImGui.setNextWindowViewport(mainViewport.getID());

        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse |
                ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove |
                ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNav;

        ImGui.begin("DockSpace Demo", new ImBoolean(true), windowFlags);
        ImGui.popStyleVar(2);

        // Dockspace
        ImGui.dockSpace(ImGui.getID("DockSpace"));
    }

    public PropertiesWindow getPropertiesWindow() {
        return this.propertiesWindow;
    }
}
