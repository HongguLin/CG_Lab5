import java.awt.Dimension;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.swing.JFrame;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.PMVMatrix;
import com.jogamp.opengl.util.gl2.GLUT;


public class ShaderExampleMatrix implements GLEventListener {

	/**
	 * ShaderExample - this is a simple example for drawing a coloured triangle using a shader 
	 * Eric McCreath 2009, 2011, 2015, 2017
	 * 
	 *
	 * 
	 */

	JFrame jf;
//	GLCanvas canvas;
	GLProfile profile;
	GLJPanel gljpanel;
	
	GLCapabilities caps;
	Dimension dim = new Dimension(800, 600);
	FPSAnimator animator;

	
	float angle, scale;
	
	PMVMatrix matrix;
	

	int shaderprogram, vertexshader, fragshader;
	int vertexbuffer[];
	int colorbuffer[];

	public ShaderExampleMatrix() {
		jf = new JFrame();
		profile = GLProfile.getDefault();
		caps = new GLCapabilities(profile);
		gljpanel = new GLJPanel();
		gljpanel.addGLEventListener(this);
		gljpanel.requestFocusInWindow();
		jf.getContentPane().add(gljpanel);
		jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jf.setVisible(true);
		jf.setPreferredSize(dim);
		jf.pack();
		animator = new FPSAnimator(gljpanel, 20);
		
		angle = 0.0f;
		scale = 1.1f;
		
		animator.start();
	}

	public static void main(String[] args) {
		new ShaderExampleMatrix();
	}

	/*
	static final String vertstr[] = { 
		      "#version 330 core\n" + 
	          "in vec3 aPos;\n" + 
			  "in vec3 color;\n" + 
	          "uniform mat4 mvMat, pMat;\n" +
	          "out vec4 vertex_color;\n" + 
	          "vec4 mc;\n" +
			  "void main() {\n" + 
	          "    vertex_color = vec4(color,1.0);\n" +
			  "    mc = vec4(aPos.x,aPos.y,aPos.z,1.0);\n" + 
			  "    gl_Position = (pMat * mvMat) * mc;\n" + 
	          "}\n" };
	 */

	static final String vertstr[] = {
			//"#version 330 core\n" +
					"attribute vec3 aPos;\n" +
					"attribute vec3 color;\n" +
					"uniform mat4 mvMat, pMat;\n" +
					"varying vec4 vertex_color;\n" +
					"varying vec4 mc;\n" +
					"void main() {\n" +
					"    vertex_color = vec4(color,1.0);\n" +
					"    mc = vec4(aPos.x,aPos.y,aPos.z,1.0);\n" +
					"    gl_Position = (pMat * mvMat) * mc;\n" +
					"}\n" };

	static int vlens[] = new int[1];
	static int flens[] = new int[1];

	/*
	static final String fragstr[] = { 
		      "#version 330 core\n"
			+ "out vec4 FragColor;\n"
		    + "in vec4 vertex_color;\n"
			+ "void main() {\n" 
		   // + "   FragColor = vec4(sin(vertex_color.x*100.0),sin(vertex_color.y*60.0),vertex_color.z,1.0);\n"
		    + "   FragColor = vec4(vertex_color.x,vertex_color.y,vertex_color.z,1.0);\n"
			+ "}\n" };
    */

	static final String fragstr[] = {
			//"#version 330 core\n"
			"varying vec4 FragColor;\n"
					+ "varying vec4 vertex_color;\n"
					+ "void main() {\n"
					// + "   FragColor = vec4(sin(vertex_color.x*100.0),sin(vertex_color.y*60.0),vertex_color.z,1.0);\n"
					+ "   gl_FragColor = vec4(vertex_color.x,vertex_color.y,vertex_color.z,1.0);\n"
					+ "}\n" };

	public void init(GLAutoDrawable dr) { // set up openGL for 2D drawing
		GL2 gl2 = dr.getGL().getGL2();
		GLU glu = new GLU();
		GLUT glut = new GLUT();
		
		
		
		matrix = new PMVMatrix();
		// setup and load the vertex and fragment shader programs
		
		matrix.glMatrixMode(GL2.GL_PROJECTION);
		//matrix.glOrthof(-2.0f, 2.0f, -2.0f, 2.0f, -1.0f, 1.0f);
		matrix.glFrustumf(-5.0f, 5.0f, -3.0f, 3.0f, 5.0f, 200.0f);
		matrix.glMatrixMode(GL2.GL_MODELVIEW);
		matrix.gluLookAt(0.0f, 20.0f, 100.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f);
		
		shaderprogram = gl2.glCreateProgram();

		vertexshader = gl2.glCreateShader(GL2.GL_VERTEX_SHADER);
		vlens[0] = vertstr[0].length();
		gl2.glShaderSource(vertexshader, 1, vertstr, vlens, 0);
		
		gl2.glCompileShader(vertexshader);
		
		checkok(gl2, vertexshader, GL2.GL_COMPILE_STATUS);
		gl2.glAttachShader(shaderprogram, vertexshader);

		fragshader = gl2.glCreateShader(GL2.GL_FRAGMENT_SHADER);
		flens[0] = fragstr[0].length();
		gl2.glShaderSource(fragshader, 1, fragstr, flens, 0);
		gl2.glCompileShader(fragshader);
	
		checkok(gl2, fragshader, GL2.GL_COMPILE_STATUS);
		gl2.glAttachShader(shaderprogram, fragshader);

		gl2.glLinkProgram(shaderprogram);

		checkok(gl2, shaderprogram, GL2.GL_LINK_STATUS);

		gl2.glValidateProgram(shaderprogram);
		checkok(gl2, shaderprogram, GL2.GL_VALIDATE_STATUS);

		gl2.glUseProgram(shaderprogram);

		gl2.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		
		// load the vertex and color data for drawing the triangle

		/*
		float[] triangleArray = { -1.0f, -0.5f, 0.0f,1.0f, -1.0f, 0.0f,
				0.0f, 1.0f, 0.0f
				 };
				 */

		float[] triangleArray = new float[9*4+18*4];
		float r = 30;
		for(int i=0;i<4;i++){
			triangleArray[i*9]=0;
			triangleArray[i*9+1]=10;
			triangleArray[i*9+2]=0;

			triangleArray[i*9+3]=r*(float)Math.sin(2*i*Math.PI/4);
			triangleArray[i*9+4]=0;
			triangleArray[i*9+5]=r*(float)Math.cos(2*i*Math.PI/4);

			triangleArray[i*9+6]=r*(float)Math.sin(2*(i+1)*Math.PI/4);
			triangleArray[i*9+7]=0;
			triangleArray[i*9+8]=r*(float)Math.cos(2*(i+1)*Math.PI/4);
		}

		for(int i=0;i<4;i++){
			triangleArray[i*18+36]=r*(float)Math.sin(2*i*Math.PI/4);
			triangleArray[i*18+1+36]=0;
			triangleArray[i*18+2+36]=r*(float)Math.cos(2*i*Math.PI/4);

			triangleArray[i*18+3+36]=r*(float)Math.sin(2*(i+1)*Math.PI/4);
			triangleArray[i*18+4+36]=0;
			triangleArray[i*18+5+36]=r*(float)Math.cos(2*(i+1)*Math.PI/4);

			triangleArray[i*18+6+36]=r*(float)Math.sin(2*(i+1)*Math.PI/4);
			triangleArray[i*18+7+36]=-30;
			triangleArray[i*18+8+36]=r*(float)Math.cos(2*(i+1)*Math.PI/4);

			triangleArray[i*18+9+36]=r*(float)Math.sin(2*i*Math.PI/4);
			triangleArray[i*18+10+36]=0;
			triangleArray[i*18+11+36]=r*(float)Math.cos(2*i*Math.PI/4);

			triangleArray[i*18+12+36]=r*(float)Math.sin(2*i*Math.PI/4);
			triangleArray[i*18+13+36]=-30;
			triangleArray[i*18+14+36]=r*(float)Math.cos(2*i*Math.PI/4);

			triangleArray[i*18+15+36]=r*(float)Math.sin(2*(i+1)*Math.PI/4);
			triangleArray[i*18+16+36]=-30;
			triangleArray[i*18+17+36]=r*(float)Math.cos(2*(i+1)*Math.PI/4);

		}

		FloatBuffer triangleVertexBuffer = Buffers
				.newDirectFloatBuffer(triangleArray);

		/*
		float[] triangleColorArray = { 1.0f, 0.0f, 0.0f, 
				0.0f, 1.0f, 0.0f,
				0.0f, 0.0f, 1.0f };
				*/

		float[] triangleColorArray = {
				0.5f, 0.5f, 0.5f,
				1.0f, 0.0f, 0.0f,
				1.0f, 0.0f, 1.0f,

				0.5f, 0.5f, 0.5f,
				1.0f, 0.0f, 1.0f,
				1.0f, 1.0f, 0.0f,

				0.5f, 0.5f, 0.5f,
				1.0f, 1.0f, 0.0f,
				0.0f, 1.0f, 0.0f,

				0.5f, 0.5f, 0.5f,
				0.0f, 1.0f, 0.0f,
				1.0f, 0.0f, 0.0f,

				0f, 0f, 1f,
				0f, 0f, 1f,
				0f, 0f, 1f,


		};

		FloatBuffer triangleColorBuffer = Buffers
				.newDirectFloatBuffer(triangleColorArray);

		vertexbuffer = new int[1];
		gl2.glGenBuffers(1, vertexbuffer, 0);
		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexbuffer[0]);
		gl2.glBufferData(GL2.GL_ARRAY_BUFFER, (long) triangleArray.length *4,
				triangleVertexBuffer, GL2.GL_STATIC_DRAW);

		colorbuffer = new int[1];
		gl2.glGenBuffers(1, colorbuffer, 0);
		gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, colorbuffer[0]);
		gl2.glBufferData(GL2.GL_ARRAY_BUFFER,
				(long) triangleColorArray.length*4, triangleColorBuffer,
				GL2.GL_STATIC_DRAW);

	}

	public void drawPic(GL2 gl2){



		matrix.glRotatef(angle, 0.1f, 1.0f, 0.0f);


		int mvMatrixID = gl2.glGetUniformLocation(shaderprogram, "mvMat");
		gl2.glUniformMatrix4fv(mvMatrixID, 1, false, matrix.glGetMvMatrixf());

		int pMatrixID = gl2.glGetUniformLocation(shaderprogram, "pMat");
		gl2.glUniformMatrix4fv(pMatrixID, 1, false, matrix.glGetPMatrixf());


		int posVAttrib = gl2.glGetAttribLocation(shaderprogram, "aPos");
		gl2.glEnableVertexAttribArray(posVAttrib);
		//gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexbuffer[0]);
		//gl2.glVertexAttribPointer(posVAttrib, 3, GL2.GL_FLOAT, false, 0, 0);

		int posCAttrib = gl2.glGetAttribLocation(shaderprogram, "color");
		gl2.glEnableVertexAttribArray(posCAttrib);
		//gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, colorbuffer[0]);
		//gl2.glVertexAttribPointer(posCAttrib, 3, GL2.GL_FLOAT, false, 0, 0);

		//gl2.glDrawArrays(GL2.GL_TRIANGLES, 0, 3);

		for(int i=0;i<4;i++){
			gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexbuffer[0]);
			gl2.glVertexAttribPointer(posVAttrib, 3, GL2.GL_FLOAT, false, 0, i*36);
			gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, colorbuffer[0]);
			gl2.glVertexAttribPointer(posCAttrib, 3, GL2.GL_FLOAT, false, 0, i*36);
			gl2.glDrawArrays(GL2.GL_TRIANGLES, 0, 3);
		}

		for(int i=0;i<8;i++){
			gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, vertexbuffer[0]);
			gl2.glVertexAttribPointer(posVAttrib, 3, GL2.GL_FLOAT, false, 0, 4*36+i*36);
			gl2.glBindBuffer(GL2.GL_ARRAY_BUFFER, colorbuffer[0]);
			gl2.glVertexAttribPointer(posCAttrib, 3, GL2.GL_FLOAT, false, 0, 4*36);
			gl2.glDrawArrays(GL2.GL_TRIANGLES, 0, 3);

		}
		gl2.glDisableVertexAttribArray(posVAttrib);
		gl2.glDisableVertexAttribArray(posCAttrib);



	}

	
	
	private void checkok(GL2 gl2, int program, int type) {
		
		IntBuffer intBuffer = IntBuffer.allocate(1);
				
		gl2.glGetProgramiv(program, type, intBuffer);
		
		if (intBuffer.get(0) != GL.GL_TRUE) {
			int[] len = new int[1];
			gl2.glGetProgramiv(program, GL2.GL_INFO_LOG_LENGTH, len, 0);
			if (len[0] != 0) {

				byte[] errormessage = new byte[len[0]];
				gl2.glGetProgramInfoLog(program, len[0], len, 0, errormessage,
						0);
				System.err.println("problem\n" + new String(errormessage));
				gljpanel.destroy();
				jf.dispose();
				System.exit(0);
			}
		}
	}

	public void display(GLAutoDrawable dr) { // clear the screen and draw
												// "Save the Screens"
		GL2 gl2 = dr.getGL().getGL2();
		GLU glu = new GLU();
		GLUT glut = new GLUT();

		
		gl2.glUseProgram(shaderprogram);

	    //gl2.glClear(GL.GL_COLOR_BUFFER_BIT);
		gl2.glEnable(gl2.GL_DEPTH_TEST);
		gl2.glClear(GL.GL_COLOR_BUFFER_BIT|GL.GL_DEPTH_BUFFER_BIT);

	    
	    matrix.glPushMatrix();
	    matrix.glTranslatef(0,35,0);
	    drawPic(gl2);
		matrix.glPopMatrix();


		matrix.glPushMatrix();
		matrix.glTranslatef(-70,10,0);
		drawPic(gl2);
		matrix.glPopMatrix();

		matrix.glPushMatrix();
		matrix.glTranslatef(70,10,0);
		drawPic(gl2);
		matrix.glPopMatrix();

		matrix.glPushMatrix();
		matrix.glTranslatef(0,-20,0);
		drawPic(gl2);
		matrix.glPopMatrix();

		
		
		gl2.glFlush();

		
		angle += 1.0f;
		if (angle > 360.0)
			angle = 0.0f;
		
		
		scale *= 1.003f;
		if (scale > 100.0f)
			scale = 1.1f;
	}

	public void dispose(GLAutoDrawable glautodrawable) {
	}

	public void reshape(GLAutoDrawable dr, int x, int y, int width, int height) {
	}
}
