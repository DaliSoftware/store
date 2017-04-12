package com.dali.ui;

import java.util.ArrayList;
import java.util.List;

import com.zhy.imageloader.MyAdapter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ImageManageGridView extends RelativeLayout{
	private String sucaikuPath;
	
	
	private List<String> imagesPath = new ArrayList<String>();
	
	private GridView gridView;
	private MyAdapter mAdapter;
	
	
	public ImageManageGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		LayoutInflater.from(context).inflate(R.layout.image_manage_grid_view, this);
		gridView = (GridView) findViewById(R.id.id_gridView);
		
		sucaikuPath = context.getCacheDir().getAbsolutePath() + "/sucai";
		
		mAdapter = new MyAdapter(context, imagesPath,
				R.layout.image_manage_grid_view_item, sucaikuPath);
		
		gridView.setAdapter(mAdapter);
		ImageView selImageView = new ImageView(context);
		selImageView.setImageResource(R.drawable.ui_upload_image_128px);
		gridView.addView(selImageView);
	}
	
	
	
	
}
