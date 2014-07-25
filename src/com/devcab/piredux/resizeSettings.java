package com.devcab.piredux;

//Import android OS features
import android.os.Bundle;

//Import android Application features
import android.app.Activity;
import android.app.AlertDialog;

//Import Android View Features
import android.view.View;
import android.view.KeyEvent;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;

//Import android intent features
import android.content.Intent;

//Import android content features
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;

//Import Button
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.CheckBox;
import android.widget.TextView;

import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

//Import RadioGroup
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class resizeSettings extends Activity
{
	//Global variable for CheckBox
	CheckBox aspectCheckBox;
	
	//Global variable for choice selection
	String aspectRatioSelected = "CHECKED";
	
	//Global variable for RadioGroup
	RadioGroup selectedSize;
	//Global variable for okay button
	Button okayClicked;
		
	//Global variable for selected width and height ints
	int selectedWidth = 1;
	int selectedHeight = 1;
	//Global variable selectedRadio Button variable
	int selectedRadioButton = 3;
	//Global variable selectedRadio button String
	String selectedRadioButtonString = "HD (1920px x 1080px)";
	
	//Global variable for Context
	final Context context = this;
	
	//Global variable for Alert Dialog Selector for YES_NO
	int alertDialogSelector = 0;
	EditText customWidthbtn, customHeightbtn;
	TextView fitsATextBox, byTextBox, screenTextBox, seekBarValue;
	boolean customVisibility = false;
	
	//Global variable SeekBar
	SeekBar seekbar;
	int qualityCompressValue = 100;
	int value2Show = 0;
	String seekTypeTextString = "0";

	//Global variable Intent
	Intent selectedAcceptedSize;
	
	//Global final int
	final int MAX_Width_HEIGHT = 5001;
		
	protected void onCreate(Bundle savedInstanceState)
	{
		//create Bundle package (required before anything else)
		super.onCreate(savedInstanceState);
		//display Content View (required before anything else)
		setContentView(R.layout.resize_layout);
		int setRadioButtonSelected = getIntent().getIntExtra("selectedRadioButton", selectedRadioButton);
		String setRadioButtonString = getIntent().getStringExtra("selectedRadioButtonString");
		int setWidthSelected = getIntent().getIntExtra("selectedWidth", selectedWidth);
		int setHeightSelected = getIntent().getIntExtra("selectedHeight", selectedHeight);
		String setSelectedRatioButton = getIntent().getStringExtra("aspectRatioSelected");
		int compressQualityValue = getIntent().getIntExtra("qualityCompressValue", qualityCompressValue);
		SharedPreferences prefs = getPreferences(MODE_PRIVATE);
		boolean set_info_saved = false;
		boolean restoredText = prefs.getBoolean("info_saved", set_info_saved);
		if(restoredText == true)
		{
			getSettingsChoices();	
		}
		//Function to set RadioGroup Selection		
		setRadioGroupSection();
		initializeCheckBox();
		setCheckBoxChoice(setSelectedRatioButton);
		setRadioButtonSelection(setRadioButtonSelected, setRadioButtonString);
		initializeEditText();
		initializeTextView();
		
		//Function to assign EditText Selection
		setEditTextFunction(setWidthSelected, setHeightSelected);
		
		//Function to initialize Okay button
		initializeButtons();
		initializeSeekBar();
		//Function to set all action Listeners
		setActionListeners();
		setVisiblityForCustom(2);
	}
	
	public void initializeEditText()
	{
		customWidthbtn = (EditText)findViewById(R.id.customWidth);
		customHeightbtn = (EditText)findViewById(R.id.customHeight);
	}
	
	public void initializeTextView()
	{
		fitsATextBox = (TextView) findViewById(R.id.fitsAText);
		byTextBox = (TextView) findViewById(R.id.byText);
		screenTextBox = (TextView) findViewById(R.id.screenText);
		seekBarValue = (TextView) findViewById(R.id.qualityText);
	}
	
	public void initializeCheckBox()
	{
		aspectCheckBox = (CheckBox)findViewById(R.id.checkboxAspectRatio);
	}
	
	public void initializeSeekBar()
	{
		SeekBar seekBar = (SeekBar)findViewById(R.id.seekbar);
		seekBar.setProgress(0);
		seekBar.incrementProgressBy(50);
		seekBar.setMax(150);
		
		seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

		    @Override
		    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		        progress = progress / 50;
		        progress = progress * 50;
		        seekBarValue.setText("Quality: " + String.valueOf(progress));
		        if(progress >= 0 && progress <= 49)
		        {
		        	qualityCompressValue = 50;
		        }
		        else if(progress >= 50 && progress <= 64)
		        {
		        	qualityCompressValue = 65;
		        }
		        else if(progress >= 65 && progress <= 100)
		        {
		        	qualityCompressValue = 80;
		        }
		        else
		        {
		        	qualityCompressValue = 80;
		        }
		    }

		    @Override
		    public void onStartTrackingTouch(SeekBar seekBar)
		    {

		    }

		    @Override
		    public void onStopTrackingTouch(SeekBar seekBar) {

		    }
		});
	}
	
	public void setVisiblityForCustom(int option)
	{
		switch(option)
		{
			case 1:
				customVisibility = true;
				customWidthbtn.setVisibility(View.VISIBLE);
				customHeightbtn.setVisibility(View.VISIBLE);
				fitsATextBox.setVisibility(View.VISIBLE);
				byTextBox.setVisibility(View.VISIBLE);
				screenTextBox.setVisibility(View.VISIBLE);
				break;
			case 2:
				customVisibility = false;
				customWidthbtn.setVisibility(View.INVISIBLE);
				customHeightbtn.setVisibility(View.INVISIBLE);
				fitsATextBox.setVisibility(View.INVISIBLE);
				byTextBox.setVisibility(View.INVISIBLE);
				screenTextBox.setVisibility(View.INVISIBLE);
				break;
		}
	}
	
	public void setCheckBoxChoice(String selectedCheckBox)
	{
		if(selectedCheckBox.equals("CHECKED"))
		{
			aspectCheckBox.setSelected(true);
			aspectRatioSelected = "CHECKED";
		}
		else if(selectedCheckBox.equals("UNCHECKED"))
		{
			aspectCheckBox.setSelected(false);
			aspectRatioSelected = "UNCHECKED";
		}
	}
	
	public void onBackPressed()
	{
		//Your code here
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
		
		selectedAcceptedSize = new Intent();
		getHeightTypedIn();
		getWidthTypedIn();
		if(selectedHeight == 1 && selectedWidth == 1 && selectedRadioButton == 4)
		{
			// set title
			alertDialogBuilder.setTitle("Custom filled but no width nor height filled"); 
			// set dialog message
			alertDialogBuilder
				.setMessage("You have selected custom but neither the width nor height was filled out." +
							"\nWould you like to change this or go with the default settings?" +
							"\nNote: Click YES to change your choice or click NO to go with the default settings")
				.setCancelable(false)
				.setPositiveButton("YES",new DialogInterface.OnClickListener() 
				{
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, close
						alertDialogSelector = 1;
						dialog.cancel();
					}
				  })	
				.setNegativeButton("No",new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						alertDialogSelector = 2;
						dialog.cancel();
						setSelectedSize();
					}
				});
			
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
		}
		else if(selectedRadioButton == 1 || selectedRadioButton == 2 || selectedRadioButton == 3)
		{
			selectedAcceptedSize.putExtra("selectedRadioButton", selectedRadioButton);
			selectedAcceptedSize.putExtra("selectedRadioButtonString",selectedRadioButtonString);
			selectedAcceptedSize.putExtra("qualityCompressValue", qualityCompressValue);
			setResult(RESULT_OK, selectedAcceptedSize);
			finish();
		}
		else if(selectedRadioButton == 4 && (selectedWidth > 1 && selectedWidth < MAX_Width_HEIGHT) && (selectedHeight > 1 && selectedHeight < MAX_Width_HEIGHT))
		{
			selectedAcceptedSize.putExtra("selectedRadioButton", selectedRadioButton);
			selectedAcceptedSize.putExtra("selectedRadioButtonString",selectedRadioButtonString);
			selectedAcceptedSize.putExtra("selectedWidth",selectedWidth);
			selectedAcceptedSize.putExtra("selectedHeight",selectedHeight);
			selectedAcceptedSize.putExtra("qualityCompressValue", qualityCompressValue);
			setResult(RESULT_OK, selectedAcceptedSize);
			finish();
		}
		else if(selectedRadioButton == 4 && ((selectedWidth > 1 && selectedWidth < MAX_Width_HEIGHT) && selectedHeight == 1))
		{
			// set title
			alertDialogBuilder.setTitle("Error!"); 
			// set dialog message
			alertDialogBuilder
			.setMessage("You must have both width and height filled out")
			.setCancelable(false)
			.setPositiveButton("OK",new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog,int id)
				{
					// if this button is clicked, close
					dialog.cancel();
				}
			});
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
		}
		else if(selectedRadioButton == 4 && (selectedWidth == 1 && (selectedHeight > 1 && selectedHeight < MAX_Width_HEIGHT)))
		{
			// set title
			alertDialogBuilder.setTitle("Error!"); 
			// set dialog message
			alertDialogBuilder
			.setMessage("You must have both width and height filled out")
			.setCancelable(false)
			.setPositiveButton("OK",new DialogInterface.OnClickListener()
			{
				public void onClick(DialogInterface dialog,int id)
				{
					// if this button is clicked, close
					dialog.cancel();
				}
			});
			
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
		}
		else if(selectedRadioButton == 4 && selectedWidth >= MAX_Width_HEIGHT && selectedHeight >= MAX_Width_HEIGHT)
		{
			//set title
			alertDialogBuilder.setTitle("Error!");
			//set dialog message
			alertDialogBuilder
			.setMessage("Range for width and height must be in between 0 and " + MAX_Width_HEIGHT)
			.setCancelable(false)
			.setPositiveButton("OK", new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					//cancel dialog
					dialog.cancel();
				}
			});
			
			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
		}
	}
				
	public void setEditTextFunction(int _width, int _height)
	{
		customWidthbtn.setText(Integer.toString(_width));
		customHeightbtn.setText(Integer.toString(_height));
	}
	
	public void setRadioButtonSelection(int setRadio2this, String setRadioString2this)
	{
		RadioButton mdsRadio = (RadioButton) findViewById(R.id.mobiledevicesizeRadio);
		RadioButton tabletRadio = (RadioButton) findViewById(R.id.tabletsizeRadio);
		RadioButton largeRadio = (RadioButton) findViewById(R.id.largesizeRadio);
		RadioButton customRadio = (RadioButton) findViewById(R.id.customsizeRadio);
		
		selectedSize.clearCheck();
		switch(setRadio2this)
		{
			case 1:
				mdsRadio.setChecked(true);
				selectedRadioButtonString = setRadioString2this;
				break;
			case 2:
				tabletRadio.setChecked(true);
				selectedRadioButtonString = setRadioString2this;
				break;
			case 3:
				largeRadio.setChecked(true);
				selectedRadioButtonString = setRadioString2this;
				break;
			case 4:
				customRadio.setChecked(true);
				selectedRadioButtonString = setRadioString2this;
				break;
		}
	}
		
	//Function to initialize RadioGroup
	public void setRadioGroupSection()
	{
		selectedSize = (RadioGroup)findViewById(R.id.SizeGroupButtons);
	}
	
	//Function to initialize Okay Button
	public void initializeButtons()
	{
		okayClicked = (Button)findViewById(R.id.btnOK);
	}
	
	public void setSelectedSize()
	{
		selectedRadioButton = 1;
		selectedRadioButtonString = "Mobile Device (320px x 480px)";
		selectedAcceptedSize.putExtra("selectedRadioButton", selectedRadioButton);
		selectedAcceptedSize.putExtra("selectedRadioButtonString",selectedRadioButtonString);
		selectedAcceptedSize.putExtra("aspectRatioSelected", aspectRatioSelected);
		setResult(RESULT_OK, selectedAcceptedSize);
		finish();
	}
	
	//Function to initialize all ActionListeners
	public void setActionListeners()
	{		
		aspectCheckBox.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				//is aspectCheckBox checked
				if (((CheckBox) v).isChecked() == true)
				{
					aspectRatioSelected = "CHECKED";
				}
				else if(((CheckBox) v).isChecked() == false)
				{
					aspectRatioSelected = "UNCHECKED";
				}
			}
		});
		
		selectedSize.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId)
			{
				switch(checkedId)
				{
				case R.id.mobiledevicesizeRadio:
					RadioButton mdsRadio = (RadioButton) findViewById(R.id.mobiledevicesizeRadio);
					String textString4Mobile = mdsRadio.getText().toString();
					selectedRadioButton = 1;
					selectedRadioButtonString = textString4Mobile;
					setVisiblityForCustom(2);
					break;
				case R.id.tabletsizeRadio:
					RadioButton tabletRadio = (RadioButton) findViewById(R.id.tabletsizeRadio);
					String textString4Tablet = tabletRadio.getText().toString();
					selectedRadioButton = 2;
					selectedRadioButtonString = textString4Tablet;
					setVisiblityForCustom(2);
					break;
				case R.id.largesizeRadio:
					RadioButton largeRadio = (RadioButton) findViewById(R.id.largesizeRadio);
					String textString4Large = largeRadio.getText().toString();
					selectedRadioButton = 3;
					selectedRadioButtonString = textString4Large;
					setVisiblityForCustom(2);
					break;
				case R.id.customsizeRadio:
					RadioButton customRadio = (RadioButton) findViewById(R.id.customsizeRadio);
					String textString4Custom = customRadio.getText().toString();
					selectedRadioButton = 4;
					selectedRadioButtonString = textString4Custom;
					setVisiblityForCustom(1);
					break;
				}
			}
		});
		
		okayClicked.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
				
				selectedAcceptedSize = new Intent();
				getHeightTypedIn();
				getWidthTypedIn();
				if(selectedHeight == 1 && selectedWidth == 1 && selectedRadioButton == 4)
				{
					// set title
					alertDialogBuilder.setTitle("Custom filled but no width nor height filled"); 
					// set dialog message
					alertDialogBuilder
						.setMessage("You have selected custom but neither the width nor height was filled out." +
									"\nWould you like to change this or go with the default settings?" +
									"\nNote: Click YES to change your choice or click NO to go with the default settings")
						.setCancelable(false)
						.setPositiveButton("YES",new DialogInterface.OnClickListener() 
						{
							public void onClick(DialogInterface dialog,int id) {
								// if this button is clicked, close
								alertDialogSelector = 1;
								dialog.cancel();
							}
						  })	
						.setNegativeButton("No",new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								// if this button is clicked, just close
								// the dialog box and do nothing
								alertDialogSelector = 2;
								dialog.cancel();
								setSelectedSize();
								updateSettingsChoice(selectedRadioButton, selectedRadioButtonString, selectedWidth, selectedHeight, aspectRatioSelected, qualityCompressValue);
							}
						});
					
					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();
	 
					// show it
					alertDialog.show();
				}
				else if(selectedRadioButton == 1 || selectedRadioButton == 2 || selectedRadioButton == 3)
				{
					selectedAcceptedSize.putExtra("selectedRadioButton", selectedRadioButton);
					selectedAcceptedSize.putExtra("selectedRadioButtonString",selectedRadioButtonString);
					selectedAcceptedSize.putExtra("aspectRatioSelected", aspectRatioSelected);
					selectedAcceptedSize.putExtra("qualityCompressValue", qualityCompressValue);
					updateSettingsChoice(selectedRadioButton, selectedRadioButtonString, selectedWidth, selectedHeight, aspectRatioSelected, qualityCompressValue);
					setResult(RESULT_OK, selectedAcceptedSize);
					finish();
				}
				else if(selectedRadioButton == 4 && (selectedWidth > 1 && selectedWidth < MAX_Width_HEIGHT) && (selectedHeight > 1 && selectedHeight < MAX_Width_HEIGHT))
				{
					selectedAcceptedSize.putExtra("selectedRadioButton", selectedRadioButton);
					selectedAcceptedSize.putExtra("selectedRadioButtonString",selectedRadioButtonString);
					selectedAcceptedSize.putExtra("selectedWidth",selectedWidth);
					selectedAcceptedSize.putExtra("selectedHeight",selectedHeight);
					selectedAcceptedSize.putExtra("aspectRatioSelected", aspectRatioSelected);
					selectedAcceptedSize.putExtra("qualityCompressValue", qualityCompressValue);
					updateSettingsChoice(selectedRadioButton, selectedRadioButtonString, selectedWidth, selectedHeight, aspectRatioSelected, qualityCompressValue);
					setResult(RESULT_OK, selectedAcceptedSize);
					finish();
				}
				else if(selectedRadioButton == 4 && ((selectedWidth > 1 && selectedWidth < MAX_Width_HEIGHT) && selectedHeight == 1))
				{
					// set title
					alertDialogBuilder.setTitle("Error!"); 
					// set dialog message
					alertDialogBuilder
						.setMessage("You must have both width and height filled out")
						.setCancelable(false)
						.setPositiveButton("OK",new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog,int id)
							{
								// if this button is clicked, close
								dialog.cancel();
							}
						});	
					
					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();
	 
					// show it
					alertDialog.show();
				}
				else if(selectedRadioButton == 4 && (selectedWidth == 1 && (selectedHeight > 1 && selectedHeight < MAX_Width_HEIGHT)))
				{
					// set title
					alertDialogBuilder.setTitle("Error!"); 
					// set dialog message
					alertDialogBuilder
						.setMessage("You must have both width and height filled out")
						.setCancelable(false)
						.setPositiveButton("OK",new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog,int id)
							{
								// if this button is clicked, close
								dialog.cancel();
							}
						});
					
					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();
	 
					// show it
					alertDialog.show();
				}
				else if(selectedRadioButton == 4 && selectedWidth >= MAX_Width_HEIGHT && selectedHeight >= MAX_Width_HEIGHT)
				{
					//set title
					alertDialogBuilder.setTitle("Error!");
					//set dialog message
					alertDialogBuilder
						.setMessage("Range for width and height must be in between 0 and " + MAX_Width_HEIGHT)
						.setCancelable(false)
						.setPositiveButton("OK", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which)
							{
								//cancel dialog
								dialog.cancel();
							}
						});
					
					// create alert dialog
					AlertDialog alertDialog = alertDialogBuilder.create();
	 
					// show it
					alertDialog.show();
				}
			}
		});
	}
	
	public void updateSettingsChoice(
			int _selectedRadioButton, String _selectedRadioButtonString, int _selectedWidth, 
			int _selectedHeight, String _aspectRatioSelected, int _qualityCompressValue)
	{
		SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean("info_saved", true);
		editor.putInt("selectedRadioButton_saved", _selectedRadioButton);
		editor.putString("selectedRadioButtonString_saved", _selectedRadioButtonString);
		editor.putInt("selectedWidth_saved", _selectedWidth);
		editor.putInt("selectedHeight_saved", _selectedHeight);
		editor.putString("aspectRatioSelected_saved", _aspectRatioSelected);
		editor.putInt("qualityCompressValue_saved", _qualityCompressValue);
		editor.commit();
	}
	
	public void getSettingsChoices()
	{
		SharedPreferences sharedPreferences = getPreferences(Context.MODE_PRIVATE);
		int saved_selectedRadioButton = sharedPreferences.getInt("selectedRadioButton_saved", selectedRadioButton);
		String saved_selectedRadioButtonString = sharedPreferences.getString("selectedRadioButtonString_saved", selectedRadioButtonString );
		int saved_selectedWidth = sharedPreferences.getInt("selectedWidth_saved", selectedWidth);
		int saved_selectedHeight = sharedPreferences.getInt("selectedHeight_saved", selectedHeight);
		String saved_aspectRatioSelected = sharedPreferences.getString("aspectRatioSelected_saved", aspectRatioSelected);
		int saved_qualityCompressValue = sharedPreferences.getInt("qualityCompressValue_saved", qualityCompressValue);
		selectedRadioButton = saved_selectedRadioButton;
		selectedRadioButtonString = saved_selectedRadioButtonString;
		selectedWidth = saved_selectedWidth;
		selectedHeight = saved_selectedHeight;
		aspectRatioSelected = saved_aspectRatioSelected;
		qualityCompressValue = saved_qualityCompressValue;
	}

	
	
	//Function to assign global variable height typed in
	public void getHeightTypedIn()
	{
		String customHeightString = (String) customHeightbtn.getText().toString();
		if(customHeightString.equals("") == true)
		{
			selectedHeight = 1;
		}
		else
		{
			selectedHeight = Integer.parseInt(customHeightString);
		}
	}
	
	//Function to assign global variable width typed in
	public void getWidthTypedIn()
	{
		String customWidthString = (String) customWidthbtn.getText().toString();
		if(customWidthString.equals("") == true)
		{
			selectedWidth = 1;
		}
		else
		{
			selectedWidth = Integer.parseInt(customWidthString);
		}
	}
}
