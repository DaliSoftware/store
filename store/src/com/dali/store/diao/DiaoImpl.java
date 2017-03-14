package com.dali.store.diao;

import android.app.Activity;

public class DiaoImpl implements Diao{
	private Activity act;
	public DiaoImpl(Activity act){
		this.act = act;
	}
	
	@Override
	public void shiqu() {
	}

	@Override
	public void run() {
		
	}

	public Activity getAct() {
		return act;
	}

	public void setAct(Activity act) {
		this.act = act;
	}
	
	
}
