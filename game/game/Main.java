package game;

/**
 *
 * @author Owner
 */


import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class Main {

	// The window handle
	public static long window;
        public static Scene mainScene;
        
        boolean hasCapabilities = false;
                
	public void run() {

		init();
		loop();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
                
	}

	private void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if ( !glfwInit() )
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(1500, 1500, "Game Test", NULL, NULL);
		if ( window == NULL )
			throw new RuntimeException("Failed to create the GLFW window");

		// Get the thread stack and push a new frame
		try ( MemoryStack stack = stackPush() ) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);
			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
			// Center the window
			glfwSetWindowPos(
                                window,
				(vidmode.width() - pWidth.get(0))/2,
				(vidmode.height() - pHeight.get(0))/2
			);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(2);
                glfwSetWindowSizeLimits(window, GLFW_DONT_CARE, GLFW_DONT_CARE, GLFW_DONT_CARE, GLFW_DONT_CARE);
                
                //Resize Window Event
                glfwSetWindowSizeCallback(window, new GLFWWindowSizeCallback() {
                    @Override
                    public void invoke(long window, int w, int h) {
                        if(hasCapabilities){
                            glViewport(0, 0, w, h);
                            mainScene.view.aspect=(float)w/h;
                            mainScene.view.updatePerspective();
                        }
                        InputManager.windowWidth = w;
                        InputManager.windowHeight = h;
                    }
                });
                
                //Key Event
                glfwSetKeyCallback(window, new GLFWKeyCallback() {
                    @Override
                    public void invoke(long window, int key, int scancode, int action, int mod) {
                        InputManager.keymods=mod;
                        if(action == GLFW_PRESS){
                            InputManager.pressAction(key);
                            InputManager.keyStates.put(key, true);
                        }
                        else if(action == GLFW_RELEASE){
                            InputManager.releaseKeyAction(key);
                            InputManager.keyStates.put(key, false);
                        }
                    }
                });
                
                //Cursor Move Event
                glfwSetCursorPosCallback(window, new GLFWCursorPosCallback() {
                    @Override
                    public void invoke(long window, double xpos, double ypos) {
                        InputManager.cursorX=(float)xpos;
                        InputManager.cursorY=(float)ypos;
                        InputManager.cursorMoveAction();
                    }
                });
                
                //Cursor Click Event
                glfwSetMouseButtonCallback(window, new GLFWMouseButtonCallback() {
                    @Override
                    public void invoke(long window, int button, int action, int mods) {
                        if(action == GLFW_PRESS){
                            InputManager.clickAction(button, mods);
                        }
                        else if(action == GLFW_RELEASE){
                            InputManager.releaseClickAction(button, mods);
                        }
                    }
                });
                
                
		// Make the window visible
		glfwShowWindow(window);
	}

	private void loop() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();
                hasCapabilities = true;
                
                glEnable(GL_DEPTH_TEST);
                GL32.glProvokingVertex(GL32.GL_FIRST_VERTEX_CONVENTION);
                mainScene = new Scene();
                
            // Run the rendering loop until the user has attempted to close
            // the window or has pressed the ESCAPE key.
            long time=0;
            long delta = 0;
            while (!glfwWindowShouldClose(window)) {
                time = System.currentTimeMillis();
                
                mainScene.update(delta/1000f);
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
                mainScene.draw();
                glfwSwapBuffers(window); // swap the color buffers
                
                // Poll for window events. The key callback above will only be
                // invoked during this call.
                glfwPollEvents();
                if(InputManager.isPressed(GLFW_KEY_ESCAPE))
                    glfwSetWindowShouldClose(window, true);
                        

                delta = System.currentTimeMillis()-time;
            }
            mainScene.unloadAssets();
	}

	public static void main(String[] args) {
		new Main().run();
	}

}
