package com.example.incentive;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	public static final int ADD_BEHAVIOUR_REQUEST = 0;
	
	private final static String TAG = "Incentive-Main";
	private final static String BEHAVIOUR_FILE = "userBehaviours";
	//String to identify the line containing number of points
	private final static String POINTS_KEY = "Points:";
	//Integer stating the line # at which the behaviour list starts.
	//Note that the behaviour list goes until the end no matter what line
	//it starts in.
	private final static int BEHAVIOUR_START_LINE = 1; 
	
	private int numPoints = 0;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //Get reference to add behaviour button
        Button addBehaviourButton = (Button) findViewById(R.id.bAddBehaviour);
        addBehaviourButton.setOnClickListener(new OnClickListener(){
        	//Program what happens onClick
        	@Override
        	public void onClick(View v){
        		//Create intent to start new activity
        		Intent startAddBehaviour = new Intent(MainActivity.this, NewBehaviour.class);
        		//Start it with the intent to get the result
        		//MainActivity.this.startActivity(startAddBehaviour);
        		MainActivity.this.startActivityForResult(startAddBehaviour, ADD_BEHAVIOUR_REQUEST);
        	}
        });
        
        //Set the current number of points based on the value in the file
        this.numPoints = this.getPoints();
        
        //Get reference to cash points button
        Button cashButton = (Button) findViewById(R.id.bCashInPoints);
        cashButton.setOnClickListener(new OnClickListener(){
        	//Requires: A string exchangeRate in the format of a float
        	//Ensures: Result is exchangeRate * numPoints rounded to two decimal places
        	private float getDollarValue(String exchangeRate, MainActivity host){
        		if (exchangeRate.length() > 1 || (exchangeRate.length() == 1 && exchangeRate.charAt(0)!='.')){
					//As text changes update the message to tell user how many dollars
					//Float rate = Flo.parseInt(new String("" + s));
					float rate = Float.parseFloat(new String("" + exchangeRate));							
					
					float cashMoney = rate * ((float) host.numPoints);
					return cashMoney;
				} else{
					return 0f;
				}        		
        	}
			@Override
			public void onClick(View v) {
				//Create dialogbox to display the currency exchange between points and dollars
				AlertDialog.Builder pointAlert = new AlertDialog.Builder(MainActivity.this);
				pointAlert.setTitle(R.string.text_dialog_point_title);								
				//Get the layout inflater. Final so I am able to access it from TextWatcher
                final MainActivity host = (MainActivity) v.getContext();				
				LayoutInflater inflater = host.getLayoutInflater();				
				
				//Inflate and set the layout for the dialog
				//Pass null as the parent view because its going in the dialog layout
				View inflatedDialog = inflater.inflate(R.layout.dialog_point, null);
				pointAlert.setView(inflatedDialog);
				//Get reference to edit text field and the textView in the dialog box												
				final EditText exchangeRateEditText = (EditText) inflatedDialog.findViewById(R.id.editPointExchangeRate);
				//Final so I am able to access it from TextWatcher
				final TextView messageTextView = (TextView) inflatedDialog.findViewById(R.id.textViewResultMoney);
				//Set default display of the textbox
				messageTextView.setText(getString(R.string.text_dialog_dollar_val) + " $0");
				//Add the text watcher
				exchangeRateEditText.addTextChangedListener(new TextWatcher(){

					@Override
					public void afterTextChanged(Editable s) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						// TODO Auto-generated method stub
						Log.i(TAG, "New text: " + s);					
						messageTextView.setText(getString(R.string.text_dialog_dollar_val) + " $"
									+ getDollarValue(new String("" + s), host));						
					}
					
				});
				
				//Add the OK button
				pointAlert.setPositiveButton(R.string.button_okay, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String setExchangeRate = exchangeRateEditText.getText().toString();
						//Use reward dollars to create nice alert
						//float rewardDollars = getDollarValue(setExchangeRate, host);
						//For now, just subtract the points from the final and update the total
						host.resetPoints();
						//And update the points total
						//host.writePointsToFile();				        
					}
				});
				//Add the Cancel button
				pointAlert.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub						
					}
				});
				pointAlert.show();
			}        	
        });
        
        Log.i(TAG, "onCreate");        
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	Log.i(TAG, "onResume");    	
        this.populateBehaviourList();
        this.updatePointsField();
    }
    
    @Override
    public void onPause(){
    	super.onPause();
    	
    	this.writePointsToFile();
    	Log.i(TAG, "onPause");
    }

    private void populateBehaviourList(){
    	Behaviour[] savedBehaviours = this.parseSavedFile();
    	
    	ListView behaviourList = (ListView) findViewById(R.id.behaviourList);
    	
    	if (savedBehaviours != null){
    		    	
    		final ArrayAdapter<Behaviour> adapter = new ArrayAdapter<Behaviour>(this, android.R.layout.simple_list_item_1, savedBehaviours);
        	behaviourList.setAdapter(adapter);
        	Log.i(TAG, "populating");
        	
        	behaviourList.setOnItemClickListener(new OnItemClickListener(){

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					//A behaviour has been completed and clicked so
					//add the new points and update the text field
					Behaviour clicked = adapter.getItem(arg2);
					Log.i(TAG, "Behaviour list clicked: " + arg2 + " " + clicked.toString());
					//Add the new points
					numPoints += clicked.getPointsValue();
					//Update the points field
					((MainActivity) arg0.getContext()).updatePointsField();					
				}
        		
        	});
        	
        	behaviourList.setOnItemLongClickListener(new OnItemLongClickListener(){

				@Override
				public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub
					//Alert user that behavior already exists so do nothing
					AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
					alert.setTitle(R.string.alert_title_remove_behaviour);
					alert.setMessage(R.string.alert_message_remove_behaviour);
					final int behaviourClicked = arg2;
					
					alert.setPositiveButton(R.string.button_okay, new DialogInterface.OnClickListener() {								
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							//***************************************************************//
					    	//I WAS HERE. TRYING TO FIGURE OUT HOW TO REMOVE THE BEHAVIOUR
							//FROM THE LIST SAFELY.
					    	//***************************************************************//
							//adapter.remove(adapter.getItem(behaviourClicked));
							
						}
					});
					
					alert.setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					});
					//Show the alert
					alert.show();
					return false;
				}
        		
        	});
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
    	if (resultCode == RESULT_OK){
    		if (requestCode == ADD_BEHAVIOUR_REQUEST){
    			//All behaviours characteristics were properly set
    			String newBehaviourName = data.getStringExtra(NewBehaviour.BEHAVIOUR_NAME_KEY);
    			int completionPriority = data.getIntExtra(NewBehaviour.COMPLETE_KEY, 0);
    			int timeLength = data.getIntExtra(NewBehaviour.TIME_KEY, 0);
    			int difficultyRate = data.getIntExtra(NewBehaviour.DIFFICULT_KEY, 0);
    			int benefitRate = data.getIntExtra(NewBehaviour.BENEFIT_KEY, 0);
    			
    			if (completionPriority == 0 || timeLength == 0 || difficultyRate == 0 || benefitRate == 0){
    				Log.i(TAG, "Something went wrong. None of the priority values were received");
    			}else{
    				//Received data properly so create a behaviour it to file
    				Behaviour saveBehaviour = new Behaviour(newBehaviourName, 
    														completionPriority,
    														timeLength,
    														difficultyRate,
    														benefitRate);
    				//And save it to file
    				Log.i(TAG, saveBehaviour.toDataString());
    				writeDataToFile(newBehaviourName, saveBehaviour.toDataString());
    			}
    		}
    	}//Else result was not OK, so no need to do anything
    }
    
    //Requires:
    //Ensures: Resets points total for this instance to 0 and updates
    //			the textView
    public void resetPoints(){
    	this.numPoints = 0;
        this.updatePointsField();
    }

    //Requires: An instantiated MainActivity and existing TextView
    //Ensures: Sets the textview to display the appropriate number of points
    private void updatePointsField(){

    	TextView pointsText = (TextView) findViewById(R.id.textTotalPoints);
        pointsText.setText(MainActivity.POINTS_KEY + " " + this.numPoints);
    }
    //Requires:
    //Ensures: If file exists for this app then return number of points stored
    //			Else return 0 
    private int getPoints(){
    	try{
    		FileInputStream checkExisting = openFileInput(MainActivity.BEHAVIOUR_FILE);
    		String existingFile = MainActivity.getStringFromFile(checkExisting);
    		//If the first line contains the string Points:
    		String firstLine = existingFile.split("\n")[0];
    		if (firstLine.contains(MainActivity.POINTS_KEY)){
    			//then get the number of points and return it
    			int pointsIndex = firstLine.indexOf(MainActivity.POINTS_KEY) + MainActivity.POINTS_KEY.length();
    			int numExistPoints = Integer.parseInt(firstLine.substring(pointsIndex));
    			return numExistPoints;
    		}else{
    			//else add the points key to the first line and save the file
    			String newFileString = MainActivity.POINTS_KEY + 0 + "\n" + existingFile;
    			try {
    				FileOutputStream fos = openFileOutput(MainActivity.BEHAVIOUR_FILE, Context.MODE_PRIVATE);    				
    				fos.write(newFileString.getBytes());
    				fos.close();											
    			} catch (IOException e2) {
    				// TODO Auto-generated catch block
    				e2.printStackTrace();
    				Log.i(TAG, "Cannot create file or can't write data to it");
    			}
    			return 0;
    		}
    	}catch(FileNotFoundException e1){
    		//No file exists so just return 0 points
    		//num points will be saved onPause
			return 0;
    	}
    	
    }
    
    //Requires: The list of behaviours starts at line MainActivity.BEHAVIOUR_START_LINE
    //			and continues till the end of MainActivity.BEHAVIOUR_FILE
    //Ensures: Returns the list of behaviours saved in the file
    //			Returns null if there is no file
    private Behaviour[] parseSavedFile(){    	
    	
    	try {
			FileInputStream behaviourSaveFile = openFileInput(MainActivity.BEHAVIOUR_FILE);
			String fileString = MainActivity.getStringFromFile(behaviourSaveFile);
			String[] fileLines = fileString.split("\n");
			if (fileLines.length > MainActivity.BEHAVIOUR_START_LINE){
				int numBehaviours = fileLines.length - MainActivity.BEHAVIOUR_START_LINE;
				Behaviour[] parsedBehaviours = new Behaviour[numBehaviours];
				for (int i = 0; i < numBehaviours; i++){
					parsedBehaviours[i] = new Behaviour(fileLines[i+MainActivity.BEHAVIOUR_START_LINE]);
				}
				return parsedBehaviours;
			}else{
				Log.i(TAG, "parse: saved file is zero length");
				return null;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.i(TAG, "parse: could not open behaviour file");
		}
    	return null;
    }
    
    
    //Requires: this.numPoints has been instantiated
    //Ensures: The number of points in the file is updated to the numPoints instance
    //			If no file exists then file is created and numPoints instance saved
    private void writePointsToFile(){
		try {
			//If file already exists then get the file string
			FileInputStream existingFile = openFileInput(MainActivity.BEHAVIOUR_FILE);			
			String currentFile = MainActivity.getStringFromFile(existingFile);
			
			//Replace the first existing line which contains the number of points saved
			//with the number of points this session
			String[] linesCurrentFile = currentFile.split("\n");
			StringBuffer newFile = new StringBuffer(MainActivity.POINTS_KEY + this.numPoints + "\n");
			
			for (int i = MainActivity.BEHAVIOUR_START_LINE; i < linesCurrentFile.length; i++){
				newFile.append(linesCurrentFile[i] + "\n");
			}
								
			try {
				FileOutputStream fos = openFileOutput(MainActivity.BEHAVIOUR_FILE, Context.MODE_PRIVATE);
				fos.write(newFile.toString().getBytes());
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block				
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//File does not exist so create it and save the number of points to it
			e.printStackTrace();
			Log.i(TAG, "File " + MainActivity.BEHAVIOUR_FILE + " does not exist");
			try {
				FileOutputStream fos = openFileOutput(MainActivity.BEHAVIOUR_FILE, Context.MODE_PRIVATE);
				String pointString = MainActivity.POINTS_KEY + this.numPoints + "\n";
				fos.write(pointString.getBytes());
				fos.close();											
			} catch (IOException e2) {
				// TODO Auto-generated catch block
				e2.printStackTrace();
				Log.i(TAG, "Cannot create file or can't write data to it");
			}
		}
    	
    }
    
    //Requires: A non empty behaviourName, and a String containing the priority levels
    //			for different attributes formatted as a key, space, integer value, space
    //Ensures: The data is written to file in one line with the behavior data following
    //			the behaviour name    
    
    //When saving look into sorting the behaviours to display in some interesting order.
	//Better to do it while writing because less sorting time than having to sort every
    //time we populate the ListView
    private void writeDataToFile(String behaviourName, String behaviourData){
    	//
    	try {
			FileInputStream checkExisting = openFileInput(MainActivity.BEHAVIOUR_FILE);
			//File was found. Check if  a behaviour of same name
			//already exists in the file.
			if (MainActivity.fileContains(behaviourName, checkExisting)){
				//Alert user that behavior already exists so do nothing
				AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
				alert.setTitle(R.string.alert_title_existing_behaviour);
				alert.setMessage(R.string.alert_message_existing_behaviour);
				alert.setNegativeButton(R.string.button_okay, new DialogInterface.OnClickListener() {								
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						//Put here what you want to happen when user clicks
						//OK button of alert
					}
				});
				//Show the alert
				alert.show();
			}else{
				FileOutputStream fos = openFileOutput(MainActivity.BEHAVIOUR_FILE, Context.MODE_APPEND);
				try {
					fos.write(behaviourData.getBytes());
					fos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.i(TAG, "Trouble converting data string to bytes and writing to file");
				}
				
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//No file exists so have to create it
			//e.printStackTrace();
			Log.i(TAG, "Behaviour data write file " + MainActivity.BEHAVIOUR_FILE + " does not exist");			
		}		    				
	}
    
    
    //Requires: A checkString to search the existingFile for
    //Ensures: True if the checkString occurs in the file
    //			False if the checkString does not appear in the file
    public static boolean fileContains(String checkString, FileInputStream existingFile){    	
    	String fileContents = MainActivity.getStringFromFile(existingFile);
    	//Close the opened file after the file data has been read
    	try {
			existingFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "IO error trying to close read file");
			e.printStackTrace();
		}
    	return fileContents.contains(checkString);
    }
    
    //Requires: FileInputStream object pointing to existing file
    //Ensures: Returns a string version of the complete file
    public static String getStringFromFile(FileInputStream fIn){
    	BufferedReader reader = new BufferedReader(new InputStreamReader(fIn));
    	StringBuilder sb = new StringBuilder();
    	String line = null;
    	try {
			while((line = reader.readLine()) != null){
				sb.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "IO error while trying to read from file");
			e.printStackTrace();
		}
    	return sb.toString();
    }
        
}
