package com.zd.miko.riji.MVP;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class EditData implements Parcelable {
    public String inputStr;
    public String imagePath;
    public Bitmap bitmap;

    public EditData() {
    }

    public EditData(String inputStr, String imagePath, Bitmap bitmap) {
        this.inputStr = inputStr;
        this.imagePath = imagePath;
        this.bitmap = bitmap;
    }

    public String getInputStr() {
        return inputStr;
    }

    public void setInputStr(String inputStr) {
        this.inputStr = inputStr;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public int describeContents() {
        return 0;
    }


    public void writeToParcel(Parcel out, int flags) {
        out.writeString(inputStr);
        out.writeString(imagePath);

        out.writeParcelable(bitmap, flags);
    }

    public static final Parcelable.Creator<EditData> CREATOR
            = new Parcelable.Creator<EditData>() {
        public EditData createFromParcel(Parcel in) {
            return new EditData(in);
        }

        public EditData[] newArray(int size) {
            return new EditData[size];
        }
    };

    private EditData(Parcel in) {
        inputStr = in.readString();
        imagePath = in.readString();
        bitmap = in.readParcelable(null); // 这个地方可
    }
}