package com.example.demo;

import android.os.Parcel;
import android.os.Parcelable;

/* this class is made as a Parcelable class so the object can be
 * saved when the activity has to restart
 */
public class TransformationParams implements Parcelable {

	/* class attributes should not be declared as public
	 * but the following fields are declared as public to allow
	 * easier access to each item.
	 */
//    public float transX, transY;
    public float[] litePos = new float[3];
    public float tiltXRot, tiltZRot;
    public float texScale, texTransX, texTransY;
    public float sphTrX, sphTrY; /* sphere translation amount */
    public float roll_x, roll_y;
    public float eyeX, eyeY, eyeZ;
    public float droid_x, droid_y;
    public float[] coa = new float[3];

    @Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
//		dest.writeFloat(transX);
//		dest.writeFloat(transY);
	    dest.writeFloatArray(litePos);
	    dest.writeFloatArray(coa);
		dest.writeFloat(tiltXRot);
		dest.writeFloat(tiltZRot);
		dest.writeFloat(texScale);
		dest.writeFloat(texTransX);
		dest.writeFloat(texTransY);
		dest.writeFloat(sphTrX);
		dest.writeFloat(sphTrY);
		dest.writeFloat(roll_x);
		dest.writeFloat(roll_y);
		dest.writeFloat(eyeX);
		dest.writeFloat(eyeY);
		dest.writeFloat(eyeZ);
		dest.writeFloat(droid_x);
		dest.writeFloat(droid_y);
	}

}
