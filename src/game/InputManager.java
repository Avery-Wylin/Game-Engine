package game;

import static game.Main.mainScene;
import java.util.HashMap;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;



public final class InputManager {
    public static int keymods;
    public static float cursorX;
    public static float cursorY;
    public static int width;
    public static int height;
    public static byte cursorDown = 0x0;
    public static HashMap<Integer,Boolean> keyStates = new HashMap<>();
    
    public static boolean isPressed(int key){
        if(!keyStates.containsKey(key))
            return false;
        return keyStates.get(key);
    }
    
    public static void clickAction(int button, int mods){
        cursorDown|=(0x1<<button);
        
    }
    
    public static void releaseClickAction(int button, int mods){
        cursorDown&=(~(0x1<<button));
        
    }
    
    public static void pressAction(int key){
        Scene.player.input(key);
    }

    static void releaseKeyAction(int key) {
    }
    
    static void cursorMoveAction() {
        Scene.player.rot.y -= (cursorX - width/ 2) / width;
        if (Math.abs(Scene.player.rot.x - (cursorY - height / 2))/ width < 1f) {
            Scene.player.rot.x -= (cursorY - height / 2) / height;
        }
        mainScene.view.updateCameraTransform();
        glfwSetCursorPos(Main.window, width / 2, height / 2);
        Scene.player.updateTransform();
    }

}
