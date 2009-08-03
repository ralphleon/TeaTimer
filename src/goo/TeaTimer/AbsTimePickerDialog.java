
package goo.TeaTimer;

import android.app.TimePickerDialog;
import android.content.Context;

class AbsTimePickerDialog extends TimePickerDialog
{
	public AbsTimePickerDialog(Context context, TimePickerDialog.OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView){
		
		super(context, callBack, hourOfDay, minute, is24HourView);
	}
	
	public void setTitle(CharSequence title)
	{
		//CharSequence x = title;
	
	}
}