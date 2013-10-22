package com.dsusin.android.simplemind;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DialogWinFragment extends DialogFragment {
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		super.onCreateDialog(savedInstanceState);
		return new AlertDialog.Builder(getActivity())
			.setTitle(R.string.string_congratulations)
			.setPositiveButton(R.string.string_newgame, null)
			.create();
	}

}
