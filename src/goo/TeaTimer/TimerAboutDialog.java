package goo.TeaTimer;

import goo.TeaTimer.R;
import goo.TeaTimer.TimerActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.net.Uri;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class TimerAboutDialog extends Dialog {
	
	private Button mCancelButton;
		
	public TimerAboutDialog(Activity context) {
		super(context);
		init(context);
	}

	public TimerAboutDialog(Activity context, int theme) {
		super(context, theme);
		init(context);
	}

	public TimerAboutDialog(Activity context, boolean cancelable,
			OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);
		init(context);
	}
	
	/**
	 * Sharable code between constructors
	 */
	private void init(final Activity context){
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// Loads the layout of the dialog
		setContentView(R.layout.about);
		
		//	Display the application version in the About Dialog
		// String topText = "v "+JamendoApplication.getInstance().getVersion() + ", " + context.getString(R.string.about_note);
		// mVersionTextView = (TextView)findViewById(R.id.VersionText);
		// mVersionTextView.setText(topText);
		
		mCancelButton = (Button)findViewById(R.id.);
		mCancelButton.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View arg0) {
				TimerAboutDialog.this.dismiss();
			}

		});
	}

	
}