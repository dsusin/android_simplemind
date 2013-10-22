package com.dsusin.android.simplemind;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.color;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class SimpleMindFragment extends Fragment {
	private final static String TAG="SimpleMindFragment";
	private final static String DIALOG_WIN="win";
	private final static String DIALOG_LOST="lost";
	private Button mCheckButton;
	private int[] mSolution=new int[4];
	private int mCurrentRow=0;
	private final static int NUM_ROWS=6;
	private TableLayout mTableLayout;
	private View.OnClickListener mTextViewListener;
	
	
	private class NetworkingTask extends AsyncTask<Void, Void, String>{

		@Override
		protected String doInBackground(Void... params) {
			try{
				String result=new ScoreUploader().submitResult("user1", 1);
				Log.i(TAG, "Score submitted, response: "+result);
				return result;
			}catch(IOException ioe){
				Log.e(TAG, "Failed uploading score");
			}catch(JSONException je){
				Log.e(TAG, "Failed uploading score");
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String result){
			int score=0;
			try {
				JSONObject json = new JSONObject(result);
				JSONArray jsonArray=(JSONArray) json.get("users");
				if(jsonArray.length()>0){
					score=jsonArray.getJSONObject(0).getInt("score");
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			showScore(score);
		}
		
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
		View v=inflater.inflate(R.layout.fragment_simple_mind, parent, false);
		
		mCheckButton=(Button)v.findViewById(R.id.fragment_simple_mind_checkButton);
		mCheckButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Log.i(TAG, "Check!!!");
				boolean mResolved=checkResult();
				if(mResolved){
					congrats();
					newGame();
				}else{
					mCurrentRow=(mCurrentRow+1)%NUM_ROWS;
					if(mCurrentRow==0){
						gameOver();
						newGame();
					}else{
						paintCurrentRow(Color.rgb(120, 120, 255));
					}
				}
			}
		});
		
		mTextViewListener=new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TextView textView=(TextView)v;
				int val=0;
				if(textView.getText().toString()!=""){
					val=(Integer.parseInt(textView.getText().toString())+1)%10;
				}
				textView.setText(""+val);
			}
		};
		
		mTableLayout=(TableLayout)v.findViewById(R.id.fragment_simple_mind_tableLayout);
		
		newGame();

		return v;
	}
	
	private boolean checkResult(){
		TableRow row=(TableRow)mTableLayout.getChildAt(mCurrentRow);

		int[] proposed=new int[4];
		
		for(int i=0; i<4; i++){
			if(((TextView)row.getChildAt(i)).getText().equals("")){
				proposed[i]=-1;
			}else{
				proposed[i]=Integer.parseInt(((TextView)row.getChildAt(i)).getText().toString());
			}
		}
		
		Log.i(TAG, "Checking: "+proposed[0]+""+proposed[1]+""+proposed[2]+""+proposed[3]);

		if(mSolution[0]==proposed[0] &&
				mSolution[1]==proposed[1] &&
				mSolution[2]==proposed[2] &&
				mSolution[3]==proposed[3]){
			
			paintCurrentRow(Color.rgb(0, 200, 0));
			
			return true;
		}
		
		//Primero pintamos todo de blanco
		paintCurrentRow(Color.WHITE);
		
		//Pintamos los aciertos
		for(int i=0; i<4; i++){
			if(mSolution[i]==proposed[i]){
				TextView textView=(TextView)row.getChildAt(i);
				paintTextView(textView, Color.rgb(0, 200, 0));
			}
		}
		
		//Pintamos los semiaciertos
		for(int i=0; i<4; i++){
			for(int j=0; j<4; j++){
				if(mSolution[i]==proposed[j] && i!=j){
					TextView textView=(TextView)row.getChildAt(j);
					paintTextView(textView, Color.rgb(255, 255, 0));
				}
			}
		}		
		
		return false;
	}
	
	private void newGame(){
		Log.i(TAG, "New Game");
		
		generateRandomSolution();
		
		cleanTable();
		
		paintCurrentRow(Color.rgb(120, 120, 255));
	}
	
	private void congrats(){
		Log.i(TAG, "Congrats!!! :D");
		FragmentManager fm=getActivity().getSupportFragmentManager();
		DialogWinFragment dialog=new DialogWinFragment();
		dialog.show(fm, DIALOG_WIN);
		new NetworkingTask().execute();
	}
	
	private void gameOver(){
		Log.i(TAG, "Game Over :(");
		FragmentManager fm=getActivity().getSupportFragmentManager();
		DialogLoserFragment dialog=new DialogLoserFragment();
		dialog.show(fm, DIALOG_LOST);
	}
	
	private void cleanTable(){
		for(mCurrentRow=0; mCurrentRow<mTableLayout.getChildCount()-1; mCurrentRow++){
			TableRow row=(TableRow) mTableLayout.getChildAt(mCurrentRow);
			paintCurrentRow(Color.WHITE);
			for(int j=0; j<row.getChildCount(); j++){
				TextView textView=(TextView)row.getChildAt(j);
				textView.setText("");
			}
		}
		
		mCurrentRow=0;
	}
	
	private void paintCurrentRow(int color){
		for(int i=0; i<mTableLayout.getChildCount()-1; i++){
			TableRow row=(TableRow) mTableLayout.getChildAt(i);
			for(int j=0; j<row.getChildCount(); j++){
				TextView textView=(TextView)row.getChildAt(j);
				if(i==mCurrentRow){
					textView.setBackgroundColor(color);
					textView.setOnClickListener(mTextViewListener);
				}else{
					//textView.setBackgroundColor(Color.WHITE);
					textView.setOnClickListener(null);
				}
			}
		}
	}
	
	private void paintTextView(TextView textView, int color){
		textView.setBackgroundColor(color);
	}
	
	private void generateRandomSolution(){
		mSolution[0]=(int) (Math.random()*10);
		mSolution[1]=(int) (Math.random()*10);
		while(mSolution[1]==mSolution[0]){
			mSolution[1]=(int) (Math.random()*10);
		}
		mSolution[2]=(int) (Math.random()*10);
		while(mSolution[2]==mSolution[0] || mSolution[2]==mSolution[1]){
			mSolution[2]=(int) (Math.random()*10);
		}
		mSolution[3]=(int) (Math.random()*10);
		while(mSolution[3]==mSolution[0] || mSolution[3]==mSolution[1] || mSolution[3]==mSolution[2]){
			mSolution[3]=(int) (Math.random()*10);
		}
		
		Log.i(TAG, ""+mSolution[0]+""+mSolution[1]+""+mSolution[2]+""+mSolution[3]);		
	}
	
	private void showScore(int score){
		Toast.makeText(getActivity(), "Score: "+score, Toast.LENGTH_LONG).show();
	}
}
