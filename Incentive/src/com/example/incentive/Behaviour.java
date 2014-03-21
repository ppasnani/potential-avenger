package com.example.incentive;

import android.util.Log;

public class Behaviour {
	private static String TAG = "Incentive-Behaviour";
	
	private String behaviourName;
	
	private int completePriority;
	private int timePriority;
	private int difficultLevel;
	private int benefitLevel;
	private int pointValue;
	
	private final static String DATA_SEPERATE_TOKEN = ":";
	
	public Behaviour(String storeData){
		String[] seperated = storeData.split(Behaviour.DATA_SEPERATE_TOKEN);
		this.behaviourName = seperated[0];
		Log.i(TAG, "Name: " + this.behaviourName);
		this.completePriority = Integer.parseInt(seperated[1]);
		this.timePriority = Integer.parseInt(seperated[2]);
		this.difficultLevel = Integer.parseInt(seperated[3]);
		this.benefitLevel = Integer.parseInt(seperated[4]);
		
		this.setPointsValue();
	}
	
	public Behaviour(String name, int priority, int time, int difficulty, int benefit){
		this.behaviourName = name;
		
		this.completePriority = priority;
		this.timePriority = time;
		this.difficultLevel = difficulty;
		this.benefitLevel = benefit;
		
		this.setPointsValue();
	}
	
	
	@Override 
	public String toString(){
		return this.behaviourName + ": " + this.pointValue;
	}
	
	public String toDataString(){
		
		/*String createdData = DATA_SEPERATE_TOKEN + completionPriority
				+ DATA_SEPERATE_TOKEN + timeLength
				+ DATA_SEPERATE_TOKEN + difficultyRate
				+ DATA_SEPERATE_TOKEN + benefitRate;*/
		String createdData = this.behaviourName + Behaviour.DATA_SEPERATE_TOKEN
							+ this.completePriority + Behaviour.DATA_SEPERATE_TOKEN
							+ this.timePriority + Behaviour.DATA_SEPERATE_TOKEN
							+ this.difficultLevel + Behaviour.DATA_SEPERATE_TOKEN
							+ this.benefitLevel + "\n";
		
		return createdData;
	}
	//Requires: The Behaviour object has already been created
	//Ensures: The pointValue attribute is set to the appropriate value
	//			based on the algorithm from the following post
	//http://productivity.stackexchange.com/questions/2972/gamification-to-improve-myself
	//Temporary algorithm. Work on a better one. Use matlab to view attributes of current.
	private void setPointsValue(){
		float average = (float) (this.timePriority + this.difficultLevel + this.benefitLevel) / 3.0f;
		//Original algorithm says divide by 3 but since using 1 fewer variable
		//dividing by 2.
		float divideByThree = average / 2.0f;
		this.pointValue = (((int)divideByThree) + this.completePriority);
	}
	
	public int getPointsValue(){
		return this.pointValue;
	}
	
	public String getBehaviourName(){
		return this.behaviourName;
	}
	
	public int getCompletePriority(){
		return this.completePriority;
	}
	
	public int getTimePriority(){
		return this.timePriority;
	}
	
	public int getDifficultLevel(){
		return this.difficultLevel;
	}
	
	public int getBenefitLevel(){
		return this.benefitLevel;
	}
	
	public void setCompletePriority(int newPriority){
		this.completePriority = newPriority;
		this.setPointsValue();
	}
	
	public void setTimePriority(int newPriority){
		this.timePriority = newPriority;
		this.setPointsValue();
	}
	
	public void setDifficultLevel(int newLevel){
		this.difficultLevel = newLevel;
		this.setPointsValue();
	}
	
	public void setBenefitLevel(int newLevel){
		this.benefitLevel = newLevel;
		this.setPointsValue();
	}
	
}
