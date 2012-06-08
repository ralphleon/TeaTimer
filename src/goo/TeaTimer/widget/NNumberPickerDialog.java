package goo.TeaTimer.widget;

/*
 * ASTRID: Android's Simple Task Recording Dashboard
 *
 * Copyright (c) 2009 Tim Su
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

import goo.TeaTimer.R;

import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Gallery;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;

import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

/** Dialog box with an arbitrary number of number pickers */
public class NNumberPickerDialog extends AlertDialog implements OnClickListener {

    public interface OnNNumberPickedListener {
        void onNumbersPicked(int[] number);
    }

    private final List<NumberPicker>      pickers = new LinkedList<NumberPicker>();
    private final OnNNumberPickedListener mCallback;

    private int hsel;
    private int msel;
    private int ssel;

    private GestureDetector gestureDetector;


	private Gallery hour;
	private Gallery min;
	private Gallery sec;

    /** Instantiate the dialog box.
     *
     * @param context
     * @param callBack callback function to get the numbers you requested
     * @param title title of the dialog box
     * @param initialValue initial picker values array
     * @param incrementBy picker increment by array
     * @param start picker range start array
     * @param end picker range end array
     * @param separators text separating the spinners. whole array, or individual
     *        elements can be null
     */
    public NNumberPickerDialog(Context context, OnNNumberPickedListener callBack,
            String title, int[] initialValue, int[] incrementBy, int[] start,
            int[] end, String[] separators,NumberPicker.Formatter [] format)
    {
        super(context);
        
        mCallback = callBack;
        
        setButton(context.getText(android.R.string.ok), this);
        setButton2(context.getText(android.R.string.cancel), (OnClickListener) null);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.n_number_picker_dialog, null);
        setView(view);

        //setTitle(title);
        LayoutParams npLayout = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.FILL_PARENT);
        npLayout.gravity = 1;
        LayoutParams sepLayout = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.FILL_PARENT);
		String [] numbers = new String[61];
		for(int i = 0; i < 61; i++) {
			numbers[i] = Integer.toString(i);
		}
		hour = (Gallery) view.findViewById(R.id.gallery_hour);
		min = (Gallery) view.findViewById(R.id.gallery_min);
		sec = (Gallery) view.findViewById(R.id.gallery_sec);
		
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(context, R.layout.gallery_item, numbers);        

		hour.setAdapter(adapter1);
		min.setAdapter(adapter1);
		sec.setAdapter(adapter1);

        gestureDetector = new GestureDetector(new MyGestureDetector());
        hour.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent ev) {
					return gestureDetector.onTouchEvent(ev);
			}
        });
        min.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent ev) {
					return gestureDetector.onTouchEvent(ev);
			}
        });
        sec.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent ev) {
					return gestureDetector.onTouchEvent(ev);
			}
        });

    }

    class MyGestureDetector extends SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

			velocityX=velocityX/5;

			return super.onFling(e1, e2, velocityX, velocityY);
		}

 
        @Override
        public boolean onDown(MotionEvent e) {
			return super.onDown(e);
	        //return true;
        }

    }

    
    public void setInitialValues(int[] values) {
		if(true)
			return;
        for(int i = 0; i < pickers.size(); i++)
            pickers.get(i).setCurrent(values[i]);
    }

    public void setSpeed(int[] values) {
		if(true)
			return;

        for (int i = 0; i < pickers.size(); i++)
            pickers.get(i).setSpeed(values[i]);
    }

    public void setSpeed(int value) {
		if(true)
			return;

        for (NumberPicker picker : pickers)
            picker.setSpeed(value);
    }

    public void onClick(DialogInterface dialog, int which) {
        if (mCallback != null) {
			
			hsel = hour.getSelectedItemPosition();
			msel = min.getSelectedItemPosition();
			ssel = sec.getSelectedItemPosition();
			
            int[] values = {hsel,msel,ssel};
            mCallback.onNumbersPicked(values);
        }
    }
}
