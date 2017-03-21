package com.dali.store.image;

import java.util.List;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class ImageResourceParcel implements Parcelable{
	private String resourceType;
	public static final String IMAGE_TYPE_URL = "url";
	public static final String IMAGE_TYPE_BITMAP = "bitmap";
	
	private List imageList;
	public ImageResourceParcel(String resourceType, List<Bitmap> bitmapList){
		this.resourceType = resourceType;
		this.imageList = bitmapList;
	}
	
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	public List getImageList(){
		return this.imageList;
	}
	
	public String getResourceType(){
		return this.resourceType;
	}
}
