package com.dali.store.image;

import java.io.Serializable;
import java.util.List;

import android.graphics.Bitmap;

public class ImageResourceList implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String resourceType;
	public static final String IMAGE_TYPE_URL = "url";
	public static final String IMAGE_TYPE_BITMAP = "bitmap";
	
	private List<Bitmap> bitmapList;
	public ImageResourceList(String resourceType, List<Bitmap> bitmapList){
		this.resourceType = resourceType;
		this.bitmapList = bitmapList;
	}
	
	public List<Bitmap> getImageList(){
		return this.bitmapList;
	}
	
	public String getResourceType(){
		return this.resourceType;
	}

}
