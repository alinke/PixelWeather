package com.ledpixelart.weather;



//import ioio.lib.api.AnalogInput;
import ioio.lib.api.IOIO.VersionType;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.text.format.Time;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.VideoView;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.MenuInflater;
import android.media.MediaPlayer;
import android.os.StrictMode;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;



//import com.exoplatform.weather.controller.ActivityScreenLocation;
//import com.exoplatform.weather.controller.ActivityWeatherSetting;
//import com.exoplatform.weather.controller.ActivityWeatherSetting.HandleSelectContextMenu;
import com.exoplatform.weather.model.WeatherDataModel;
import com.exoplatform.weather.model.WeatherInfo;
import com.exoplatform.weather.model.WeatherPreferences;
import com.exoplatform.weather.model.YahooWeatherHelper;
import com.exoplatform.weather.view.ContextMenuAdapter;
import com.exoplatform.weather.view.ContextMenuItem;


@SuppressLint("NewApi")
public class pixelWeather extends IOIOActivity {
	
	private TextView forecastTextView_;
	private TextView currentLocationTextView_;
	
	private TextView currentTempTextView_;
	private TextView currentDescriptionTextView_;
	private TextView currentHighTextView_;
	private TextView currentLowTextView_;
	private TextView lastUpdatedTextView_;
	
//	private TextView proximity_label_;
//	private TextView pot_label_;
//	private  int proximityPinNumber = 34; //the pin used on IOIO for the alchohol sensor input
//	private  int potPinNumber = 35; //the pin used on IOIO for the alchohol sensor input
//	private  int weatherSwitchPinNumber = 36; //the pin used on IOIO for the alchohol sensor input
//	private  int stockSwitchPinNumber = 37; //the pin used on IOIO for the alchohol sensor input	
//	private  int ledPinNumber = 38; //the pin used on IOIO for the alchohol sensor input
	
	private static ioio.lib.api.RgbLedMatrix matrix_;
	private static ioio.lib.api.RgbLedMatrix.Matrix KIND;  //have to do it this way because there is a matrix library conflict
	private static android.graphics.Matrix matrix2;
	
	private static short[] frame_;
  	public static final Bitmap.Config FAST_BITMAP_CONFIG = Bitmap.Config.RGB_565;
  	private static byte[] BitmapBytes;
  	private static InputStream BitmapInputStream;
  	private static Bitmap canvasBitmap;
  	private static Bitmap IOIOBitmap;
  	private static Bitmap originalImage;
  	private static int width_original;
  	private static int height_original; 	  
  	private static float scaleWidth; 
  	private static float scaleHeight; 	  	
  	private static Bitmap resizedBitmap;  
  	private int matrix_model;
  	private int weatherObtained = 0;
  	
    private static Context context;
    private Context frameContext;
  	
  	private static final String LOG_TAG = "PixelWeather";	

//	private MediaController mc;
//	private VideoView vid;	
	//private MediaController idleMC;   
	//private VideoView idleVid;		
	
	
	private int i = 0;	
	private int characterChangedFlag = 0;
	private int deviceFound = 0;
	private float potRead = 0;
	private float potReadLast = 0;
	private float potDifference = 0;
	private double proxRead = 0;
	private int proxCounter = 0;
	private int proxMatches = 0;
	private int playingFlag = 0;
	private int idlePlayingFlag = 0;
	private int videoCounter = 0;
	private int weatherVideoCounter = 0;
	private int stockVideoCounter = 0;
	private int brightness;

		
	
	private Handler mHandler;
//	private String character;
	private SharedPreferences prefs;
	private String app_ver;	
	private static Resources resources;
	
	private boolean debug;
	private boolean stealth;
	private boolean proximity_sensor;
	private boolean custom_videos;
	private int min_baseline;
	private int max_value;	
	private double stock_goodThreshold;
	private double stock_badThreshold;
	private int character;
	private TextView proximityValue_ = null;
	private TextView potValue_ = null;
	
	
	
	//string text
	///********** Localization String from @string *************
	private String userAcceptanceString; 
	private String userAcceptanceStringTitle;
	private String setupInstructionsString; 
	private String setupInstructionsStringTitle;
	private String notFoundString; 
	private String notFoundStringTitle;
	
	private String instructionsString; 
	private String instructionsStringTitle;
	
	private String batteryLifeString;
	private String batteryLifeStringTitle;
	
	private String blewTooSoonString; 
	private String blewTooSoonStringTitle;
	private String OKText;
	private String AcceptText;
	
	private String level1Result;
	private String level2Result;
	private String level3Result;
	private String level4Result;
	
	private String analyzingText;
	private String justAmomentText;
		
	private String blowForText;
	private String pleaseWaitText;
	private String statusSimulationModeText;
	private String statusResettingText;	
	private String statusInprogressText;
	private String tapTobeginText;
	private String statusReadyText;
	private String statusNotconnectedText;
	private String blowPrompt;
	private String weatherCity;
	
	
	/** Change location */
	private static final int REG_CHANGELOCATION = 1;	
	/** Request get location */
	private static final int REG_CHANGESTOCKS = 5;	
	/** Request get location */
	private static final int REG_GET_WEATHER_START = 100;	
	/** Request get location finish */
	private static final int REG_GET_WEATHER_FINISH = 101;	
	/** Frequency update */
	private static final int ONE_MINUTE = 60*1000;	
	/** Context menu */
	private static final int MENU_CONTEXT_0 = 0;		
	/** Context menu */
	private static final int MENU_CONTEXT_1 = 1;	
	/** Context menu */
	private static final int MENU_CONTEXT_2 = 2;	
	/** Item 1 */
	private static final int SELECT_ITEM_1 = 0;	
	/** Item 2 */
	private static final int SELECT_ITEM_2 = 1;	
	/** Item 3 */
	private static final int SELECT_ITEM_3 = 2;	
	/** Weather infomation */
	private WeatherInfo m_WeatherInfo;	
	/** Weather setting */
	private WeatherPreferences m_Preferneces;	
	/** Model data */
	private WeatherDataModel m_DataModel;
	/** Icon */
	//private ImageView m_WeatherIcon;
	/** Handle request */
	Handler m_HandleRequest;
	/** Dialog */
	//ProgressDialog m_ProgressDialog;	
	/** Dialog */
	AlertDialog m_Dialog;		
	/** Runable */
	Runnable m_Runnable;	
	/** For adapter of dialog */
	private ContextMenuAdapter m_contextAdapter;	
	/** Dialog */
	AlertDialog m_Alert;
		/** Temperature */
	private String s_Temperature;		
	private String s_Humimidy;	
	private String s_Visibility;
	private int weatherCode;
	private static String weatherCondition = null;
	private List<String> lines = new ArrayList<String>();
	//private double stocks[]; //array for the stocks
	private double stockPriceChange = 0;
	private String stocksCSVString;	
    private String[] stockarray;
    private double[] stocks2;
    private BufferedReader bufferReader;
    
   
    
	///********** Timers
  //  private MediaScanTimer mediascanTimer; 	
	private boolean noSleep = false;	
	private int countdownCounter;
	private static final int countdownDuration = 30;
	private Display display;
//	private ImageAdapter imageAdapter;
	private Cursor cursor;
	private int size;  //the number of pictures
	private ProgressDialog pDialog = null;
	private int columnIndex; 

	private boolean debug_;
	private static int appAlreadyStarted = 0;
	private int FPSOverride_ = 0;
	private static int fps = 0;
	private static int x = 0;
	private static int u = 0;
	private static String selectedFileName;
	private static int selectedFileTotalFrames;
	private static int selectedFileDelay;
	private static int Playing = 0;
	private static int selectedFileResolution;
	private static int currentResolution;
	private static String pixelFirmware = "Not Found";
	private static String pixelBootloader = "Not Found";
	private static String pixelHardwareID = "Not Found";
	private static String IOIOLibVersion = "Not Found";
	private static VersionType v;
	private ConnectTimer connectTimer; 
	private static DecodedTimer decodedtimer; 
	private Button changeLocationButton_;
	private Button refreshWeatherButton_;
	
//	private String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
 //   private String basepath = extStorageDirectory;
 //   private static String decodedDirPath =  Environment.getExternalStorageDirectory() + "/pixel/pixelanimations/decoded"; 
  //  private String artpath = "/media";
	 private static String decodedDirPath;
   
	@SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
    
	  //quick hack, instead need to implement asynch task on the yahoo api calls
	 int SDK_INT = android.os.Build.VERSION.SDK_INT;

	 if (SDK_INT>8){

		 StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		 StrictMode.setThreadPolicy(policy); 
	 }
	 //*************************************************************************
  
    
	super.onCreate(savedInstanceState);
	 pixelWeather.context = getApplicationContext();
		
	requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
	setContentView(R.layout.main);
	display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	      
	    //  decodedDirPath = "android.resource://" + getPackageName() +  "/" +R.raw.input;
	    //  decodedDirPath = "android.resource://" + getPackageName();
	     
        
        this.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        try
        {
            app_ver = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        }
        catch (NameNotFoundException e)
        {
           // Log.v(tag, e.getMessage());
        }
        
        //******** preferences code
        resources = this.getResources();
        setPreferences();
        //***************************
        
        forecastTextView_ = (TextView)findViewById(R.id.forecastTextView);
        currentLocationTextView_ = (TextView)findViewById(R.id.currentLocationTextView);
        
        currentTempTextView_ = (TextView)findViewById(R.id.currentTempTextView);
        currentDescriptionTextView_ = (TextView)findViewById(R.id.currentDescriptionTextView);
       // currentHighTextView_ = (TextView)findViewById(R.id.currentHighTextView);
        currentLowTextView_ = (TextView)findViewById(R.id.currentLowTextView);
        lastUpdatedTextView_ = (TextView)findViewById(R.id.lastUpdatedTextView);
        
     
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/freepixel.ttf");
      //  forecastTextView_.setTypeface(tf);
      //  currentLocationTextView_.setTypeface(tf);
        
        currentLocationTextView_.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50);
        forecastTextView_.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        
        currentTempTextView_.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        currentDescriptionTextView_.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
       // currentHighTextView_.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        currentLowTextView_.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        lastUpdatedTextView_.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        
        
        
        changeLocationButton_ = (Button)findViewById(R.id.changeLocationButton);
        refreshWeatherButton_ = (Button)findViewById(R.id.refreshWeatherButton);
        
        
        if (noSleep == true) {        	      	
        	this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //disables sleep mode
        }	
        
        connectTimer = new ConnectTimer(30000,5000); //pop up a message if it's not connected by this timer
 		connectTimer.start(); //this timer will pop up a message box if the device is not found
 		
 		context = getApplicationContext();

        //enableUi(false);
		 
		  //******* Weather Stuff ***************
	        boolean bResult = initializeData();
	   
	        
	        if (bResult == false){
	        	Log.e(LOG_TAG,"Init data failed");
	        	/* Add notify here and quit app */
	        	finish();
	       	return;
	        }	
			        
	        ///********** weather updates functions*********
	    	//initializeHandleRequest();
			requestUpdateWeather();
			//final WeatherCheck weathercheck = new WeatherCheck(this);
			//weathercheck.execute();
			//stockUpdate();
			
			
			
			changeLocationButton_.setOnClickListener(new View.OnClickListener() {  //if the user wants to change the location
			    @Override
			    public void onClick(View v) {
			    	selectSetting();
			    }
			});
			
			refreshWeatherButton_.setOnClickListener(new View.OnClickListener() {  //if the user wants to change the location
			    @Override
			    public void onClick(View v) {
			    	//initializeHandleRequest();
			    	requestUpdateWeather(); //get the latest weather codes
			    	//WeatherCheck weathercheck = new WeatherCheck(this);
					//weathercheck.execute();
			    	
			    }
			});
			
			
			
			
    } //end main function
    
    public static Context getAppContext() {
        return pixelWeather.context;
    }

	
    private void showNotFound() {	
		AlertDialog.Builder alert=new AlertDialog.Builder(this);
		alert.setTitle(getResources().getString(R.string.notFoundString)).setIcon(R.drawable.icon).setMessage(getResources().getString(R.string.bluetoothPairingString)).setNeutralButton(getResources().getString(R.string.OKText), null).show();	
    }
	
	 class IOIOThread extends BaseIOIOLooper {
	  		//private ioio.lib.api.RgbLedMatrix matrix_;
	    	//public AnalogInput prox_;  //just for testing , REMOVE later

	  		@Override
	  		protected void setup() throws ConnectionLostException {
	  			matrix_ = ioio_.openRgbLedMatrix(KIND);
	  			deviceFound = 1; //if we went here, then we are connected over bluetooth or USB
	  			connectTimer.cancel(); //we can stop this since it was found
	  	
	  			//**** let's get IOIO version info for the About Screen ****
	  			pixelFirmware = ioio_.getImplVersion(v.APP_FIRMWARE_VER);
	  			pixelBootloader = ioio_.getImplVersion(v.BOOTLOADER_VER);
	  			pixelHardwareID = ioio_.getImplVersion(v.HARDWARE_VER);
	  			IOIOLibVersion = ioio_.getImplVersion(v.IOIOLIB_VER);
	  			//**********************************************************
	  			
	  			if (debug_ == true) {  			
		  			showToast("Found PIXEL");
	  			}
	  		//	showToast("found pixel");
	  			
	  			//if (fps != 0) {  //then we're doing the FPS override which the user selected from settings
	  			//	matrixdrawtimer.start(); 
	  			//}
	  			//else {
	  			matrix_.frame(frame_); //write select pic to the frame since we didn't start the timer
	  			//}
	  			
	  		//	appAlreadyStarted = 1;
	  		//	try {
					//animateWeather();
			//	} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				//}
	  			
	  			//decodedtimer.start();
	  			
	  			
	  		}

	  	//	@Override
	  		//public void loop() throws ConnectionLostException {
	  		
	  			//matrix_.frame(frame_); //doesn't work as well on older hardware if we keep in this loop, bad performance especially on animations
	  			//try {
				//	pixelFirmware = ioio_.getImplVersion(v);
				//	showToast(pixelFirmware);
				//} catch (ConnectionLostException e) {
					// TODO Auto-generated catch block
				//	e.printStackTrace();
				//}
	  					
	  		//}	
	  		
	  		
	  		
	  		@Override
			public void disconnected() {   			
	  			Log.i(LOG_TAG, "IOIO disconnected");
				if (debug_ == true) {  			
		  			showToast("Bluetooth Disconnected");
	  			}			
			}

			@Override
			public void incompatible() {  //if the wrong firmware is there
				//AlertDialog.Builder alert=new AlertDialog.Builder(context); //causing a crash
				//alert.setTitle(getResources().getString(R.string.notFoundString)).setIcon(R.drawable.icon).setMessage(getResources().getString(R.string.bluetoothPairingString)).setNeutralButton(getResources().getString(R.string.OKText), null).show();	
				showToast("Incompatbile firmware!");
				showToast("This app won't work until you flash the IOIO with the correct firmware!");
				showToast("You can use the IOIO Manager Android app to flash the correct firmware");
				Log.e(LOG_TAG, "Incompatbile firmware!");
			}
	  		
	  		}

	  	@Override
	  	protected IOIOLooper createIOIOLooper() {
	  		return new IOIOThread();
	  	}

	private void enableUi(final boolean enable) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				//seekBar_.setEnabled(enable);
				//toggleButton_.setEnabled(enable);
			}
		});
	}
	
	
	
	private void screenOn() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();  //turn the screen back on
				lp.screenBrightness = brightness / 100.0f;  
				//lp.screenBrightness = 100 / 100.0f;  
				getWindow().setAttributes(lp);
			}
		});
	}
	
	private void screenOff() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				WindowManager.LayoutParams lp = getWindow().getAttributes();  //turn the screen back on
				lp.screenBrightness = 1 / 100.0f;  
				getWindow().setAttributes(lp);
			}
		});
	}
	
	
	
	//private void hideTroubleshootingScreen() {
		//runOnUiThread(new Runnable() {
			///@Override
			//public void run() {				
				//findViewById(R.id.notConnectedLabel).setVisibility(View.GONE); //we found the board so hide this message
			//}
		//});
	//}
  
    
    private void showToast(final String msg) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast toast = Toast.makeText(pixelWeather.this, msg, Toast.LENGTH_LONG);
                toast.show();
			}
		});
	}   
    
	
    
   
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
       
    	 super.onCreateOptionsMenu(menu);
    	
    	//vid.pause();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
       
        	
      if (item.getItemId() == R.id.menu_instructions) {
	    	AlertDialog.Builder alert=new AlertDialog.Builder(this);
	      	//alert.setTitle(setupInstructionsStringTitle).setIcon(R.drawable.icon).setMessage(setupInstructionsString).setNeutralButton(OKText, null).show();
	      	alert.setTitle("Instructions").setIcon(R.drawable.icon).setMessage(getString(R.string.setupInstructionsString)).setNeutralButton(OKText, null).show();
	   }
	  
	  //if (item.getItemId() == R.id.menu_about) {
		  
		//  AlertDialog.Builder alert=new AlertDialog.Builder(this);
	      //alert.setTitle(getString(R.string.menu_about_title)).setIcon(R.drawable.icon).setMessage(getString(R.string.menu_about_summary) + "\n\n" + getString(R.string.versionString) + " " + app_ver).setNeutralButton(getResources().getString(R.string.OKText), null).show();	
	   //}
	  
	  if (item.getItemId() == R.id.menu_about) {
		  
		    AlertDialog.Builder alert=new AlertDialog.Builder(this);
	      	alert.setTitle(getString(R.string.menu_about_title)).setIcon(R.drawable.icon).setMessage(getString(R.string.menu_about_summary) + "\n\n" + getString(R.string.versionString) + " " + app_ver + "\n"
	      			+ getString(R.string.FirmwareVersionString) + " " + pixelFirmware + "\n"
	      			+ getString(R.string.HardwareVersionString) + " " + pixelHardwareID + "\n"
	      			+ getString(R.string.BootloaderVersionString) + " " + pixelBootloader + "\n"
	      			+ getString(R.string.LibraryVersionString) + " " + IOIOLibVersion).setNeutralButton(getResources().getString(R.string.OKText), null).show();	
	   }
	  
	  
	  
    	
    	if (item.getItemId() == R.id.menu_prefs)
       {
    		
    		Intent intent = new Intent()
           		.setClass(this,
           				com.ledpixelart.weather.preferences.class);
       
           this.startActivityForResult(intent, 0);
       }
    	
    
   	 if (item.getItemId() == R.id.menu_setting) {
  		  
   		selectSetting();
   		 // selectWeatherSetting();	//this is an extra box for setting celsius or farenheight, not needed at the moment
	   }
   	 
 //  	if (item.getItemId() == R.id.menu_stock) {
   //		Intent stock_intent = new Intent(getApplicationContext(), com.exoplatform.weather.controller.ActivityStockList.class);
   		//intent.putExtra("weatherCityKey", weatherCity);
   		//startActivityForResult(intent, REG_CHANGELOCATION);  
   	//	startActivityForResult(stock_intent, REG_CHANGESTOCKS);  
   		//startActivity();  
//	   }
   	 
   
  
    	
   // 	if (item.getItemId() == R.id.menu_socialmediaaccounts) //social media accounts screen
    //    {
     		
     //		Intent intent = new Intent()
       //     		.setClass(this,
         //   				com.diymagicmirror.android.SocialMediaAccounts.class);
        
          //  this.startActivityForResult(intent, 0);
       // }    	
              
       return true;
    }
    
  //now let's get data back from the preferences activity below
    @Override
    public void onActivityResult(int reqCode, int resCode, Intent data) //we'll go into a reset after this
    {
    	
    	super.onActivityResult(reqCode, resCode, data);
    	
    	switch (reqCode){
    	case REG_CHANGELOCATION:
    		updateDataOfCurrentLocation();
    		//showToast("went here after onactivity weather");
    		break;
    	//case REG_CHANGESTOCKS: //this means the stocks were changed so we need to update the variables
    		//SharedPreferences prefs = getSharedPreferences("stocklist", MODE_PRIVATE ); 
        	//stocksCSVString = prefs.getString("stocks","");
        	//showToast("stock string: " + stocksCSVString);
        //	break;
    		default:
    			//Log.w(LOG_TAG,"Not handle request code:"+ reqCode);
    		break;
    	}
    	
    	// if (debug == true) {
    	//	 Toast.makeText(getBaseContext(), "On Activity Result Code: " + reqCode, Toast.LENGTH_LONG).show();
        // }      	
    	
    	setPreferences(); //very important to have this here, after the menu comes back this is called, we'll want to apply the new prefs without having to re-start the app
    	//update stocks
    	
    //	vid.start();
    	
    }
    
   private void setPreferences() //here is where we read the shared preferences into variables
    {
	   
		     SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);     
		    
		     //scanAllPics = prefs.getBoolean("pref_scanAll", false);
		     //slideShowMode = prefs.getBoolean("pref_slideshowMode", false);
		     noSleep = prefs.getBoolean("pref_noSleep", false);
		     debug_ = prefs.getBoolean("pref_debugMode", false);
		  //   dimDuringSlideShow = prefs.getBoolean("pref_dimDuringSlideShow", true);
		     
		    // imageDisplayDuration = Integer.valueOf(prefs.getString(   
		  	      //  resources.getString(R.string.pref_imageDisplayDuration),
		  	      //  resources.getString(R.string.imageDisplayDurationDefault)));   
		     
		    // pauseBetweenImagesDuration = Integer.valueOf(prefs.getString(   
		  	        //resources.getString(R.string.pref_pauseBetweenImagesDuration),
		  	        //resources.getString(R.string.pauseBetweenImagesDurationDefault)));  
		     
		     matrix_model = Integer.valueOf(prefs.getString(   //the selected RGB LED Matrix Type
		    	        resources.getString(R.string.selected_matrix),
		    	        resources.getString(R.string.matrix_default_value))); 
		     
		     if (matrix_model == 0 || matrix_model == 1) {
		    	 currentResolution = 16;
		     }
		     else
		     {
		    	 currentResolution = 32;
		     }
		     
		     FPSOverride_ = Integer.valueOf(prefs.getString(   //the selected RGB LED Matrix Type
		    	        resources.getString(R.string.fps_override),
		    	        resources.getString(R.string.FPSOverrideDefault))); 
		     
		     switch (FPSOverride_) {  //get this from the preferences
		     case 0:
		    	 fps = 0;
		    	 break;
		     case 1:
		    	 fps = 5;
		    	 break;
		     case 2:
		    	 fps = 10;
		    	 break;
		     case 3:
		    	 fps = 15;
		    	 break;
		     case 4:
		    	 fps = 24;
		    	 break;
		     case 5:
		    	 fps = 30;
		    	 break;
		     default:	    		 
		    	 fps = 0;
		     }
		     
		  //   switch (matrix_model) {  //get this from the preferences
		    // case 0:
		    	// KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x16;
		    	 //BitmapInputStream = getResources().openRawResource(R.raw.selectimage16);
		    	 //break;
		     //case 1:
		    	// KIND = ioio.lib.api.RgbLedMatrix.Matrix.ADAFRUIT_32x16;
		    	// BitmapInputStream = getResources().openRawResource(R.raw.selectimage16);
		    	// break;
		    // case 2:
		    	// KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32_NEW; //v1
		    	// BitmapInputStream = getResources().openRawResource(R.raw.selectimage32);
		    	// break;
		   //  case 3:
		    //	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32; //v2
		    //	 BitmapInputStream = getResources().openRawResource(R.raw.selectimage32);
		    //	 break;
		   //  default:	    		 
		    //	 KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32; //v2 is the default
		    //	 BitmapInputStream = getResources().openRawResource(R.raw.selectimage32);
		  //   }
		         
		     KIND = ioio.lib.api.RgbLedMatrix.Matrix.SEEEDSTUDIO_32x32; //v2 is the default
		     BitmapInputStream = getResources().openRawResource(R.raw.loading32);
		     frame_ = new short [KIND.width * KIND.height];
			 BitmapBytes = new byte[KIND.width * KIND.height *2]; //512 * 2 = 1024 or 1024 * 2 = 2048
			 
			 loadRGB565(); //this function loads a raw RGB565 image to the matrix
    
 }
   
   private void setWeatherText(final String str) {
	   runOnUiThread(new Runnable() {
		   public void run() {
			   forecastTextView_.setText(str);
		   }
	   });
   }
   
   private void setLocationText(final String str) {
	   runOnUiThread(new Runnable() {
		   public void run() {
			   currentLocationTextView_.setText(str);
		   }
	   });
   }
   
   private static void loadRGB565() {
 	   
		try {
  			int n = BitmapInputStream.read(BitmapBytes, 0, BitmapBytes.length); // reads
  																				// the
  																				// input
  																				// stream
  																				// into
  																				// a
  																				// byte
  																				// array
  			Arrays.fill(BitmapBytes, n, BitmapBytes.length, (byte) 0);
  		} catch (IOException e) {
  			e.printStackTrace();
  		}

  		int y = 0;
  		for (int i = 0; i < frame_.length; i++) {
  			frame_[i] = (short) (((short) BitmapBytes[y] & 0xFF) | (((short) BitmapBytes[y + 1] & 0xFF) << 8));
  			y = y + 2;
  		}
  }
   
///****** weather functions here *****
   
	private AlertDialog createContextMenuSetting(Context context){
		/* Crate menu list */
		AlertDialog dialogMenu = null;
		List<ContextMenuItem> arrMenuItem = null;
		AlertDialog.Builder contextMenu = new AlertDialog.Builder(context);

		/* Create menu item of context menu */
		arrMenuItem = _createContextMenuList();
		if (arrMenuItem == null){
			Log.e(LOG_TAG,"Can note create dialog item");
			return null;
		}

		this.m_contextAdapter = new ContextMenuAdapter(context,
				0, arrMenuItem);
		contextMenu.setAdapter(m_contextAdapter, new HandleSelectContextMenu());
		contextMenu.setInverseBackgroundForced(true);
		contextMenu.setTitle(R.string.title_context_menu_setting);
		contextMenu.setIcon(R.drawable.ic_context_menu);
		
		dialogMenu = contextMenu.create();
		dialogMenu.setCanceledOnTouchOutside(true);
		
		return dialogMenu;
	}
	
	private List<ContextMenuItem> _createContextMenuList(){
		ArrayList<ContextMenuItem> arrMenuItem = new ArrayList<ContextMenuItem>();

		/* Create first menu item base on menu state */
		ContextMenuItem itemContext1 = new ContextMenuItem(
				MENU_CONTEXT_0,
				R.string.context_menu_changeLocation,
				R.drawable.location_ic);

	//	ContextMenuItem itemContext2 = new ContextMenuItem(
		//		MENU_CONTEXT_1,
		//		R.string.context_menu_update_time,
		//		R.drawable.update_time);
		
		ContextMenuItem itemContext2 = new ContextMenuItem(
				MENU_CONTEXT_1,
				R.string.temperature_unit,
				R.drawable.temperature_ic);		
		
		/* Add context item to list */
		arrMenuItem.add(itemContext1);
		//arrMenuItem.add(itemContext2);
		arrMenuItem.add(itemContext2);
		return arrMenuItem;
	}		
	
	/**************************************************************************
	 * Handle select context menu
	 * @author DatNQ
	 *
	 **************************************************************************/
	private class HandleSelectContextMenu implements 
					android.content.DialogInterface.OnClickListener{
		
		/*********************************************************
		 * Handle when select context menu item
		 * @see android.content.DialogInterface.OnClickListener#onClick(android.content.DialogInterface, int)
		 * @author DatNQ
		 ********************************************************/
		@Override
		public void onClick(DialogInterface dialog, int which) {
			
			switch (which){
			case MENU_CONTEXT_0:
				selectSetting();
				//selectSetting();
				
				break;

		//	case MENU_CONTEXT_1:
				//selectSetting();
				//selectTempFormat();
				//selectTimeIntervalUpdating();
			//	break;
				
			case MENU_CONTEXT_1:
				selectTempFormat();
				
				//selectTempFormat();
				break;
				
				default:
					Log.e(LOG_TAG,"Invalid context menu");
					break;
			}
		}
	}	
	
	 /***************************************************************************
     * Select temperature format
     * @date May 12, 2011
     * @time 11:21:27 PM
     * @author DatNQ
     **************************************************************************/
    private void selectTempFormat(){
    	final CharSequence[] items = {"Celsius", "Fahrenheit"};

    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	builder.setTitle(R.string.selectTemperatureUnit);
    	builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
    	    public void onClick(DialogInterface dialog, int item) {
    	        /* Check to update setting */
    	    	boolean bIsC = true;
    	    	switch( item ){
    	    	case SELECT_ITEM_1:
    	    		bIsC = true;
    	    		break;
    	    	case SELECT_ITEM_2:
    	    		bIsC = false;
    	    		
    	    		default:
    	    			break;
    	    	}
    	    	bIsC = false;
    	    	m_Preferneces.setTempFmt(bIsC);
    	    	m_Alert.dismiss();
    	    	//notifyUpdateTime();
    	    	updateWeatherInfo(m_WeatherInfo);
    	    }
    	});
    	m_Alert = builder.create();  
    	m_Alert.show();
    }    
	
	/***************************************************************************
	 * Select setting
	 * @date May 12, 2011
	 * @time 11:26:52 PM
	 * @author DatNQ
	 **************************************************************************/
	private void selectWeatherSetting(){
		m_Dialog = createContextMenuSetting(this);
		if (m_Dialog != null){
			m_Dialog.show();
		}		
	}
   
  
   
   /***************************************************************************
    * Initialize data
    * @return true if success, false if failed
    * @date May 7, 2011
    * @time 6:48:46 AM
    * @author DatNQ
    **************************************************************************/
   private boolean initializeData(){
   	/* Get application context */
   	Context appContext = this.getApplicationContext();
   	
   	/* Get preference instance */
   	m_Preferneces = WeatherPreferences.getInstance(appContext);
   	if (m_Preferneces == null){
   		Log.e(LOG_TAG, "Get preference instance failed, check please");
   		return false;
   	}
   	
   	/* Get instance of data model */
   	m_DataModel = WeatherDataModel.getInstance();
   	if (m_DataModel == null){
   		Log.e(LOG_TAG,"Can not get data model");
   		return false;
   	}

   	initializeHandleRequest();
   	
   	return true;
   }    
   
   /***************************************************************************
    * Draw weather screen, about if need
    * @date May 7, 2011
    * @time 5:38:13 PM
    * @author DatNQ
    **************************************************************************/
     
   /***************************************************************************
    * Change location
    * @date May 9, 2011
    * @time 9:57:52 PM
    * @author DatNQ
    ***************************************************************************/
   private void updateDataOfCurrentLocation(){
		//WeatherCheck weathercheck = new WeatherCheck(this);
		//weathercheck.execute();
     	requestUpdateWeather();
   }
   
   /***************************************************************************
    * Update weather information
    * @param weatherInfo
    * @date May 9, 2011
    * @time 3:38:08 AM
    * @author DatNQ
    **************************************************************************/
   private void updateWeatherInfo(WeatherInfo weatherInfo){
   	if (weatherInfo == null){  //we'll go here if no internet connection
   		Log.e(LOG_TAG,"Weather is null");
   		//resetVideos(); //make sure and set this back or nothing else will play
   		showToast("Could not retrieve weather, no Internet connection");
   		return;
   	}
   	
   	String strCode = weatherInfo.getCode();
   	int nCode = getImageByCode(strCode);
   	//m_WeatherIcon.setImageResource(nCode);   	
   	
   	String strForecastHigh = weatherInfo.getForecastHigh();   
	String strHumidity = weatherInfo.getHumidity();   	
   	String strForecastCode = weatherInfo.getForecastCode();   	
   	String strForecastHigh2 = weatherInfo.getForecastHigh2();   	
   	String strForecastCode2 = weatherInfo.getForecastCode2();   	
   	String strForecastText = weatherInfo.getForecastText();   	
   	String strForecastText2 = weatherInfo.getForecastText2();   	
   	String strCity = weatherInfo.getCity();
   	String strCountry = weatherInfo.getCountry();
   	String strDate = weatherInfo.getDate(); 
   	boolean bIsC = m_Preferneces.getTempFmt();
   	
   	if (debug_ == true) {  			
			showToast("Weather Code: " + strForecastCode);
    }
   	
	String strFmt;
   	String strTemp = weatherInfo.getTemperature(WeatherInfo.TEMPERATURE_FMT_CELSIUS);
   	if (bIsC == false){
   		strFmt = getString(R.string.str_temperature_fmt); 
   	} else {
   		strFmt = getString(R.string.str_temperature_fmt_f);
   		strTemp = WeatherDataModel.convertC2F(strTemp);
   	}
   	
   //	Time now = new Time();
   //	now.setToNow();
   	
   	Time today = new Time(Time.getCurrentTimezone());
   	today.setToNow();
   	
   	String strTemperature = String.format(strFmt, strTemp);
   	
   	setWeatherText("Current Weather: " + strForecastText + "\n\n" + "Tomorrow's Weather: " + strForecastText2);
	setLocationText(strCity);
	
	currentTempTextView_.setText(strTemperature);
    currentDescriptionTextView_.setText(strForecastText);
 //   currentHighTextView_.setText("High: " + strForecastHigh);
    currentLowTextView_.setText("Humidity: " + strHumidity +"%");
    lastUpdatedTextView_.setText("Last Update: " + today.format("%k:%M"));
    
   	weatherCode = Integer.valueOf(strForecastCode.toString());   	
   	getPIXELWeather();
   	//showToast("Weather Condition: " + weatherCondition); //rain, cold, or sunny
   	
   
   	
   	//12797534 santa clara woeid
   	
   	//m_TextLocation.setText(weatherInfo.getCity());
   	//m_Temperature.setText(strTemperature);
   	//m_Date.setText(weatherInfo.getDate());
   	
   	//strFmt = getString(R.string.str_humidity_fmt);
   	//String strHumidity = String.format(strFmt, weatherInfo.getHumidity());
   //	s_Humimidy = strHumidity;
   	//m_Humimidy.setText(strHumidity);
   	//strFmt = getString(R.string.str_visi_fmt);
   	//String strVisi = String.format(strFmt, weatherInfo.getVisibility());
   	//m_Visibility.setText(strVisi);
   	
  // 	 showToast("Code: " + strCode);
  //   showToast("Temp: " + strTemp);
  // 	 showToast("Temperature: " + strDate);
   	 
   	// showToast("Forecast High: " + strForecastHigh);
  // 	 showToast("Forecast Code: " + strForecastCode);
  // 	 showToast("Forecast Text: " + strForecastText);
   	 
   //	showToast("Forecast High2: " + strForecastHigh2);
   //	showToast("Forecast Code2: " + strForecastCode2);
   //	showToast("Forecast Text2: " + strForecastText2);
   	
   //	showToast("City: " + strCity);
  // 	showToast("Country: " + strCountry);  
   	
   //	String both = name + "-" + dest;
    weatherCity = strCity + ", " + strCountry;
  //  showToast(weatherCity);  
   	
   	// String strForecastHigh = weatherInfo.getForecastHigh();
   }
   
   private int getImageByCode(String strCode){
   	int nImageCode = R.drawable.a0;
   	
   	if (strCode == null){
   		Log.e(LOG_TAG,"Code is null");
   		return nImageCode;
   	}
   	
   	int nCode = Integer.parseInt(strCode);
   	
   	int nNumber= YahooWeatherHelper.m_ImageArr.length;
   	for (int i=0; i < nNumber; i++){
   		if (nCode == YahooWeatherHelper.m_ImageArr[i][1]){
   			return YahooWeatherHelper.m_ImageArr[i][0];
   		}
   	}
   	return nImageCode;
   }
 
   /***************************************************************************
	 * Handler request
	 * @date May 10, 2011
	 * @time 8:50:24 PM
	 * @author DatNQ
	 **************************************************************************/
   private void initializeHandleRequest(){
		m_Runnable = new Runnable(){

			@Override
			public void run() {
				requestUpdateWeather();
			}			
		};
		
		
	    /* Setting up handler for ProgressBar */
		m_HandleRequest = new Handler(){
			@Override
			public void handleMessage(Message message) {
				int nRequest = message.what;
				
				switch(nRequest){
				case REG_GET_WEATHER_START:
			    	String strWOEID = m_Preferneces.getLocation();
			    	if (strWOEID == null){
			    		Log.e(LOG_TAG,"Can not get WOEID");
			    		 showToast("Could not retrive weather, please check your spelling or ensure you've got Internet");
			    		// resetVideos();
			    		//m_ProgressDialog.dismiss();
			    		displayNotifyCation(R.string.strFetchFailed);
			    		return;
			    	} else {
				    	/* Get weather information */
				        m_WeatherInfo = m_DataModel.getWeatherData(strWOEID);			
			    	}
					
					Message msgRegSearch = new Message();
					msgRegSearch.what = REG_GET_WEATHER_FINISH;
					sendMessage(msgRegSearch);
					break;
					
				case REG_GET_WEATHER_FINISH:
			    	if (m_WeatherInfo != null){
			    		updateWeatherInfo(m_WeatherInfo);
			    		
						//if (Playing == 0) { //don't play twice
			    		
				    		//try {
									try {
										animateWeather();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
							//	} catch (IOException e) {
									// TODO Auto-generated catch block
								//	e.printStackTrace();
							//	}
					//	}
			    		weatherObtained = 1;
			    		
			    		//notifyUpdateTime();
			    	}
			    	else {
			    		 showToast("Could not retrive weather, please check Internet connection");
			    		// resetVideos();
			    	}
					//m_ProgressDialog.dismiss();
					//m_HandleRequest.postDelayed(m_Runnable, (ONE_MINUTE*m_Preferneces.getTimeUpdate()));
					break;
					 
					 default:
						 Log.e(LOG_TAG,"Can not handle this message");
						 showToast("Could not retrive weather, please check Internet connection");
			    		// resetVideos();
					break;
				}
			}
       };		
	}  
   
      
   
   /***************************************************************************
    * Select setting
    * @date May 7, 2011
    * @time 8:57:55 PM
    * @author DatNQ
    **************************************************************************/
   	
   public class WeatherCheck extends AsyncTask<Void, Void, String> {


       public WeatherCheck(Context context) {

       }

       @Override
       protected void onPreExecute() {

       }

       @Override
       protected String doInBackground(Void... params) {
    	   Message msgFetchData = new Message();
	    	msgFetchData.what = REG_GET_WEATHER_START;
	    	m_HandleRequest.sendMessage(msgFetchData);
           return null;
       }      

       @Override
       protected void onPostExecute(String result) {                           

       }
 }
  
   
   
  private void requestUpdateWeather(){
	    	Message msgFetchData = new Message();
	    	msgFetchData.what = REG_GET_WEATHER_START;
	    	m_HandleRequest.sendMessage(msgFetchData);    	
	 }
	 
	 private void displayNotifyCation(int nResID){
			Toast.makeText(getApplicationContext(), getString(nResID),
					Toast.LENGTH_LONG).show();    	
	 }
		
	 /***************************************************************************
	     * Select setting
	     * @date May 7, 2011
	     * @time 8:57:55 PM
	     * @author DatNQ
	     **************************************************************************/
    private void selectSetting(){
		//Intent intent = new Intent(magicmirror.this,com.exoplatform.weather.controller.ActivityScreenLocation.class);
    	
    	Intent intent = new Intent(getApplicationContext(), com.exoplatform.weather.controller.ActivityScreenLocation.class);
		intent.putExtra("weatherCityKey", weatherCity);
		startActivityForResult(intent, REG_CHANGELOCATION);  
		//startActivity(intent);
		 
	//	Intent i= new Intent(getApplicationContext(), NewActivity.class);
//		i.putExtra("new_variable_name","value");
	//	startActivity(i);
    }
    
    private void getPIXELWeather() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				switch (weatherCode)  //sets the video playlists based on the mode
				{
					case 0: //tornado							
						weatherCondition = "rain";				
						break;
					case 1: //tropical storm					
						weatherCondition = "rain";	
						break;
					case 2: //hurricane					
						weatherCondition = "rain";	
						break;
					case 3: //severe thunderstorms					
						weatherCondition = "rain";	
						break;
					case 4: //thunderstorms					
						weatherCondition = "rain";	
						break;
					case 5: //mixed rain and snow
						weatherCondition = "rain";	
						break;
					case 6: //mised rain and sleet
						weatherCondition = "rain";	
						break;
					case 7: //mised snow and sleet
						weatherCondition = "snow";	
						break;
					case 8: //freezing drizzle
						weatherCondition = "rain";	
						break;
					case 9: //drizzle
						weatherCondition = "rain";	
						break;
					case 10: //freezing rain
						weatherCondition = "rain";	
						break;
					case 11: //showers
						weatherCondition = "rain";	
						break;	
					case 12: //showers
						weatherCondition = "rain";	
						break;	
					case 13: //snow flurries
						weatherCondition = "snow";	
						break;	
					case 14: //light snow showers
						weatherCondition = "snow";	
						break;	
					case 15: //blowing snow
						weatherCondition = "snow";	
						break;
					case 16: //snow
						weatherCondition = "snow";	
						break;
					case 17: //hail
						weatherCondition = "snow";	
						break;
					case 18: //sleet
						weatherCondition = "snow";	
						break;
					case 19: //dust
						weatherCondition = "rain";	
						break;
					case 20: //foggy
						weatherCondition = "rain";	
						break;
					case 21: //haze
						weatherCondition = "rain";	
						break;
					case 22: //smoky
						weatherCondition = "rain";	
						break;
					case 23: //blustery
						weatherCondition = "rain";	
						break;
					case 24: //windy
						weatherCondition = "cold";	
						break;
					case 25: //cold
						weatherCondition = "cold";	
						break;
					case 26: //cloudy
						weatherCondition = "cold";	
						break;
					case 27: //mostly cloudy (day)
						weatherCondition = "cold";	
						break;
					case 28: //mostly cloudy (night)
						weatherCondition = "cold";	
						break;
					case 29: //partly cloudy (night)
						weatherCondition = "sunny";	
						break;
					case 30: //partly cloudy (day)
						weatherCondition = "sunny";	
						break;
					case 31: //clear (night)
						weatherCondition = "sunny";	
						break;
					case 32: //sunny
						weatherCondition = "sunny";	
						break;
					case 33: //fair (night)
						weatherCondition = "sunny";	
						break;
					case 34: //fair (day)
						weatherCondition = "sunny";	
						break;
					case 35: //mixed rain and hail
						weatherCondition = "rain";	
						break;
					case 36: //hot
						weatherCondition = "sunny";	
						break;
					case 37: //isoldated thunderstorms
						weatherCondition = "rain";	
						break;
					case 38: //scattered thunderstorms
						weatherCondition = "rain";	
						break;
					case 39: //scattered thunderstorms
						weatherCondition = "rain";	
						break;
					case 40: //scattered showers
						weatherCondition = "rain";	
						break;
					case 41: //heavy snow
						weatherCondition = "snow";	
						break;	
					case 42: //scattered snow showers
						weatherCondition = "snow";	
						break;	
					case 43: //heavy snow
						weatherCondition = "snow";	
						break;	
					case 44: //partly cloudy
						weatherCondition = "cold";	
						break;
					case 45: //thundershowers
						weatherCondition = "rain";	
						break;
					case 46: //snow showers
						weatherCondition = "rain";	
						break;
					case 47: //isoldated thundershowers
						weatherCondition = "rain";	
						break;	
					case 3200: //not available
						weatherCondition = "cold";	
						break;						
					default:
						showToast("Could not obtain weather, please check Internet connection");
				}
			}
		});
    }
    
    public class ConnectTimer extends CountDownTimer
   	{

   		public ConnectTimer(long startTime, long interval)
   			{
   				super(startTime, interval);
   			}

   		@Override
   		public void onFinish()
   			{
   				if (deviceFound == 0) {
   					showNotFound(); 					
   				}
   				
   			}

   		@Override
   		public void onTick(long millisUntilFinished)				{
   			//not used
   		}
   	}
    
    public void animateWeather() throws IOException  {
    	 InputStream iS = null;  
  	  //********we need to reset everything because the user could have been already running an animation
  	     x = 0;
  	     
  	     if (Playing == 1) {
  	    	 decodedtimer.cancel();
  	    //	iS.close();
  	     }
  	     ///****************************
  	     String msg = weatherCondition;
  	     
  	  
   	  
       
       //get the file as a stream  
 //  iS = resources.getAssets().open("cold/cold.txt");  
   iS = resources.getAssets().open(weatherCondition + "/" + weatherCondition + ".txt");  
   
   

   //create a buffer that has the same size as the InputStream  
   byte[] buffer = new byte[iS.available()];  
   //read the text file as a stream, into the buffer  
   iS.read(buffer);  
   //create a output stream to write the buffer into  
   ByteArrayOutputStream oS = new ByteArrayOutputStream();  
   //write this buffer to the output stream  
   oS.write(buffer);  
   //Close the Input and Output streams  
   oS.close();  
   iS.close();  
   
   String animationMetaFile = oS.toString();

   //return the output stream as a String  
//    return oS.toString();   
	     
	    
	//Toast toast6 = Toast.makeText(context, "weather condition: " + weatherCondition, Toast.LENGTH_LONG);
    //toast6.show();
    

      StringBuilder text = new StringBuilder();
      String fileAttribs = null;
	   	      
	   	
	  fileAttribs = animationMetaFile;
	   	    
	   	    String fdelim = "[,]"; //now parse this string considering the comma split  ie, 32,60
	        String[] fileAttribs2 = fileAttribs.split(fdelim);
	        selectedFileTotalFrames = Integer.parseInt(fileAttribs2[0].trim());
	    	selectedFileDelay = Integer.parseInt(fileAttribs2[1].trim());
	    	selectedFileResolution = Integer.parseInt(fileAttribs2[2].trim());
	    	
	     	if (debug_ == true) {  	
	     		Toast toast9 = Toast.makeText(context, "Num frames of weather animation: " + String.valueOf(selectedFileTotalFrames), Toast.LENGTH_LONG);
	    	    toast9.show();
	     	}	
	    	
	    		
	    	
	    	//now we need to compare the current resoluiton with the encoded resolution
	    	//if different, then we need to re-encode
	    	
	    //	if (selectedFileResolution == currentResolution) {
	    	
			    	if (fps != 0) {  //then we're doing the FPS override which the user selected from settings
			    		selectedFileDelay = 1000/fps;
					}
			    	
			    	if (selectedFileDelay == 0) {  //had to add this as some animated gifs have 0 delay which was causing a crash
			    		selectedFileDelay = 10;
			    	}
			    	pixelWeather myActivity = new pixelWeather();  //had to add this due to some java requirement	    	
					decodedtimer = myActivity.new DecodedTimer(300000,selectedFileDelay);
					decodedtimer.start();
					Playing = 1; //our isPlaying flag	        	
		  // 		}
  	    //	else {
  	    		//Toast toast6 = Toast.makeText(context, "LED panel model was changed, decoding again...", Toast.LENGTH_LONG);
  		      //  toast6.show();
  		       
  		        ///************** let's show a message on PIXEL letting the user know we're decoding
  		       // showDecoding();
  		        ///*********************************************************************************
  	    	//	gifView.play();
  	    		
  	    	//}
  	//	}	
  		
  		//else { //then we need to decode the gif first	
  			//Toast toast7 = Toast.makeText(context, "One time decode in process, just a moment...", Toast.LENGTH_SHORT);
  	       // toast7.show();
  	       // showDecoding();
  			//gifView.play();
  		//}
    }
    
   
    
	 
    public class DecodedTimer extends CountDownTimer
   	{

   		public DecodedTimer(long startTime, long interval)
   			{
   				super(startTime, interval);
   			}

   		@Override
   		public void onFinish()
   			{
   			decodedtimer.start(); //re-start the timer to keep is going
   				
   			}

   		@Override
   		public void onTick(long millisUntilFinished)	{
   			
   			//now we need to read in the raw file, it's already in RGB565 format and scaled so we don't need to do any scaling
   			
   		//	File file = new File(decodedDirPath + "/" + selectedFileName + "/" + selectedFileName + x + ".rgb565");
   	 	//	File file = new File("file:///android_asset/"+ "/cold/cold" + x + ".rgb565");  
   			
   		   InputStream fileStream = null;  
   	   	  
   	       
   	       //get the file as a stream  
   		   try {
   		//	fileStream = resources.getAssets().open("cold/cold" + x + ".rgb565");
   			fileStream = resources.getAssets().open(weatherCondition + "/" + weatherCondition + x + ".rgb565");
   			//iS = resources.getAssets().open(weatherCondition + "/" + weatherCondition + ".txt");  
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}  
   		   
   		//long length = ((CharSequence) fileStream).length();
   		   
   		
   	//	File jfile = new File("file:///android_asset/"+ "/cold/cold" + x + ".rgb565");   
   		   
   	//	File f = new File(getCacheDir()+"/cold/cold" + x + ".rgb565");
   	  //  if (!f.exists()) try {

	   	//    InputStream is = getAssets().open("cold/cold" + x + ".rgb565");
	   	//    int size = is.available();
	   	//    byte[] buffer = new byte[size];
	   	//    is.read(buffer);
	   //	    is.close();
	
	
	   	 //   FileOutputStream fos = new FileOutputStream(f);
	   	 //   fos.write(buffer);
	   	 //   fos.close();
	   	//  } catch (Exception e) { throw new RuntimeException(e); 
	  // 	}

   	//   mapView.setMapFile(f.getPath());  
   		   
   		
   		   
   			
   		//	File file = new File(decodedDirPath + "/" + weatherCondition + "/" + weatherCondition + x + ".rgb565");
   			
   		//	File file = new File(f.getPath());
   			
   		//	weatherCondition
   		   
   		
   		ByteArrayOutputStream bos = new ByteArrayOutputStream();
   		int next = 0;
		try {
			next = fileStream.read();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
   		while (next > -1) {
   		    bos.write(next);
   		    try {
				next = fileStream.read();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
   		}
   		try {
			bos.flush();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
   		byte[] BitmapBytes = bos.toByteArray(); 
   		   
   		   
   		   
   		   
   		//	FileInputStream raw565 = null;
			//try {
				//raw565 = new FileInputStream(file);
			//} catch (FileNotFoundException e1) {
				//// TODO Auto-generated catch block
				//e1.printStackTrace();
		//	}
   			 
			x++;
			
			if (x == selectedFileTotalFrames - 1) {
   				x = 0;
   			}
			
			  
   			
		
   			
   			int y = 0;
     		for (int i = 0; i < frame_.length; i++) {
     			frame_[i] = (short) (((short) BitmapBytes[y] & 0xFF) | (((short) BitmapBytes[y + 1] & 0xFF) << 8));
     			y = y + 2;
     		}
     		
     		//we're done with the images so let's recycle them to save memory
    	   // canvasBitmap.recycle();
    	 //  bitmap.recycle(); 
   		
	   		//and then load to the LED matrix
     		
		   	try {
				matrix_.frame(frame_);
			} catch (ConnectionLostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
	   			
   		}
   		
   	}
   
   @Override
   public void onDestroy() {
       super.onDestroy();
    //   vid.stopPlayback();      
      // charchangeTimer.cancel();
   }
   
   @Override
   protected void onStop()
{
   // Stop play
   super.onStop();
  // vid.stopPlayback();
   
}
   
   @Override
   protected void onPause() {
       super.onPause();
      // vid.pause();
   }
   
   protected void onResume() { //this runs after the control is returned to this main activie, ie, after the set stock or set weather things are fun
	   super.onResume();
	   SharedPreferences prefs = getSharedPreferences("stocklist", MODE_PRIVATE ); 
	   stocksCSVString = prefs.getString("stocks","");
	   
	  requestUpdateWeather();
		//initializeHandleRequest();
	  // showToast("stock string: " + stocksCSVString);
  
   }
    
}