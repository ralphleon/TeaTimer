/* @file AbsTimePickerDialog.java
 * 
 * TeaTimer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version. More info: http://www.gnu.org/licenses/
 *  
 * Copyright 2009 Ralph Gootee <rgootee@gmail.com>
 *  
 */

package com.goo.TeaTimer;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.TimePicker;

/**
 * Cool override of the time picker class, to pick a time without regard for the current time
 * 
 * @author Ralph Gootee (rgootee@gmail.com)
 *
 */
class AbsTimePickerDialog extends TimePickerDialog
{
	/**
	 * Overloaded constructor, very similar to TimePickerDialog
	 * @param context the context of the parent
	 * @param callBack the callback function
	 * @param h the default hour
	 * @param m the default minute
	 */
	public AbsTimePickerDialog(Context context, TimePickerDialog.OnTimeSetListener callBack, int h,int m){
		
		super(context, callBack, h, m, true);
		
		CharSequence title = h + " hours & " + m + " min";
		super.setTitle(title);
	}
	
	/**
	 * We don't want the parent to set the title... ever
	 */
	@Override
	public void setTitle(CharSequence ignore)
	{		
	}
	
	/** {@inheritDoc} */
	@Override
	public void onTimeChanged(TimePicker ignore,int h,int m)
	{
		CharSequence title = h + " hours & " + m + " min";
		super.setTitle(title);
		
	}
}