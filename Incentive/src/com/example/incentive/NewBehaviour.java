package com.example.incentive;

import java.util.Arrays;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class NewBehaviour extends Activity {

	public final static String COMPLETE_KEY = "compP";
	public final static String TIME_KEY = "timeP";
	public final static String DIFFICULT_KEY = "diffP";
	public final static String BENEFIT_KEY = "beneP";
	public final static String BEHAVIOUR_NAME_KEY = "behaviourP";
	
	private final static String TAG = "Incentive-New";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_behaviour);
        
        //Get the spinner id for time priority
        Spinner completePrioritySpin = (Spinner) findViewById(R.id.spinnerComplete);
        //Create an ArrayAdapter using the string array and a defaulyt spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.array_priority, android.R.layout.simple_spinner_item);
        //Specify layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Apply adapter to spinner
        completePrioritySpin.setAdapter(adapter);
        
        //Set up the time spinner
        Spinner timeSpin = (Spinner) findViewById(R.id.spinnerTime);
        ArrayAdapter<CharSequence> timeAdapter = ArrayAdapter.createFromResource(this, R.array.array_time, android.R.layout.simple_spinner_item);
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeSpin.setAdapter(timeAdapter);
        
        //Set up the Difficulty spinner
        Spinner difficultSpin = (Spinner) findViewById(R.id.spinnerDifficult);
        //ArrayAdapter<CharSequence> diffAdapter = ArrayAdapter.createFromResource(this, R.array.priority_array, android.R.layout.simple_spinner_item);
        //diffAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        difficultSpin.setAdapter(adapter);
        
        //Set up the benefit spinner
        Spinner benefitSpin = (Spinner) findViewById(R.id.spinnerBenefit);
        benefitSpin.setAdapter(adapter);
        
        //Set up the actions to take upon clicking the "Add" button
        Button addBehaviourButton = (Button) findViewById(R.id.bNewBehaviour);
        addBehaviourButton.setOnClickListener(new OnClickListener(){
        	
        	@Override
        	public void onClick(View v){
        		Log.i(TAG, "Add clicked");
        		//Get the text of the text field that requests a behaviour name 
        		EditText mBehaveName = (EditText) findViewById(R.id.mBehaviourName);        		        		
        		
        		//Get the spinners
        		Spinner compSpin = (Spinner) findViewById(R.id.spinnerComplete);
        		Spinner timeSpin = (Spinner) findViewById(R.id.spinnerTime);
        		Spinner diffSpin = (Spinner) findViewById(R.id.spinnerDifficult);
        		Spinner benSpin = (Spinner) findViewById(R.id.spinnerBenefit);
        		
        		String compSpinSelected = compSpin.getSelectedItem().toString();
        		String timeSpinSelected = timeSpin.getSelectedItem().toString();
        		String diffSpinSelected = diffSpin.getSelectedItem().toString();
        		String benSpinSelected = benSpin.getSelectedItem().toString();        		
        		
        		//Get the context (activity) that this button belongs in
                Activity host = (Activity) v.getContext();
        		
                //If a name for the behaviour was not entered               
        		if (mBehaveName.getText().toString().equals("")){
        			Log.i(TAG, "no name entered");
        			//Create an alert for the user with an OK button
        			AlertDialog.Builder alert = new AlertDialog.Builder(host);
        			
        			alert.setTitle(R.string.alert_title_add_behaviour);
        			alert.setMessage(R.string.alert_message_add_behaviour);
        			//Create the OK button
        			alert.setNegativeButton(R.string.button_okay, new DialogInterface.OnClickListener() {						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							//alert.
							Log.i(TAG, "alert clicked");
						}
					});
        			//Show the alert
        			alert.show();
        		}else{        			   
        			//If a name was entered
        			Log.i(TAG, mBehaveName.getText().toString());
        			
        			Log.i(TAG, "c: "+ compSpinSelected + " " + priorityToInt(compSpinSelected)
            				+" t: "+timeSpinSelected + " " + priorityToInt(timeSpinSelected)
            				+" d: "+diffSpinSelected + " " + priorityToInt(diffSpinSelected)
            				+" b: "+benSpinSelected + " " + priorityToInt(benSpinSelected));
            		
        			//Create the intent to pass the result data back to the main activity
            		Intent resultData = new Intent();          
                    resultData.putExtra(COMPLETE_KEY, priorityToInt(compSpinSelected));
                    resultData.putExtra(TIME_KEY, priorityToInt(timeSpinSelected));
                    resultData.putExtra(DIFFICULT_KEY, priorityToInt(diffSpinSelected));
                    resultData.putExtra(BENEFIT_KEY, priorityToInt(benSpinSelected));
                    resultData.putExtra(BEHAVIOUR_NAME_KEY, mBehaveName.getText().toString());                
                    
                    //Set the result data 
                    host.setResult(RESULT_OK, resultData);
                    
                    //Close the activity
                    NewBehaviour.this.finish();
        		}        		
        	}
        	
        });
        
        
        
    }
    
    private int priorityToInt(String prior){
    	String[] priorityArray;
    	String[] timePriorityArray;
    	priorityArray = getResources().getStringArray(R.array.array_priority);
    	timePriorityArray = getResources().getStringArray(R.array.array_time);
    	
    	int pIndex = Arrays.asList(priorityArray).indexOf(prior);
    	int tIndex = Arrays.asList(timePriorityArray).indexOf(prior);
    	
    	//Return tIndex if pIndex is -1 (i.e. the String prior is not
    	//and element of priorityArray) otherwise return pIndex
    	return ((pIndex == -1) ? timePriorityArray.length - tIndex : priorityArray.length - pIndex);
    	
    	//return 0;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
