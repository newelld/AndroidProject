package com.example.demo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import android.content.Context;
import android.util.FloatMath;
import android.util.Log;

public class OffScanner {
    private final String TAG = getClass().getName();
    private Context ctx;
    private float[] vertices, normals;
    private short[] index;
    
    public OffScanner (Context c)
    {
        ctx = c;
    }
    
    public float[] getVertexArray() {
        return vertices;
    }
    
    public float[] getNormalArray() {
        return normals;
    }
    
    public short[] getIndexArray() {
        return index;
    }
    
    public void read (String name)
    {
        try {
            InputStream ist = ctx.getAssets().open(name);
            Scanner inp = new Scanner (ist);
            inp.nextLine();  /* read the tag */
            int vertexCount = inp.nextInt();
            int faceCount = inp.nextInt();
            int edgeCount = inp.nextInt();
            inp.nextLine();
            vertices = new float[vertexCount * 3];
            normals = new float[vertexCount * 3];
            index = new short [faceCount * 3];
            for (int k = 0; k < vertexCount; k++)
            {
                String[] toks = inp.nextLine().trim().split(" +");
                for (int m = 0; m < 3; m++)
                    vertices[3*k + m] = Float.parseFloat(toks[m].trim());
            }
            int triang[] = new int[3];
            for (int k = 0; k < faceCount; k++) {
                String[] toks = inp.nextLine().trim().split(" +");
                int nside = Integer.parseInt(toks[0]);

                if (nside != 3 || toks.length != 4) {
                    throw new IllegalArgumentException("Not a triangular mesh");
                }
                
                for (int m = 0; m < nside; m++) {
                    index [3 * k + m] = Short.parseShort(toks[m+1]);
                    triang[m] = index [3* k + m];
                }
                calcNormal (triang);
            }
        } catch (IOException e) {
            Log.e (TAG, "IOException when reading " + name + " " + 
                    e.getMessage());
        }
    }
    
    private void calcNormal (int[] t)
    {
        /*
         * t[0] is the vertex id of the first point in the triangle
         * vertices [3 * t[0]    ] is the x-coord of that point
         * vertices [3 * t[0] + 1] is the y-coord of that point
         * vertices [3 * t[0] + 2] is the z-coord of that point
         */
        float ax = vertices[3*t[1]] - vertices[3*t[0]];
        float bx = vertices[3*t[2]] - vertices[3*t[0]];
        float ay = vertices[3*t[1] + 1] - vertices[3*t[0] + 1];
        float by = vertices[3*t[2] + 1] - vertices[3*t[0] + 1];
        float az = vertices[3*t[1] + 2] - vertices[3*t[0] + 2];
        float bz = vertices[3*t[2] + 2] - vertices[3*t[0] + 2];
        float nx = ay*bz - az*by;
        float ny = az*bx - ax*bz;
        float nz = ax*by - ay*bx;
        float len = FloatMath.sqrt(nx*nx + ny*ny + nz*nz);
        for (int pid : t) {
            normals[3 * pid + 0] = nx/len;
            normals[3 * pid + 1] = ny/len;
            normals[3 * pid + 2] = nz/len;
        }
    }
}
