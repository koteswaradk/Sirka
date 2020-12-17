package com.yoobikwiti.sirka;







import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class ShowCustomAlertDialog {
	Context context;

	public ShowCustomAlertDialog(Context context) {
		this.context = context;
	}

	public void showDialog() {
		final Dialog dialog = new Dialog(context);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

		 dialog.setCancelable(false);
		dialog.setContentView(R.layout.customalertdialog);
		ImageButton wifiDialog = (ImageButton) dialog.findViewById(R.id.wifi_dialog);
		ImageButton datapackDialog = (ImageButton) dialog.findViewById(R.id.datapack_dialog);
		ImageButton cancelDialog = (ImageButton) dialog.findViewById(R.id.cancel);
		wifiDialog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				dialog.dismiss();
			}
		});
		datapackDialog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				dialog.dismiss();
			}
		});
		cancelDialog.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Toast.makeText(context, "Press OutSide The Box", Toast.LENGTH_SHORT).show();
				((Activity)context).finish();
				
				
			}
		});
		dialog.show();

	}
	
}
