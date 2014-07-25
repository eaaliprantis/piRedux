package com.devcab.piredux;

//Import Android OS features
import android.os.*;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;

//Import Android Application features
import android.app.Activity;
import android.app.AlertDialog;

//Import Android content features
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

//Import Android Database
import android.database.Cursor;

//Import Bitmap Info
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;

//Import Android IO features
import java.io.File;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

//Import Language features
import java.lang.Math;
import java.lang.String;

//Import Java Math
import java.math.BigDecimal;

//Import Android URI
import android.net.Uri;

//Import Android Log feature
import android.util.Log;
import java.util.ArrayList;

//Import Android View Features
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;

//Import Android Widget Features
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity
{
	
	//Android URL
	String fullURL = null;
	
	//Java URI
	java.net.URI javaURI;
	
	//Android URI
	android.net.Uri androidURI;
	
	//features for Images
	Button btnAddFile;
	Button btnResizeSettings;
	String btnResizeOriginalText = "Set Resize";
	Button btnResizePhotos;
	
	Boolean loopFinished = true;
	
	//ArrayList URI
	ArrayList<Uri> arrayUri = new ArrayList<Uri>();
	ArrayList<String> sizeArrayList = new ArrayList<String>();
	ArrayList<Double> sizeArrayInByte = new ArrayList<Double>();
	ArrayList<Double> sizeArrayInKB = new ArrayList<Double>();
	ArrayList<Double> sizeArrayInMB = new ArrayList<Double>();
	ArrayList<Integer> sizeArrayWidth = new ArrayList<Integer>();
	ArrayList<Integer> sizeArrayHeight = new ArrayList<Integer>();
	ArrayList<String> sizeArrayMimeType = new ArrayList<String>();
	double totalSizeInMB = 0.00;
	int numOfPicsSelected = 0;
	
	//Final int
	final int RQS_LOADMULTIPLEIMAGE = 10;
	final int RQS_LOADIMAGE = 0;
	final int RQS_SENDEMAIL = 1;
	final int FINISHED_CONVERSION = 100;
	
	//Final int radioButton Selector
	final int radioButton_1 = 1;
	final int radioButton_2 = 2;
	final int radioButton_3 = 3;
	final int radioButton_4 = 4;
	
	//Global variable for Context
	final Context context = this;
	
	//int radio button selection
	int selectedRadioButton = 3;
		
	int radioButton_1_width = 320;
	int radioButton_1_height = 480;
	int radioButton_2_width = 1024;
	int radioButton_2_height = 768;
	int radioButton_3_width = 1920;
	int radioButton_3_height = 1080;
	int radioButton_4_width = 0;
	int radioButton_4_height = 0;
	
	int qualityCompressValue = 100;
	
	//Bitmap Factory Options Global
	BitmapFactory.Options bounds;
	Bitmap tempMap;
	Bitmap rescaledBitmap;
	Bitmap fixRotationBitmap;
	
	//String radio button selected String output
	String selectedRadioButtonString = "HD (1920px x 1080px)";
	String aspectRatioSelected = "CHECKED";
	int aspectRatioWidth = 0;
	int aspectRatioHeight = 0;
	
	//int selected width and height (only will be used if selectedRadioButton equals to 4
	int selectedWidth = 1;
	int selectedHeight = 1;
	
	//Global Variables for duplicate files
	int duplicateImage = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		//create Bundle package (required before anything else)
		super.onCreate(savedInstanceState);
		//display Content View (required before anything else)
		setContentView(R.layout.activity_main);
		
		//Intent features (necessary features needed to go to another layout)
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		String action = intent.getAction();
		
		//Functions that initialize the app
		//Function that initializes add button
		initializebtnAddFile();
		//Function that initializes resize settings button
		initializebtnResizeSettings();
		//Function that initializes resize photos button
		initializebtnResizePhotos();
		//Function that initializes visibility for all objects
		initialVisibility();
		
		
		//if ACTION_SEND_MULTIPLE is selected from the menu
		if(Intent.ACTION_SEND_MULTIPLE.equals(action))
		{
			if (extras == null)
			{
				return;
			}
			else if(extras.containsKey(Intent.EXTRA_STREAM))
			{
				ArrayList<Parcelable> list = intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
				for(Parcelable p : list)
				{
					Uri imageUri = (Uri) p;
					arrayUri.add(imageUri);
					long fileSize = 0;
					String fileSizeString = getImageSize(imageUri);
					long fileSizeLong = Long.parseLong(fileSizeString);
					int fileSizeInt = Integer.parseInt(fileSizeString);
					fileSize = fileSizeInt;
					
					double fileSizeInByte = fileSizeLong;
					double fileSizeInKB = fileSizeLong / 1024;
					double fileSizeInMB = fileSizeInKB / 1024;
					double fileSizeInMBPercision = roundMB(fileSizeInMB, 2, BigDecimal.ROUND_HALF_UP);
					fileSizeString = Double.toString(fileSizeInMBPercision);
					if(fileSize == 0)
					{
						sizeArrayList.add("N/A");
						sizeArrayInByte.add(0.00);
						sizeArrayInKB.add(0.00);
						sizeArrayInMB.add(0.00);
					}
					else
					{
						sizeArrayList.add(fileSizeString + " MB");
						sizeArrayInByte.add(fileSizeInByte);
						sizeArrayInKB.add(fileSizeInKB);
						sizeArrayInMB.add(fileSizeInMB);
					}
					totalSizeInMB += fileSizeInMBPercision;
					updateNumPhotoTotalSizeViews(numOfPicsSelected, totalSizeInMB);
				}
				return;
			}
		}
		// if this is from the share menu
		else if (Intent.ACTION_SEND.equals(action))
	    {
			if (extras == null)
			{
				return;
			}
			else if (extras.containsKey(Intent.EXTRA_STREAM))
	        {
	            try
	            {
	                // Get resource path from intent called
	                Uri imageUri = (Uri) extras.getParcelable(Intent.EXTRA_STREAM);
	                arrayUri.add(imageUri);
	                long fileSize = 0;
	    			String fileSizeString = getImageSize(imageUri);
	    			long fileSizeLong = Long.parseLong(fileSizeString);
	    			int fileSizeInt = Integer.parseInt(fileSizeString);
	    			fileSize = fileSizeInt;
	    			
	    			double fileSizeInByte = fileSizeLong;
	    			double fileSizeInKB = fileSizeLong / 1024;
	    			double fileSizeInMB = fileSizeInKB / 1024;
	    			double fileSizeInMBPercision = roundMB(fileSizeInMB, 2, BigDecimal.ROUND_HALF_UP);
	    			fileSizeString = Double.toString(fileSizeInMBPercision);
					if(fileSize == 0)
					{
						sizeArrayList.add("N/A");
						sizeArrayInByte.add(0.00);
						sizeArrayInKB.add(0.00);
						sizeArrayInMB.add(0.00);
					}
					else
					{
						sizeArrayList.add(fileSizeString + " MB");
						sizeArrayInByte.add(fileSizeInByte);
						sizeArrayInKB.add(fileSizeInKB);
						sizeArrayInMB.add(fileSizeInMB);
					}
					totalSizeInMB += fileSizeInMBPercision;
					numOfPicsSelected = arrayUri.size();
					updateNumPhotoTotalSizeViews(numOfPicsSelected, totalSizeInMB);				
	    			return;
	            }
	            catch (Exception e)
	            {
	                Log.e(this.getClass().getName(), e.toString());
	            }

	        } else if (extras.containsKey(Intent.EXTRA_TEXT))
	        {
	            return;
	        }
		}
		else
		{
			//Function that sets all buttons visible
			setAllButtonsVisible();
		}
	}
	
	public void updateNumPhotoTotalSizeViews(int _numberOfPhotos, double _totalPhotoSizes)
	{
		TextView numPhotoTextView = (TextView)findViewById(R.id.numPhotosView);
		numPhotoTextView.setText(Integer.toString(_numberOfPhotos) + " photo(s)");
		TextView totalSizeTextView = (TextView)findViewById(R.id.totalSizeView);
		totalSizeTextView.setText(Double.toString(_totalPhotoSizes) + " MB");
	}

	public void onBackPressed()
	{
		super.onBackPressed();
	}
	
	//Function that is called when Set Resize button is clicked
	OnClickListener btnResizeSettingsListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			//Create first intent to call resizeSettings
			Intent request = new Intent(MainActivity.this, resizeSettings.class);
			request.putExtra("selectedRadioButton", selectedRadioButton);
			request.putExtra("selectedRadioButtonString", selectedRadioButtonString);
			request.putExtra("selectedWidth", selectedWidth);
			request.putExtra("selectedHeight", selectedHeight);
			request.putExtra("aspectRatioSelected", aspectRatioSelected);
			request.putExtra("qualityCompressValue", qualityCompressValue);
			
			//Start new Resizing Settings and wait for its results
			startActivityForResult(request, selectedRadioButton);
		}
	};
	
	//Function that is called when Resize Photos is clicked
	OnClickListener btnResizePhotosListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			bitmapCompression();
		}
	};
	
	public void updateSettingsChoice(
			String _selectedRadioButton, 
			String _selectedRadioButtonString, 
			String _selectedWidth, 
			String _selectedHeight,
			String _aspectRatioSelected, 
			String _qualityCompressValue)
	{
		SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString("selectedRadioButton_saved", _selectedRadioButton);
		editor.putString("selectedRadioButtonString_saved", _selectedRadioButtonString);
		editor.putString("selectedWidth_saved", _selectedWidth);
		editor.putString("selectedHeight_saved", _selectedHeight);
		editor.putString("aspectRatioSelected_saved", _aspectRatioSelected);
		editor.putString("qualityCompressValue_saved", _qualityCompressValue);
		editor.commit();
    }

	
	public void bitmapCompression()
	{
		int arraySize = arrayUri.size();
		int setDesiredWidth = 0;
		int setDesiredHeight = 0;
		if(selectedRadioButton == 1)
		{
			setDesiredWidth = radioButton_1_height;
			setDesiredHeight = radioButton_1_width;
		}
		else if(selectedRadioButton == 2)
		{
			setDesiredWidth = radioButton_2_width;
			setDesiredHeight = radioButton_2_height;
		}
		else if(selectedRadioButton == 3)
		{
			setDesiredWidth = radioButton_3_width;
			setDesiredHeight = radioButton_3_height;
		}
		else if(selectedRadioButton == 4)
		{
			setDesiredWidth = radioButton_4_width;
			setDesiredHeight = radioButton_4_height;
		}
		else
		{
			setDesiredWidth = radioButton_3_width;
			setDesiredHeight = radioButton_3_height;
		}
		
		if(arraySize == arrayUri.size() &&
				arraySize == sizeArrayList.size() &&
				arraySize == sizeArrayInByte.size() &&
				arraySize == sizeArrayInKB.size() &&
				arraySize == sizeArrayInMB.size() &&
				arraySize == sizeArrayWidth.size() &&
				arraySize == sizeArrayHeight.size() )
		{
			if(arrayUri.size() == 1)
			{
				int setSampleScale = getSampleSize(sizeArrayWidth.get(0), sizeArrayHeight.get(0), setDesiredWidth, setDesiredHeight);
				if(aspectRatioSelected.equals("CHECKED"))
				{
					createScaledBitmapForEachImage(aspectRatioWidth, aspectRatioHeight, setSampleScale, 0);
				}
				else if(aspectRatioSelected.equals("UNCHECKED"))
				{
					createScaledBitmapForEachImage(setDesiredWidth, setDesiredHeight, setSampleScale, 0);
					
				}
				//NOTE:  CLEAR ALL ARRAYLIST, LISTVIEW, EVERYTHING
				clearAllFeaturesAndArrayLists();
				
				//Function that sets all buttons visible
				setAllButtonsVisible();
			}
			else if(arrayUri.size() > 1)
			{
				for(int i = 0; i < arrayUri.size(); i++)
				{
					int setSampleScale = getSampleSize(sizeArrayWidth.get(i), sizeArrayHeight.get(i), setDesiredWidth, setDesiredHeight);
					if(aspectRatioSelected.equals("CHECKED"))
					{
						createScaledBitmapForEachImage(aspectRatioWidth, aspectRatioHeight, setSampleScale, i);
					}
					else if(aspectRatioSelected.equals("UNCHECKED"))
					{
						createScaledBitmapForEachImage(setDesiredWidth, setDesiredHeight, setSampleScale, i);
					}
				}
				
				//NOTE:  CLEAR ALL ARRAYLIST, IMAGEVIEW, EVERYTHING
				clearAllFeaturesAndArrayLists();
				
				//Function that sets all buttons visible
				setAllButtonsVisible();
			}
			else if(arrayUri.size() == 0)
			{
				//Your code here
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
				
				// set title
				alertDialogBuilder.setTitle("Custom filled but no width nor height filled");
				
				// set dialog message
				alertDialogBuilder
				.setMessage("You have selected no photos. Would you like to choose from the gallery?")
				.setCancelable(true)
				.setPositiveButton("YES",new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog,int id)
					{
						// if this button is clicked, close
						dialog.cancel();
						Intent intent = new Intent
						(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
						);
						startActivityForResult(intent, RQS_LOADIMAGE);
					}
				})
				.setNegativeButton("NO",new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog,int id)
					{
						// if this button is clicked, just close
						// the dialog box and do nothing
						dialog.cancel();
					}
				});
				
				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();
				
				// show it
				alertDialog.show();
			}
		}
	}
		
	//Method to clear all array list and adapters
	public void clearAllFeaturesAndArrayLists()
	{
		arrayUri.clear();
		sizeArrayList.clear();
		sizeArrayInByte.clear();
		sizeArrayInKB.clear();
		sizeArrayInMB.clear();
		sizeArrayWidth.clear();
		sizeArrayHeight.clear();
		sizeArrayMimeType.clear();
		totalSizeInMB = 0.00;
		numOfPicsSelected = 0;
		updateNumPhotoTotalSizeViews(numOfPicsSelected, totalSizeInMB);	
	}
	
	//Method to set the aspectRatioWidth and aspectRatioHeight
	public void setDesiredWidthAndHeight(int desiredWidth, int desiredHeight)
	{
		aspectRatioWidth = desiredWidth;
		aspectRatioHeight = desiredHeight;
	}
	
	//Method to return the sample size and sets the desiredWidth and desiredHeight correctly if aspectRatio is selected
	public int getSampleSize(int imageWidth, int imageHeight, int desiredWidth, int desiredHeight)
	{
		
		if(aspectRatioSelected.equals("CHECKED"))
		{
			double accurateAspectRatio = (double) imageWidth / (double)imageHeight;
			double doubleDesiredHeight = 0;
			double doubleDesiredWidth = 0;
			if(imageWidth > imageHeight)
			{
				doubleDesiredHeight = desiredWidth / accurateAspectRatio;
				doubleDesiredWidth = desiredWidth;
			}
			else if(imageWidth < imageHeight)
			{
				doubleDesiredWidth = desiredHeight * accurateAspectRatio;
				doubleDesiredHeight = desiredHeight;
			}
			else if(imageWidth == imageHeight)
			{
				if(desiredWidth > desiredHeight)
				{
					doubleDesiredWidth = desiredWidth;
					doubleDesiredHeight = doubleDesiredWidth;	
				}
				else if(desiredWidth < desiredHeight)
				{
					doubleDesiredHeight = desiredHeight;
					doubleDesiredWidth = doubleDesiredHeight;
				}
			}
			desiredWidth = (int) Math.round(doubleDesiredWidth);
			desiredHeight = (int) Math.round(doubleDesiredHeight);
			setDesiredWidthAndHeight(desiredWidth, desiredHeight);
			
			return (int) Math.round(accurateAspectRatio);
		}
		else if(aspectRatioSelected.equals("UNCHECKED"))
		{
			if(imageWidth > desiredWidth)
			{
				if(imageHeight > desiredHeight)
				{
					return Math.max( (imageWidth / desiredWidth), (imageHeight / desiredHeight) );
				}
				else if(imageHeight < desiredHeight)
				{
					return Math.max( (imageWidth / desiredWidth), (desiredWidth / desiredHeight) );
				}
			}
			else if(imageWidth < desiredWidth)
			{
				if(imageHeight > desiredHeight)
				{
					return Math.max( (desiredWidth / imageWidth), (imageHeight / desiredHeight));
				}
				else if(imageHeight < desiredHeight)
				{
					return Math.max( (desiredWidth / imageWidth), (desiredHeight / imageHeight));
				}
			}
			else if(imageWidth == desiredWidth)
			{
				if(imageHeight == desiredHeight)
				{
					return 1;
				}
			}
		}
		return Math.max( (imageWidth / desiredWidth), (imageHeight / desiredHeight) );
	}
		
	//Function that creates a scaled bitmap for each resized image
	public void createScaledBitmapForEachImage(int setDesiredWidth, int setDesiredHeight, int sampleSizeInt, int indexPlacement)
	{		
		Bitmap rescaledBitmap = getBitmapRescaled(arrayUri.get(indexPlacement), sampleSizeInt, setDesiredWidth, setDesiredHeight, indexPlacement);
		clearGarbageCollector();
		
		String fileNameString = getFileNameFromURI(arrayUri.get(indexPlacement)) + sizeArrayMimeType.get(indexPlacement);
		
		try
		{
			saveToFileAndUri(rescaledBitmap, sizeArrayMimeType.get(indexPlacement), fileNameString);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		rescaledBitmap.recycle();
		fixRotationBitmap.recycle();
		clearGarbageCollector();
	}
	
	//Function that saves the Bitmap to your gallery and returns the content that is stored into the URI created
	public Uri saveToFileAndUri(Bitmap bitmap, String mimeTypeString, String _fileName) throws Exception
	{
		String fileName = _fileName;
		if(mimeTypeString.equals(".jpeg") || mimeTypeString.equals(".jpg"))
		{
			fileName = _fileName;
		}
		else if(mimeTypeString.equals(".png"))
		{
			fileName = _fileName;
		}
		File extBaseDir = Environment.getExternalStorageDirectory();
		File file = new File(extBaseDir.getAbsoluteFile() + "/piRedux");
		
		if(!file.exists())
		{
			if(!file.mkdirs())
			{
				throw new Exception("Could not create directories, "+ file.getAbsolutePath());
			}
		}
		String filePath = file.getAbsolutePath() + "/" + fileName;
		File checkIfExist = new File(filePath);
		if(checkIfExist.exists() == true)
		{
			String newFilePath = file.getAbsolutePath() + "/" + duplicateImage + "_" + fileName;
			filePath = newFilePath;
			duplicateImage++;
		}
		FileOutputStream out = new FileOutputStream(filePath);
		
		if(mimeTypeString.equals(".jpeg") || mimeTypeString.equals(".jpg"))
		{
			bitmap.compress(Bitmap.CompressFormat.JPEG, qualityCompressValue, out);
		}
		else if(mimeTypeString.equals("png"))
		{
			bitmap.compress(Bitmap.CompressFormat.PNG, qualityCompressValue, out);
		}
		out.flush();
		out.close();
		
		long size = new File(filePath).length();
		
		ContentValues values = new ContentValues(7);
		values.put(Images.Media.TITLE, fileName);
		// That filename is what will be handed to Email when a user shares a
		// photo. Email gets the name of the picture attachment from the
		// "DISPLAY_NAME" field.
		values.put(Images.Media.DISPLAY_NAME, fileName);
		if(mimeTypeString.equals(".jpg") || mimeTypeString.equals(".jpeg"))
		{
			values.put(Images.Media.MIME_TYPE, "image/jpeg");
		}
		else if(mimeTypeString.equals(".png"))
		{
			values.put(Images.Media.MIME_TYPE, "image/png");
		}
		values.put(Images.Media.ORIENTATION, 0);
		values.put(Images.Media.DATA, filePath);
		values.put(Images.Media.SIZE, size);
		
		return MainActivity.this.getContentResolver().insert(Images.Media.EXTERNAL_CONTENT_URI, values);
    }

	//Function that returns the file name of the URI being sent in
	public String getFileNameFromURI(Uri uriImg)
	{
		File file = new File(uriImg.toString());
		String uriString = file.getAbsolutePath().toString();
		
		int fileNameInt = uriString.lastIndexOf("/");
		if(uriString.contains("/"))
		{
			fileNameInt = uriString.lastIndexOf("/");
		}
		else if(uriString.contains("\\"))
		{
			fileNameInt = uriString.lastIndexOf("\\");
		}
		String fileName = uriString.substring(fileNameInt + 1, uriString.length()) + "_resized";
		return fileName;
	}

	//Function to clear the Garbage Collector for possible memory issues (OutOfMemoryException)
	public void clearGarbageCollector()
	{
		System.gc();
	}
	
	public int convertExif2Degress(int degreesConvert)
	{
		int returnValue = 0;
		if(degreesConvert == 90)
		{
			returnValue = 90;
		}
		else if(degreesConvert == 180)
		{
			returnValue = 180;
		}
		else if(degreesConvert == 270)
		{
			returnValue = 270;
		}
		else
		{
			returnValue = 0;
		}
		
		return returnValue;
	}
	
	public int getOrientation(Context context, Uri photoUri)
	{
		int rotateIntValue = 0;
		Cursor cursor = context.getContentResolver().query(photoUri, new String[] {MediaStore.Images.ImageColumns.ORIENTATION}, null, null, null);
		if(cursor.getCount() != 1)
		{
			rotateIntValue = -1;
		}
		else
		{
			cursor.moveToFirst();
			rotateIntValue = cursor.getInt(0);
		}
		cursor.close();
		return rotateIntValue;
	}
	
	//Method to resize the bitmap
	public Bitmap getBitmapRescaled(Uri imageUri, int sampleSizeInt, int setDesiredWidth, int setDesiredHeight, int indexPlacement)
	{
		Uri imageURI = imageUri;
		int imageRotation = getOrientation(this, imageURI);
		int finalRotation = convertExif2Degress(imageRotation);
		
		Matrix matrix = new Matrix();
		if (finalRotation != 0f)
		{
			matrix.postRotate(finalRotation);
		}
		ContentResolver resolver = getContentResolver();
		InputStream inputStream;
		try
		{
			inputStream = resolver.openInputStream(imageURI);
			bounds = new BitmapFactory.Options();
			bounds.inJustDecodeBounds = false;
			bounds.inSampleSize = sampleSizeInt;
			bounds.inPreferredConfig=Bitmap.Config.ARGB_8888;
			Bitmap bitmap_Source = BitmapFactory.decodeStream(inputStream,null,bounds);
			rescaledBitmap =  Bitmap.createScaledBitmap(bitmap_Source, setDesiredWidth, setDesiredHeight, true);
			if(bounds.outWidth == -1 || bounds.outHeight == -1)
			{
				//bitmap is null
				return null;
			}
			bitmap_Source.recycle();
			fixRotationBitmap = Bitmap.createBitmap(rescaledBitmap, 0, 0, rescaledBitmap.getWidth(), rescaledBitmap.getHeight(), matrix, true);
			rescaledBitmap.recycle();
			inputStream.close();
			resolver = null;
			clearGarbageCollector();
			return fixRotationBitmap;
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
			
	//Function that is called when Add File is clicked
	OnClickListener btnAddFileOnClickListener = new OnClickListener()
	{
		@Override
		public void onClick(View v)
		{
			Intent intent = new Intent
			(
				Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI
			);
			startActivityForResult(intent, RQS_LOADIMAGE);
		}
	};
		
	//Function that is called when activityResult is called
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		Bundle extras = data.getExtras();
		String action = data.getAction();
		
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == 0)
		{
			
		}
		else
		{
			int setSelectedRadioButton = data.getExtras().getInt("selectedRadioButton");
			requestCode = setSelectedRadioButton;
		}
		
		switch(requestCode)
		{
			case radioButton_1:
				if(resultCode == RESULT_OK)
				{
					//Get color string that returns from our resizeSettings
					int getSelectedRadioButton1 = data.getExtras().getInt("selectedRadioButton");
					String getSelectedRadioButtonString1 = data.getExtras().getString("selectedRadioButtonString");
					selectedRadioButton = getSelectedRadioButton1;
					selectedRadioButtonString = getSelectedRadioButtonString1;
					aspectRatioSelected = data.getExtras().getString("aspectRatioSelected");
					int compressedQualityValue = data.getExtras().getInt("qualityCompressValue");
					qualityCompressValue = compressedQualityValue;
					setSelectedRadioButtons();
				}
				else if(resultCode == RESULT_CANCELED)
				{
					
				}
				break;
			case radioButton_2:
				if(resultCode == RESULT_OK)
				{
					int getSelectedRadioButton2 = data.getExtras().getInt("selectedRadioButton");
					String getSelectedRadioButtonString2 = data.getExtras().getString("selectedRadioButtonString");
					selectedRadioButton = getSelectedRadioButton2;
					selectedRadioButtonString = getSelectedRadioButtonString2;
					aspectRatioSelected = data.getExtras().getString("aspectRatioSelected");
					int compressedQualityValue = data.getExtras().getInt("qualityCompressValue");
					qualityCompressValue = compressedQualityValue;
					setSelectedRadioButtons();
				}
				else if(resultCode == RESULT_CANCELED)
				{
					
				}
				break;
			case radioButton_3:
				if(resultCode == RESULT_OK)
				{
					int getSelectedRadioButton3 = data.getExtras().getInt("selectedRadioButton");
					String getSelectedRadioButtonString3 = data.getExtras().getString("selectedRadioButtonString");
					selectedRadioButton = getSelectedRadioButton3;
					selectedRadioButtonString = getSelectedRadioButtonString3;
					aspectRatioSelected = data.getExtras().getString("aspectRatioSelected");
					int compressedQualityValue = data.getExtras().getInt("qualityCompressValue");
					qualityCompressValue = compressedQualityValue;
					setSelectedRadioButtons();
				}
				else if(resultCode == RESULT_CANCELED)
				{
					
				}
				break;
			case radioButton_4:
				if(resultCode == RESULT_OK)
				{
					int getSelectedRadioButton4 = data.getExtras().getInt("selectedRadioButton");
					String getSelectedRadioButtonString4 = data.getExtras().getString("selectedRadioButtonString");
					int getSelectedWidth = data.getExtras().getInt("selectedWidth");
					int getSelectedHeight = data.getExtras().getInt("selectedHeight");
					selectedRadioButton = getSelectedRadioButton4;
					selectedRadioButtonString = getSelectedRadioButtonString4;
					selectedWidth = getSelectedWidth;
					selectedHeight = getSelectedHeight;
					aspectRatioSelected = data.getExtras().getString("aspectRatioSelected");
					int compressedQualityValue = data.getExtras().getInt("qualityCompressValue");
					qualityCompressValue = compressedQualityValue;
					setSelectedRadioButtonWidthHeight();
				}
				else if(resultCode == RESULT_CANCELED)
				{
					
				}
				break;
			case RQS_LOADMULTIPLEIMAGE:
				if (Intent.ACTION_SEND_MULTIPLE.equals(action) && data.hasExtra(Intent.EXTRA_STREAM) && resultCode == RESULT_OK)
				{
					if(extras.containsKey(Intent.EXTRA_STREAM))
					{
						ArrayList<Parcelable> list = data.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
						for(Parcelable p : list)
						{
							Uri imageUri = (Uri) p;
							arrayUri.add(imageUri);
							long fileSize = 0;
							String fileSizeString = getImageSize(imageUri);
							long fileSizeLong = Long.parseLong(fileSizeString);
							int fileSizeInt = Integer.parseInt(fileSizeString);
							fileSize = fileSizeInt;
							
							double fileSizeInByte = fileSizeLong;
							double fileSizeInKB = fileSizeLong / 1024;
							double fileSizeInMB = fileSizeInKB / 1024;
							double fileSizeInMBPercision = roundMB(fileSizeInMB, 2, BigDecimal.ROUND_HALF_UP);
							fileSizeString = Double.toString(fileSizeInMBPercision);
							if(fileSize == 0)
							{
								sizeArrayList.add("N/A");
								sizeArrayInByte.add(0.00);
								sizeArrayInKB.add(0.00);
								sizeArrayInMB.add(0.00);
							}
							else
							{
								sizeArrayList.add(fileSizeString + " MB");
								sizeArrayInByte.add(fileSizeInByte);
								sizeArrayInKB.add(fileSizeInKB);
								sizeArrayInMB.add(fileSizeInMB);
							}
							totalSizeInMB += fileSizeInMBPercision;
							numOfPicsSelected = arrayUri.size();
							updateNumPhotoTotalSizeViews(numOfPicsSelected, totalSizeInMB);	
						}
					}
			    	break;
				}
				else if(resultCode == RESULT_CANCELED)
				{
					
				}
				break;
				
			case RQS_LOADIMAGE:
				if(resultCode == RESULT_OK)
				{
					Uri imageUri = data.getData();
					arrayUri.add(imageUri);
					long fileSize = 0;
					String fileSizeString = getImageSize(imageUri);
					long fileSizeLong = Long.parseLong(fileSizeString);
					int fileSizeInt = Integer.parseInt(fileSizeString);
					fileSize = fileSizeInt;
					
					double fileSizeInByte = fileSizeLong;
					double fileSizeInKB = fileSizeLong / 1024;
					double fileSizeInMB = fileSizeInKB / 1024;
					double fileSizeInMBPercision = roundMB(fileSizeInMB, 2, BigDecimal.ROUND_HALF_UP);
					fileSizeString = Double.toString(fileSizeInMBPercision);
					if(fileSize == 0)
					{
						sizeArrayList.add("N/A");
						sizeArrayInByte.add(0.00);
						sizeArrayInKB.add(0.00);
						sizeArrayInMB.add(0.00);
					}
					else
					{
						sizeArrayList.add(fileSizeString + " MB");
						sizeArrayInByte.add(fileSizeInByte);
						sizeArrayInKB.add(fileSizeInKB);
						sizeArrayInMB.add(fileSizeInMB);
					}
					totalSizeInMB += fileSizeInMBPercision;
					numOfPicsSelected = arrayUri.size();
					updateNumPhotoTotalSizeViews(numOfPicsSelected, totalSizeInMB);	
				}
				else if(resultCode == RESULT_CANCELED)
				{
					
				}
				break;
		}
	}
	
	public static double roundMB(double unrounded, int precision, int roundingMode)
	{
	    BigDecimal bd = new BigDecimal(unrounded);
	    BigDecimal rounded = bd.setScale(precision, roundingMode);
	    return rounded.doubleValue();
	}
	
	public String getImageSize(Uri ImageURI)
	{
		ContentResolver resolver = getContentResolver();
		InputStream is;
		int sizeOfImg = 0;
		try
		{
			is = resolver.openInputStream(ImageURI);
			bounds = new BitmapFactory.Options();
			bounds.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(is,null,bounds);
			int width = bounds.outWidth;
			int height = bounds.outHeight;
			String mimeTypeImg = bounds.outMimeType;

			
			sizeArrayWidth.add(width);
			sizeArrayHeight.add(height);
			int mimeTypeImgInt = mimeTypeImg.lastIndexOf("/");
			String subMimeType = mimeTypeImg.substring((mimeTypeImgInt + 1));
			String extensionString = "." + subMimeType;
			sizeArrayMimeType.add(extensionString);
			
			sizeOfImg = is.available();
			is.close();
			resolver = null;
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		String tempSize = Integer.toString(sizeOfImg);
		return tempSize;
	}
	
	//Function that is called when settings the text for the selected Radio Button
	public void setSelectedRadioButtons()
	{
		btnResizeSettings.setText(btnResizeOriginalText + ": " + selectedRadioButtonString);
	}
	
	public void setSelectedRadioButtonWidthHeight()
	{
		btnResizeSettings.setText(btnResizeOriginalText + ": " + selectedRadioButtonString + " (" + selectedWidth + " by " + selectedHeight + "px)");
		radioButton_4_width = selectedWidth;
		radioButton_4_height = selectedHeight;
	}

	//Function that sets All the buttons visible
	public void setAllButtonsVisible()
	{
		btnAddFile.setVisibility(View.VISIBLE);
		btnResizeSettings.setVisibility(View.VISIBLE);
		btnResizePhotos.setVisibility(View.VISIBLE);
	}
	
	//Function that sets All the buttons invisible
	public void setAllButtonsInVisible()
	{
		btnAddFile.setVisibility(View.INVISIBLE);
		btnResizeSettings.setVisibility(View.INVISIBLE);
		btnResizePhotos.setVisibility(View.INVISIBLE);
	}
	
	//Function that initializes all the buttons/list view visibility
	public void initialVisibility()
	{
		btnAddFile.setVisibility(View.INVISIBLE);
		btnResizeSettings.setVisibility(View.VISIBLE);
		btnResizePhotos.setVisibility(View.VISIBLE);
	}
		
	//Function that initializes add files
	public void initializebtnAddFile()
	{
		btnAddFile = (Button)findViewById(R.id.addphoto);
		btnAddFile.setVisibility(View.INVISIBLE);
		btnAddFile.setOnClickListener(btnAddFileOnClickListener);	
	}
	
	//Function that initializes resize settings
	public void initializebtnResizeSettings()
	{
		btnResizeSettings = (Button)findViewById(R.id.setResizeSettings);
		btnResizeSettings.setVisibility(View.VISIBLE);
		btnResizeSettings.setOnClickListener(btnResizeSettingsListener);
		setSelectedRadioButtons();
	}
	
	//Function that initializes resize photos
	public void initializebtnResizePhotos()
	{
		btnResizePhotos = (Button)findViewById(R.id.resizephotos);
		btnResizePhotos.setVisibility(View.VISIBLE);
		btnResizePhotos.setOnClickListener(btnResizePhotosListener);
	}	
	
	//Function that is called when creating the menu/features
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
}
