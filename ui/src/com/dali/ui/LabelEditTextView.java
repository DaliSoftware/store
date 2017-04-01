package com.dali.ui;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LabelEditTextView extends RelativeLayout{
	private Context mContext;
	private TextView tvTabel = null;
	private EditText etValue = null;
	public LabelEditTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		//LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LayoutInflater inflater = LayoutInflater.from(context);
		View parentView = inflater.inflate(R.layout.label_edittext, this);
		tvTabel = (TextView) findViewById(R.id.tv_label);
		etValue = (EditText) findViewById(R.id.et_value);
		
		int count = attrs.getAttributeCount();  
        for (int index = 0; index < count; index++) {  
            String attributeName = attrs.getAttributeName(index);  
            String attributeValue = attrs.getAttributeValue(index);  
            if("tv_label".equals(attributeName)){
            	tvTabel.setText(attributeValue);
            }else if("et_hint".equals(attributeName)){
            	etValue.setHint(attributeValue);
            }else if("max_length".equals(attributeName)){
            	etValue.setFilters(new InputFilter[]{
            			new InputFilter.LengthFilter(Integer.parseInt(attributeValue))});
            }
        }  
        
//        init(context, attrs);
	}

	/*
	private void init(Context context, AttributeSet attrs){
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.let);
		for(int i = 0; i < array.getIndexCount(); i ++){
			int itemId = array.getIndex(i);
			switch(itemId){
			case R.styleable.let_et_hint:
				etValue.setHint(array.getString(itemId));
				break;
			case R.styleable.let_tv_label:
				tvTabel.setText(array.getString(itemId));
				break;
			}
		}
	}
	*/
	
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		etValue.setFocusable(true);
		etValue.setFocusableInTouchMode(true);
		etValue.requestFocus();
		
		Timer timer = new Timer();
		timer.schedule(new TimerTask() { //让软键盘延时弹出，以更好的加载Activity
			public void run() {
				Context context = etValue.getContext();
				InputMethodManager inputManager = 
						(InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
				inputManager.showSoftInput(etValue, 0);
			}
		}, 500);
		
		return true;
	}
	
	public void setEditTextValue(String text){
		etValue.setText(text);
	}
	public String getEditTextValue(){
		return etValue.getText().toString();
	}

	public EditText getEtValue() {
		return etValue;
	}
	
}