package test.concept;


import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.content.Context;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.util.Log;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.KeyEvent;

import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.io.DataOutputStream;
import java.io.FileInputStream;

import java.net.HttpURLConnection;
import java.net.URL;

import android.media.MediaRecorder;
import android.media.MediaPlayer;

import java.io.IOException;

public class FullscreenActivity extends Activity 
{
	
  private SurfaceView 		preview			= null;
  private SurfaceHolder 	previewHolder	= null;
  private Camera 			camera			= null;
  private boolean 			inPreview		= false;
  private boolean 			cameraConfigured= false;
  private EditText 			edittext;
  
  private static final String LOG_TAG = "AudioRecordTest";
  private static String mFileName = null;

  private RecordButton mRecordButton = null;
  private MediaRecorder mRecorder = null;

  private PlayButton   mPlayButton = null;
  private MediaPlayer   mPlayer = null;
  
  
  
  /*
   * start or stop recording audio
   */
  private void onRecord ( boolean start ) 
  {
      if (start) {
          startRecording();
      } else {
          stopRecording();
      }
  }

  
  
  /*
   * start or stop playing audio
   */
  private void onPlay ( boolean start ) 
  {
      if ( start ) 
      {
          startPlaying ( );
      }
      else
      {
          stopPlaying ( );
      }
  }

  
  /*
   * start playing audio
   */
  private void startPlaying ( ) 
  {
      mPlayer = new MediaPlayer ( );
      
      try 
      {
          mPlayer.setDataSource ( mFileName );
          mPlayer.prepare ( );
          mPlayer.start ( );
      } 
      catch ( IOException e ) 
      {
          Log.e ( LOG_TAG, "prepare() failed" );
      }
  }

  
  /*
   * stop playing audio
   */
  private void stopPlaying ( ) 
  {
      mPlayer.release ( );
      mPlayer = null;
  }

  
  /*
   * start recording audio
   */
  private void startRecording ( ) 
  {
      mRecorder = new MediaRecorder ( );
      mRecorder.setAudioSource ( MediaRecorder.AudioSource.MIC );
      mRecorder.setOutputFormat ( MediaRecorder.OutputFormat.THREE_GPP );
      mRecorder.setOutputFile ( mFileName );
      mRecorder.setAudioEncoder ( MediaRecorder.AudioEncoder.AMR_NB );

      try 
      {
          mRecorder.prepare ( );
      } 
      catch ( IOException e ) 
      {
          Log.e ( LOG_TAG, "prepare() failed" );
      }

      mRecorder.start();
  }

  
  /*
   * stop recording audio
   */
  private void stopRecording ( )
  {
      mRecorder.stop ( );
      mRecorder.release ( );
      mRecorder = null;
  }

  
  /*
   * create record audio button
   */
  class RecordButton extends Button 
  {
      boolean mStartRecording = true;

      OnClickListener clicker = new OnClickListener ( )
      {
    	  
          public void onClick ( View v ) 
          {
        	  Log.v( "Concept", "Record button pressed" );
              onRecord ( mStartRecording );
              
              if ( mStartRecording ) 
              {
                  setText ( "Stop recording" );
              } 
              else 
              {
                  setText ( "Start recording" );
              }
              
              mStartRecording = !mStartRecording;
              
          }
      };

      public RecordButton(Context ctx)
      {
          super ( ctx );
          
          setText ( "Start recording" );
          setOnClickListener ( clicker );
      }
  }

  
  /*
   * create play audio button
   */
  class PlayButton extends Button 
  {
      boolean mStartPlaying = true;

      OnClickListener clicker = new OnClickListener ( ) 
      {
    	  
          public void onClick ( View v )
          {
        	  Log.v ( "Concept", "Play button pressed" );
              onPlay ( mStartPlaying );
              
              if ( mStartPlaying )
              {
                  setText ( "Stop playing" );
              }
              else
              {
                  setText ( "Start playing" );
              }
              
              mStartPlaying = !mStartPlaying;
              
          }
      };

      public PlayButton ( Context ctx ) 
      {
          super ( ctx );
          setText ("Start playing");
          setOnClickListener ( clicker );
      }
  }

  
  /*
   * Sets mFileName, however this is not called
   */
  public void AudioRecordTest() {
      mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
      mFileName += "/audiorecordtest.3gp";
  }

  /*  
  @Override
  public void onCreate(Bundle icicle) {
      super.onCreate(icicle);

      LinearLayout ll = new LinearLayout(this);
      mRecordButton = new RecordButton(this);
      ll.addView(mRecordButton,
          new LinearLayout.LayoutParams(
              ViewGroup.LayoutParams.WRAP_CONTENT,
              ViewGroup.LayoutParams.WRAP_CONTENT,
              0));
      mPlayButton = new PlayButton(this);
      ll.addView(mPlayButton,
          new LinearLayout.LayoutParams(
              ViewGroup.LayoutParams.WRAP_CONTENT,
              ViewGroup.LayoutParams.WRAP_CONTENT,
              0));
      setContentView(ll);
  }

  @Override
  public void onPause() {
      super.onPause();
      if (mRecorder != null) {
          mRecorder.release();
          mRecorder = null;
      }

      if (mPlayer != null) {
          mPlayer.release();
          mPlayer = null;
      }
  }
*/
  
  /*
   * (non-Javadoc)
   * @see android.app.Activity#onCreate(android.os.Bundle)
   */
  @Override
  public void onCreate ( Bundle savedInstanceState )
  {
	  
    super.onCreate ( savedInstanceState );
    setContentView ( R.layout.main );

    preview = ( SurfaceView ) findViewById ( R.id.preview );
    
    previewHolder = preview.getHolder ( );
    previewHolder.addCallback(surfaceCallback);
    previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    
    LinearLayout ll = new LinearLayout ( this );
    mRecordButton = new RecordButton ( this );
    
    ll.addView(mRecordButton,
        new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            0));
    
    mPlayButton = new PlayButton(this);
    ll.addView ( mPlayButton,
        new LinearLayout.LayoutParams (
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            0 ) );
    
    setContentView ( ll );

    addKeyListener ( );
  }

  
  
  /*
   * (non-Javadoc)
   * @see android.app.Activity#onResume()
   */
  @Override
  public void onResume ( )
  {
    super.onResume ( );

    if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD)
    {
      Camera.CameraInfo info = new Camera.CameraInfo ( );

      for ( int i=0; i < Camera.getNumberOfCameras ( ); i++ )
      {
        Camera.getCameraInfo ( i, info );

        if ( info.facing == Camera.CameraInfo.CAMERA_FACING_BACK )
        {
          camera=Camera.open ( i );
        }
      }
    }

    if ( camera == null )
    {
      camera=Camera.open ( );
    }
    
    startPreview ( );
  }

  
  
  /*
   * (non-Javadoc)
   * @see android.app.Activity#onPause()
   */
  @Override
  public void onPause ( )
  {
    if ( inPreview ) 
    {
      camera.stopPreview ( );
    }

    camera.release ( );
    camera=null;
    inPreview=false;
    
    if ( mRecorder != null )
    {
        mRecorder.release ( );
        mRecorder = null;
    }

    if (mPlayer != null) {
        mPlayer.release();
        mPlayer = null;
    }

    super.onPause();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    new MenuInflater(this).inflate(R.menu.options, menu);

    return(super.onCreateOptionsMenu(menu));
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.camera) {
      if (inPreview) {
        camera.takePicture(null, null, photoCallback);
        inPreview=false;
      }
    }

    return(super.onOptionsItemSelected(item));
  }

  private Camera.Size getBestPreviewSize(int width, int height,
                                         Camera.Parameters parameters) {
    Camera.Size result=null;

    for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
      if (size.width <= width && size.height <= height) {
        if (result == null) {
          result=size;
        }
        else {
          int resultArea=result.width * result.height;
          int newArea=size.width * size.height;

          if (newArea > resultArea) {
            result=size;
          }
        }
      }
    }

    return(result);
  }

  private Camera.Size getSmallestPictureSize(Camera.Parameters parameters) {
    Camera.Size result=null;

    for (Camera.Size size : parameters.getSupportedPictureSizes()) {
      if (result == null) {
        result=size;
      }
      else {
        int resultArea=result.width * result.height;
        int newArea=size.width * size.height;

        if (newArea < resultArea) {
          result=size;
        }
      }
    }

    return(result);
  }

  private void initPreview(int width, int height) {
    if (camera != null && previewHolder.getSurface() != null) {
      try {
        camera.setPreviewDisplay(previewHolder);
      }
      catch (Throwable t) {
        Log.e("PreviewDemo-surfaceCallback",
              "Exception in setPreviewDisplay()", t);
        Toast.makeText(FullscreenActivity.this, t.getMessage(),
                       Toast.LENGTH_LONG).show();
      }

      if (!cameraConfigured) {
        Camera.Parameters parameters=camera.getParameters();
        Camera.Size size=getBestPreviewSize(width, height, parameters);
        Camera.Size pictureSize=getSmallestPictureSize(parameters);

        if (size != null && pictureSize != null) {
          parameters.setPreviewSize(size.width, size.height);
          parameters.setPictureSize(pictureSize.width,
                                    pictureSize.height);
          parameters.setPictureFormat(ImageFormat.JPEG);
          camera.setParameters(parameters);
          cameraConfigured=true;
        }
      }
    }
  }

  private void startPreview() {
    if (cameraConfigured && camera != null) {
      camera.startPreview();
      inPreview=true;
    }
  }

  SurfaceHolder.Callback surfaceCallback=new SurfaceHolder.Callback() {
    public void surfaceCreated(SurfaceHolder holder) {
      // no-op -- wait until surfaceChanged()
    }

    public void surfaceChanged(SurfaceHolder holder, int format,
                               int width, int height) {
      initPreview(width, height);
      startPreview();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
      // no-op
    }
  };

  Camera.PictureCallback photoCallback=new Camera.PictureCallback() {
    public void onPictureTaken(byte[] data, Camera camera) {
      Log.v("Concept", "Picture Taken!");
      new SavePhotoTask().execute(data);
      camera.startPreview();
      inPreview=true;
    }
  };

  class SavePhotoTask extends AsyncTask<byte[], String, String> {
    @Override
    protected String doInBackground(byte[]... jpeg) {
      File photo=
          new File(Environment.getExternalStorageDirectory(),
                   "photo.jpg");									// /mnt/SDCard/photo.jpg

      if (photo.exists()) {
        photo.delete();
      }

      try {
    	edittext = (EditText) findViewById(R.id.editTeam);
    	Log.v("Concept", edittext.getText().toString());
    	Log.v("Concept", "File Output Stream");
        FileOutputStream fos=new FileOutputStream(photo.getPath());
        Log.v("Concept", "Write File");
        fos.write(jpeg[0]);
        Log.v("Concept", "Close File");
        fos.close();
        
        
        /**********  File Path *************/
        final String uploadFilePath = "/mnt/sdcard/";
        final String uploadFileName = "photo.jpg";
        String sourceFileUri = uploadFilePath + uploadFileName;
        String fileName = sourceFileUri;
        
        HttpURLConnection conn = null;
        DataOutputStream dos = null;  
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        
        int bytesRead, bytesAvailable, bufferSize;
        
        int serverResponseCode = 0;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024; 
        
        FileInputStream fileInputStream = new FileInputStream(fileName);
        URL url = new URL("http://rogerlsmith.net/app.php");
        
        conn = (HttpURLConnection) url.openConnection(); 
        conn.setDoInput(true); // Allow Inputs
        conn.setDoOutput(true); // Allow Outputs
        conn.setUseCaches(false); // Don't use a Cached Copy
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Connection", "Keep-Alive");
        conn.setRequestProperty("ENCTYPE", "multipart/form-data");
        conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
        conn.setRequestProperty("uploaded_file", fileName);
//        conn.setRequestProperty("team_number", edittext.getText().toString());
 
        dos = new DataOutputStream(conn.getOutputStream());        
        dos.writeBytes(twoHyphens + boundary + lineEnd); 
        dos.writeBytes("Content-Disposition: form-data; name='uploaded_file';filename='" + fileName + "'" + lineEnd);
        dos.writeBytes(lineEnd);
        
        bytesAvailable = fileInputStream.available(); 
        
        bufferSize = Math.min(bytesAvailable, maxBufferSize);
        buffer = new byte[bufferSize];
        
        bytesRead = fileInputStream.read(buffer, 0, bufferSize);  
        
        while (bytesRead > 0) {
             
          dos.write(buffer, 0, bufferSize);
          bytesAvailable = fileInputStream.available();
          bufferSize = Math.min(bytesAvailable, maxBufferSize);
          bytesRead = fileInputStream.read(buffer, 0, bufferSize);   
           
        }

        // send multipart form data necessary after file data...
        dos.writeBytes(lineEnd);
        dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
        
        serverResponseCode = conn.getResponseCode();
        String serverResponseMessage = conn.getResponseMessage();
       
        //close the streams //
        fileInputStream.close();
        dos.flush();
        dos.close();
        
      }
      catch (java.io.IOException e) {
        Log.e("Concept", "Exception in photoCallback", e);
      }

      return(null);
    }
  }
  
  
  public void addKeyListener() {
	  
		// get editteam component
		edittext = (EditText) findViewById(R.id.editTeam);
	 
		if (edittext != null) {
			// add a keylistener to keep track user input
			edittext.setOnKeyListener(new OnKeyListener() {
				public boolean onKey(View v, int keyCode, KeyEvent event) {
		 
					// if keydown and "enter" is pressed
					if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
			 
						// display a floating message
						Toast.makeText(FullscreenActivity.this,
							edittext.getText(), Toast.LENGTH_LONG).show();
						return true;
			 
					} else if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_9)) {
			 
						// display a floating message
						Toast.makeText(FullscreenActivity.this,
							"Number 9 is pressed!", Toast.LENGTH_LONG).show();
						return true;
					}
			 
					return false;
				}
			});
		}
	}
}