/* $Id: $ */
package com.oreilly.demo.android.pa.viewdemo.widget;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.os.SystemClock;
import android.util.AttributeSet;

import com.example.android.apis.graphics.GLSurfaceView;
import com.oreilly.demo.android.pa.viewdemo.R;


/**
 * Trivial OpenGL widget
 * Based on Google's Triangle API Demo
 * and the GLSurfaceView
 */
public class GLDemoWidget extends GLSurfaceView
    implements GLSurfaceView.Renderer
{
    private final Context context;
    private final FloatBuffer vertexBuf;
    private final FloatBuffer textureBuf;
    private final ShortBuffer indexBuf;

    /**
     * @param context the app context
     * @param attrs the view attributes
     */
    public GLDemoWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        setRenderer(this);

        float[] coords = {
           -1.0f,  0.7f,  1.0f,
           -1.0f, -0.3f, -1.0f,
            0.0f,  0.7f,  1.0f,
            1.0f, -0.3f, -1.0f,
            1.0f,  0.7f,  1.0f,
        };

        // The points defining the shape have to be
        // somewhere safe from the GC
        ByteBuffer vbb = ByteBuffer.allocateDirect(5 * 3 * 4);
        vbb.order(ByteOrder.nativeOrder());
        vertexBuf = vbb.asFloatBuffer();
        for (int i = 0; i < 5; i++) {
            for(int j = 0; j < 3; j++) {
                vertexBuf.put(coords[i * 3 + j] * 2.0f);
            }
        }
        vertexBuf.position(0);

        ByteBuffer tbb = ByteBuffer.allocateDirect(5 * 2 * 4);
        tbb.order(ByteOrder.nativeOrder());
        textureBuf = tbb.asFloatBuffer();
        for (int i = 0; i < 5; i++) {
            for(int j = 0; j < 2; j++) {
                textureBuf.put(coords[i * 3 + j] * 2.0f + 0.5f);
            }
        }
        textureBuf.position(0);

        ByteBuffer ibb = ByteBuffer.allocateDirect(5 * 2);
        ibb.order(ByteOrder.nativeOrder());
        indexBuf = ibb.asShortBuffer();
        for(int i = 0; i < 5; i++) {
            indexBuf.put((short) i);
        }
        indexBuf.position(0);
     }

    /**
     * We don't need a depth buffer and don't care about our color depth.
     */
    @Override
    public int[] getConfigSpec() {
         return new int[] {
                EGL10.EGL_DEPTH_SIZE,
                0,
                EGL10.EGL_NONE
        };
    }

    /**
     * Handle change of size.
     */
    @Override
    public void sizeChanged(GL10 gl, int w, int h) {
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        float ratio = (float) w / h;
        gl.glFrustumf(-ratio, ratio, -1, 1, 3, 7);
    }

    /**
     * Create some big objects that we want
     * around only when we're drawing.
     */
    @Override
    public void surfaceCreated(GL10 gl) {
        // set up the surface
        gl.glDisable(GL10.GL_DITHER);

        gl.glHint(
            GL10.GL_PERSPECTIVE_CORRECTION_HINT,
            GL10.GL_FASTEST);

        gl.glClearColor(0.2f, 0.1f, 0.8f, 0.1f);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);

        // fetch the checker-board
        initImage(gl);
    }

    /**
     * Draw the moving checker-board.
     */
    @Override
    public void drawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();

        GLU.gluLookAt(gl, 0, 0, -5, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

        // apply the checker-board to the shape
        gl.glActiveTexture(GL10.GL_TEXTURE0);

        gl.glTexEnvx(
            GL10.GL_TEXTURE_ENV,
            GL10.GL_TEXTURE_ENV_MODE,
            GL10.GL_MODULATE);
        gl.glTexParameterx(
            GL10.GL_TEXTURE_2D,
            GL10.GL_TEXTURE_WRAP_S,
            GL10.GL_REPEAT);
        gl.glTexParameterx(
            GL10.GL_TEXTURE_2D,
            GL10.GL_TEXTURE_WRAP_T,
            GL10.GL_REPEAT);

        // animation
        int t = (int) (SystemClock.uptimeMillis() % (10 * 1000L));
        gl.glTranslatef(6.0f - (0.0013f * t), 0, 0);

        // draw
        gl.glFrontFace(GL10.GL_CCW);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuf);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuf);
        gl.glDrawElements(
            GL10.GL_TRIANGLE_STRIP,
            5,
            GL10.GL_UNSIGNED_SHORT, indexBuf);
    }

    /*
     * Get the checker-board image and make it a texture.
     */
    private void initImage(GL10 gl) {
        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

        gl.glTexParameterf(
            GL10.GL_TEXTURE_2D,
            GL10.GL_TEXTURE_MIN_FILTER,
            GL10.GL_NEAREST);
        gl.glTexParameterf(
            GL10.GL_TEXTURE_2D,
            GL10.GL_TEXTURE_MAG_FILTER,
            GL10.GL_LINEAR);
        gl.glTexParameterf(
            GL10.GL_TEXTURE_2D,
            GL10.GL_TEXTURE_WRAP_S,
            GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(
            GL10.GL_TEXTURE_2D,
            GL10.GL_TEXTURE_WRAP_T,
            GL10.GL_CLAMP_TO_EDGE);
        gl.glTexEnvf(
            GL10.GL_TEXTURE_ENV,
            GL10.GL_TEXTURE_ENV_MODE,
            GL10.GL_REPLACE);

        InputStream in
            = context.getResources().openRawResource(R.drawable.cb);
        Bitmap image;
        try { image = BitmapFactory.decodeStream(in); }
        finally {
            try { in.close(); } catch(IOException e) {  }
        }

        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, image, 0);

        image.recycle();
    }
}
