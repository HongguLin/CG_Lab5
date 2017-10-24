
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;

import javax.swing.*;
import java.awt.*;

public class ScreenSaverOGL implements GLEventListener {

	/**
	 * ScreenSaverOGL - this is a simple screen saver that uses JOGL 
	 * Eric McCreath 2009,2011,2017
	 * 
	 * You need to include the jogl jar files (gluegen2-rt.jar and jogl2.jar). In
	 * eclipse use "add external jars" in Project->Properties->Libaries
	 * otherwise make certain they are in the class path.  In the current linux 
         * computers there files are in the /usr/share/java directory.
	 * 
         * If you are executing from the command line then something like:
         *   javac -cp .:/usr/share/java/jogl2.jar:/usr/share/java/gluegen2-rt.jar ScreenSaverOGL.java
         *   java -cp .:/usr/share/java/jogl2.jar:/usr/share/java/gluegen2-rt.jar ScreenSaverOGL
         * should work.
         *
	 * You may also need set up the LD_LIBRARY_PATH environment variable. It should point to a
	 * directory that contains the required libraries such as: libgluegen2-rt.so, libjogl_cg.so, libjogl_awt.so,
	 * and libjogl.so. In eclipse this can be done in the "Run Configurations.."
	 * by adding an environment variable.   If you run from the command line then you may need to first run:

            LD_LIBRARY_PATH=/usr/lib/jni
            export LD_LIBRARY_PATH

	 * 
	 */

	JFrame jf;
	GLJPanel gljpanel;
	Dimension dim = new Dimension(800, 600);
	FPSAnimator animator;

	float xpos;
	float xvel;
	Texture gltexture;

	public ScreenSaverOGL() {
		jf = new JFrame();
		gljpanel = new GLJPanel();
		gljpanel.addGLEventListener(this);
         	gljpanel.requestFocusInWindow();
		jf.getContentPane().add(gljpanel);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
		jf.setPreferredSize(dim);
		jf.pack();
		animator = new FPSAnimator(gljpanel, 20);
		xpos = 100.0f;
		xvel = 1.0f;
		animator.start();
	}

	public static void main(String[] args) {
		new ScreenSaverOGL();
	}

	public void drawPolygon(GL2 gl){
		//Polygon
		gl.glRotated(xpos,0.0,1.0,0.0);
		gl.glBegin(gl.GL_POLYGON);
		gl.glVertex3d(0,0,0);
		gl.glVertex3d(10,0,0);
		gl.glVertex3d(10,20,0);
		gl.glVertex3d(0,20,0);
		gl.glEnd();

	}

	public void display(GLAutoDrawable dr) {
		GL2 gl = (GL2) dr.getGL();
		GLU glu = new GLU();
		GLUT glut = new GLUT();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT);
		gl.glColor3f(1.0f, 0.0f, 1.0f);


		gl.glPushMatrix();
		gl.glTranslated(-35,0,0);
		drawPolygon(gl);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslated(35,0,0);
		drawPolygon(gl);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslated(-0,5,0);
		drawPolygon(gl);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glTranslated(0,-25,0);
		drawPolygon(gl);
		gl.glPopMatrix();

		gl.glFlush();

		
		xpos += xvel;
		if (xpos > dim.getWidth())
			xpos = 0.0f;

	}

	public void displayChanged(GLAutoDrawable dr, boolean arg1, boolean arg2) {
	}

	public void init(GLAutoDrawable dr) {
		GL2 gl = dr.getGL().getGL2();
		GLU glu = new GLU();
		GLUT glut = new GLUT();
		gl.glClearColor(1.0f, 1.0f, 1.0f, 0.0f);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		//glu.gluOrtho2D(0.0, dim.getWidth(), 0.0, dim.getHeight());
		gl.glFrustum(-5.0,5.0,-3.0,3.0,10.0,200.0);
		glu.gluLookAt(0.0,20.0,100.0,0.0,0.0,0.0,0.0,1.0,0.0);
		gl.glMatrixMode(GL2.GL_MODELVIEW);

	}

	public void reshape(GLAutoDrawable dr, int arg1, int arg2, int arg3,
			int arg4) {
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
	
	}
}
