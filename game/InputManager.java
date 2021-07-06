package game;

import static game.Main.mainScene;
import static game.Scene.view;
import java.util.HashMap;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;



public final class InputManager {
    public static int keymods;
    public static float cursorX;
    public static float cursorY;
    public static int windowWidth;
    public static int windowHeight;
    public static byte cursorDown = 0x0;
    public static float cursorDepth = 0;
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
        if (key == GLFW.GLFW_KEY_T) {
            Vector3f ray = new Vector3f();
            ray = view.raycast();
            ray.add(view.pos);
            Scene.marker.pos.set(ray);
            Scene.marker.visible=true;
            Scene.marker.markRenderUpdate();
            System.out.println("CLICKY");
        }
    }

    static void releaseKeyAction(int key) {
    }
    
    static void cursorMoveAction() {
        if(!isPressed(GLFW.GLFW_KEY_2)){
        
        Scene.player.rot.y -= (cursorX - windowWidth/ 2) / windowWidth;
        if (Math.abs(Scene.player.rot.x + (cursorY - windowHeight / 2))/ windowWidth < 1f) {
            Scene.player.rot.x -= (cursorY - windowHeight / 2) / windowHeight;
        }
        mainScene.view.updateCameraTransform();
        glfwSetCursorPos(Main.window, windowWidth / 2, windowHeight / 2);
        Scene.player.updateTransform();
        }
        else{
            
        }
    }

}
