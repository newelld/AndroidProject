package com.example.demo;

import static android.opengl.GLES10.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class Triangle implements Drawable {

    private final static int NUM_VERTICES = 3;
    
    private FloatBuffer mVertex, mColor, mTex, mNorm;
    private ShortBuffer mIndex;
    /* The order of vertices given below DOES NOT have to be in clockwise
     * nor counter clockwise order. But the order of the indices MUST */
    
    /* our ortographic projection parameters guarantee that the shorter
     * spans [-1.0, 1.0] 
     */
    /* equilateral triangle with unit side and COG at (0.0) */
    private float[] coords = {
        -0.5f, -(float) Math.sqrt(3)/6, 0.0f,  /* lower left */
        +0.5f, -(float) Math.sqrt(3)/6, 0.0f,  /* lower right */
         0.0f,  (float) Math.sqrt(3)/3, 0.0f}; /* top */
    
    private float[] colors = {
    		1f, 0, 0, 1f,
    		0, 1f, 0, 1f,
    		0, 0, 1f, 1f
    };
    /* the indices must be specified in the correct order so the triangle
     * vertices are rendered in CCW order */
    private short[] order = {0, 1, 2};
    private float[] normals = {0,0,1f, 0,0,1f, 0,0,1f};
    
    public Triangle()
    {
        ByteBuffer buff;
        /* Allocate buffers from the heap so they won't get wiped out
         * by Java Garbage Collector.
         */
        
        /* Each vertex stores floating-point data (4 bytes) for (x,y,z) */
        buff = ByteBuffer.allocateDirect(coords.length * 4);
        buff.order(ByteOrder.nativeOrder());
        mVertex = buff.asFloatBuffer();

        buff = ByteBuffer.allocateDirect(colors.length * 4);
        buff.order(ByteOrder.nativeOrder());
        mColor = buff.asFloatBuffer();
        
        buff = ByteBuffer.allocateDirect(normals.length * 4);
        buff.order(ByteOrder.nativeOrder());
        mNorm = buff.asFloatBuffer();
        
        /* texture coordinates need two floating-point values (s,t) */ 
        buff = ByteBuffer.allocateDirect(NUM_VERTICES * 2 * 4);
        buff.order(ByteOrder.nativeOrder());
        mTex = buff.asFloatBuffer();
        
        /* each index is a short integer value (2 bytes) */
        buff = ByteBuffer.allocateDirect(NUM_VERTICES * 2);
        buff.order(ByteOrder.nativeOrder());
        mIndex = buff.asShortBuffer();
        
        /* place the coordinates and indices into the corresponding buffers */
        mVertex.put(coords);
        mNorm.put(normals);
        mColor.put(colors);
        
        for (int k = 0; k < NUM_VERTICES; k++) {
            mTex.put(coords[3*k]);
            mTex.put(-coords[3*k + 1]);
        }
        
        /* the order of vertices is already CCW */
        mIndex.put(order);
        
        /* reset the buffer position to the beginning */
        mVertex.position(0);
        mColor.position(0);
        mNorm.position(0);
        mTex.position(0);
        mIndex.position(0);
    }
    
    @Override
    public void draw(Object ... data)
    {
        glVertexPointer(
                3           /* number of coordinates per vertex */, 
                GL_FLOAT    /* type of each vertex */, 
                0           /* stride */,
                mVertex     /* memory location */);
        glColorPointer(4, GL_FLOAT, 0, mColor);
        glNormalPointer(GL_FLOAT, 0, mNorm);
        glTexCoordPointer(2 /* number of components per vertex */, 
                GL_FLOAT, 0, mTex);
        glDrawElements(
                GL_TRIANGLES, /* mode */ 
                order.length       /* number of elements */, 
                GL_UNSIGNED_SHORT  /* type of each index */, 
                mIndex);
    }
}