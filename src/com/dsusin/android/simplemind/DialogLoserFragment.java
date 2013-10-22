package com.dsusin.android.simplemind;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DialogLoserFragment extends DialogFragment {
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		super.onCreateDialog(savedInstanceState);
		return new AlertDialog.Builder(getActivity())
			.setTitle(R.string.string_loser)
			.setPositiveButton(R.string.string_newgame, null)
			.create();
	}

}
