package com.dali.store.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dali.store.R;

public class ButtonItemView extends RelativeLayout{
	private TextView tvLabel;
	private TextView tvBixuan;
	private TextView tvValue;
	
	private static final String ATTR_NAME_LABEL = "label";
	private static final String ATTR_NAME_HINT = "hint";
	private static final String ATTR_NAME_BIXUAN = "bixuan";
	
	
	public ButtonItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.button_item, this);
		tvLabel = (TextView) findViewById(R.id.tv_label);
		tvBixuan = (TextView) findViewById(R.id.tv_bixuan);
		tvValue = (TextView) findViewById(R.id.tv_value);
		initViewText(attrs);
	}

	private void initViewText(AttributeSet attrs){
		int count = attrs.getAttributeCount();  
        for (int index = 0; index < count; index++) {  
            String attributeName = attrs.getAttributeName(index);  
            String attributeValue = attrs.getAttributeValue(index);  
            if(ATTR_NAME_LABEL.equals(attributeName)){
            	tvLabel.setText(attributeValue);
            }else if(ATTR_NAME_HINT.equals(attributeName)){
            	tvValue.setHint(attributeValue);
            }else if(ATTR_NAME_BIXUAN.equals(attributeName)){
            	tvBixuan.setText(attributeValue);
            }
        }  
	}
	
}
