package com.dali.store.diao;

import java.util.ArrayList;
import java.util.List;

public class Fun{
	private List<Diao> zhan = new ArrayList<Diao>();
	private int index = 0;
	public Fun(Diao d){
		zhan.add(d);
	}
	
	
	//向队列中添加一个diao
	public void add(Diao d){
		//添加一个diao意味着注册了一个事件
		zhan.add(d);
		
		//获得前一个得到焦点的控件
		Diao sygDiao = zhan.get(index);
		
		
		//执行上一个控件的失去焦点事件
		sygDiao.shiqu();
		
		
		//如果上个控件的事件已经执行完毕
		zhan.remove(index);
	}
	
	//执行完所有的事件
	public void add2(Diao d){
		zhan.add(d);
		for(Diao d2 : zhan){
			//执行上一个控件的失去焦点事件
			d2.shiqu();
			//如果上个控件的事件已经执行完毕
			zhan.remove(d2);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
}