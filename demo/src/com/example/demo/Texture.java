package com.example.demo;

import static android.opengl.GLES11.*;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

public class Texture {
    private int[] id;
    
    public Texture (Context ctx, int drawableId)
    {
        id = new int[1];
        /* step 1: generate a texture id */
        glGenTextures(1, id, 0);
        
        /* step 2: bind it to a 2D texture */
        glBindTexture(GL_TEXTURE_2D, id[0]);
        
        /* step 3: define magnification / minification filters */
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        
        /* step 4: specify coordinate mapping */
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        
        InputStream texStream = ctx.getResources().openRawResource(drawableId);
        Bitmap img;
        try {
            img = BitmapFactory.decodeStream(texStream);
        }
        finally {
            try {
                texStream.close();
            }
            catch (IOException e) {
                // do nothing
            }
        }
        GLUtils.texImage2D(GL_TEXTURE_2D, 
                0 /* level */, 
                img, 
                0 /* border */);
        img.recycle();
    }
    
    void bind()
    {
        glBindTexture(GL_TEXTURE_2D, id[0]);
        glEnableClientState(GL_TEXTURE_COORD_ARRAY);
    }
    
    void unbind()
    {
        glDisableClientState(GL_TEXTURE_COORD_ARRAY);
//        glBindTexture(GL_TEXTURE_2D, 0);
    }
}
