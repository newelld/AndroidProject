package com.example.demo;

import static android.opengl.GLES10.*;
import android.content.Context;

public class Droid implements Drawable {
    private final String TAG = getClass().getName();
    private MeshObject torso, head, arm;
    private float headAngle, headSpin, armAngle, armSpin;
    
    public Droid (Context context)
    {
        torso = new MeshObject(context, "cylinder.off");
        head = new MeshObject(context, "half_sphere.off");
        arm = new MeshObject(context, "space_shuttle.off");
        headSpin = 0.5f;
        armSpin = -0.5f;
    }
    
    @Override
    public void draw(Object ... data) {
        boolean dance = false;
        if (data.length >= 1) {
            dance = (Boolean) data[0];
        }
        
        /* draw the head */
        glPushMatrix();
        glTranslatef(0, 0, 2.2f);
        if (dance) {
            headAngle += headSpin;
            if (headAngle > 40.0 || headAngle < -40.0)
                headSpin *= -1;
            glRotatef(headAngle, 0, 0, 1);
        }
        glScalef (1.7f, 1.7f, 1.6f);
        head.draw();
        glPopMatrix();
        
        /* draw the left arm */
        glPushMatrix();
        glTranslatef (0.9f, 0, 1f);
        if (dance) {
            glTranslatef (0, 0, 0.4f);
            armAngle -= armSpin;
            if (armAngle > 0 || armAngle < -90)
                armSpin *= -1;
            glRotatef(armAngle, 1, 0, 0);
            glTranslatef (0, 0, -0.4f);
        }
        glScalef(0.2f, 0.2f, 0.6f);
        arm.draw(); /* left arm */
        glPopMatrix();

        /* draw the right arm */
        glPushMatrix();
        glTranslatef (-0.9f, 0, 1f);
        glScalef(0.2f, 0.2f, 0.6f);
        arm.draw(); /* right arm */
        glPopMatrix();
        
        glTranslatef (0, 0, 1f);
        glScalef(.8f, 0.8f, 1.8f);
//        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        torso.draw();
    }

}
