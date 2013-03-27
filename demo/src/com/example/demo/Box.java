package com.example.demo;

import javax.microedition.khronos.opengles.GL10;
import static android.opengl.GLES10.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Box implements Drawable {
    private FloatBuffer mVertex, mNormal, mTex;
    private ShortBuffer mIdx;
    
    /* the order of vertices below DOES NOT have to be CW or CCW */
    
    public Box(int M, int N)
    {
        ByteBuffer buff;
        /* Allocate buffers from the heap so they won't get wiped out
         * by Java Garbage Collector.
         */
        /* Each vertex stores floating-point date (4 bytes) for (x,y,z) */
        buff = ByteBuffer.allocateDirect(M * N * 12);
        buff.order(ByteOrder.nativeOrder());
        mVertex = buff.asFloatBuffer();

        buff = ByteBuffer.allocateDirect(M * N * 8);
        buff.order(ByteOrder.nativeOrder());
        mTex = buff.asFloatBuffer();

        buff = ByteBuffer.allocateDirect(M * N * 12);
        buff.order(ByteOrder.nativeOrder());
        mNormal = buff.asFloatBuffer();
        
        /* each index is a short integer value (2 bytes) */
        buff = ByteBuffer.allocateDirect((M-1) * (N-1) * 12);
        buff.order(ByteOrder.nativeOrder());
        mIdx = buff.asShortBuffer();
        
        /* place the coordinates and indices into the corresponding buffers */
        float dy = 1f/(M-1);
        float dx = 1f/(N-1);
        float x, y;
        int k, m, n;
        for (m = 0, y = 0.5f; m < M; m++, y -= dy)
        	for (n = 0, x = -0.5f; n < N; n++, x += dx)
        	{
//        		Log.i("Hans GLES", "[" + x + "," + y + "]");
        		//Log.i(TAG, x + " " + y);
        		mVertex.put(x);
        		mVertex.put(y);
        		mVertex.put(0);
        		mTex.put(x);
        		mTex.put(y);
        	}
        
        for (k = 0; k < M * N; k++)
        {
        	mNormal.put(0);
        	mNormal.put(0);
        	mNormal.put(1f);
        }
        
        for (k = 0; k < M - 1; k++)
        {
        	for (m = 0; m < N - 1; m++) {
        		mIdx.put((short) (k * N + m));
        		mIdx.put((short) ((k+1) * N + m));
        		mIdx.put((short) (k * N + m + 1));
        	}
        	for (m = 0; m < N - 1; m++) {
        		mIdx.put((short) (k * N + m + 1));
        		mIdx.put((short) ((k+1) * N + m));
        		mIdx.put((short) ((k+1) * N + m + 1));
        	}
        	                                 
        }
        /* reset the buffer position to the beginning */
        mVertex.position(0);
        mNormal.position(0);
        mTex.position(0);
        mIdx.position(0);
    }
    
    @Override
    public void draw(Object ... data)
    {
        glVertexPointer(
                3 /* number of coordinates per vertex */, 
                GL10.GL_FLOAT          /* type of each vertex */, 
                0,                /* stride */
                mVertex           /* memory location */);
        glNormalPointer(GL10.GL_FLOAT, 0, mNormal);
        glTexCoordPointer(2 /* number of components per vertex */, 
                GL10.GL_FLOAT, 0, mTex);
        glDrawElements(
                GL10.GL_TRIANGLES     /* mode */, 
                mIdx.capacity()   /* number of indices */, 
                GL10.GL_UNSIGNED_SHORT /* type of each index */, 
                mIdx);
    }
}
