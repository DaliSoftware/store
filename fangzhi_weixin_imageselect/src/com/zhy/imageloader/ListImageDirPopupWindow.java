package com.zhy.imageloader;

import java.util.List;

import android.app.Activity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.zhy.bean.ImageFloder;
import com.zhy.utils.BasePopupWindowForListView;
import com.zhy.utils.CommonAdapter;
import com.zhy.utils.ViewHolder;

public class ListImageDirPopupWindow extends BasePopupWindowForListView<ImageFloder>
{
	private ListView mListDir;
	private CommonAdapter adapter;
	
	public ListImageDirPopupWindow(int width, int height,
			List<ImageFloder> datas, View convertView)
	{
		super(convertView, width, height, true, datas);
	}

	@Override
	public void initViews()
	{
		
		mListDir = (ListView) findViewById(R.id.id_list_dir);
		
		
		adapter = new CommonAdapter<ImageFloder>(context, mDatas,
				R.layout.list_dir_item)
		{
			@Override
			public void convert(ViewHolder helper, ImageFloder item)
			{
				helper.setText(R.id.id_dir_item_name, item.getName());
				helper.setImageByUrl(R.id.id_dir_item_image,
						item.getFirstImagePath());
				helper.setText(R.id.id_dir_item_count, item.getCount() + "张");
			}
		};
		mListDir.setAdapter(adapter);
	}

	public interface OnImageDirSelected
	{
		void selected(ImageFloder floder);
	}

	private OnImageDirSelected mImageDirSelected;

	public void setOnImageDirSelected(OnImageDirSelected mImageDirSelected)
	{
		this.mImageDirSelected = mImageDirSelected;
	}

	@Override
	public void initEvents()
	{
		mListDir.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{

				if (mImageDirSelected != null)
				{
					for(int i = 0; i < adapter.getCount(); i ++){
						View v2 = adapter.getView(i, null, mListDir);
						ImageView iv = (ImageView)v2.findViewById(R.id.id_dir_choose);
						if(! v2.equals(view)){
							//iv.setVisibility(View.GONE);
							iv.setImageResource(R.drawable.ic_launcher);
						}else{
							iv.setVisibility(View.VISIBLE);
						}
					}
/*					ImageView iv = (ImageView)view.findViewById(R.id.id_dir_choose);
					iv.setImageResource(R.drawable.ic_launcher);*/
					//xuanzheDangqian(view);
					mImageDirSelected.selected(mDatas.get(position));
				}
			}
		});
	}
	
	private void xuanzheDangqian(View v){
		
	}
	

	@Override
	public void init()
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void beforeInitWeNeedSomeParams(Object... params)
	{
		// TODO Auto-generated method stub
	}

}
