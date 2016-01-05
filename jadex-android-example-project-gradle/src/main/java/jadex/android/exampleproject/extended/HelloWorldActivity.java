package jadex.android.exampleproject.extended;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

import jadex.android.JadexAndroidActivity;
import jadex.android.exampleproject.R;

/**
 * Hello World Activity.
 * Can Launch a platform and run agents.
 */
public class HelloWorldActivity extends JadexAndroidActivity {

	static final int REQUEST_IMAGE_CAPTURE = 1;
	private IntentFilter filter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		filter = new IntentFilter();
		filter.addAction("foto");

		Button camera = (Button) findViewById(R.id.camera);
		camera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//wenn der CameraButton geklickt wird dann wird die Kamera aufgerufen und man kann ein Bild machen
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				if (intent.resolveActivity(getPackageManager()) != null) {
					startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
				}
			}
		});



	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//Wenn ein Bild gemacht wurde wird dies als Bundle im Intent data übergeben, welches dann hier in ein Bytearray
		//konvertiert wird
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			Bitmap bmp = (Bitmap) extras.get("data");

			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
			byte[] byteArray = stream.toByteArray();

			//Log.d("Test", byteArray.toString() + "    " + byteArray.length);
			// LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter());

			int level = ((int) getBatteryLevel());
			boolean wifi = isWifiAvailable();

			Intent intent = new Intent(this, MyJadexService.class);
			intent.putExtra("Bytearray", byteArray);
			intent.putExtra("level",level);
			intent.putExtra("wifi",wifi);

			startService(intent);

		}

	}

	@Override
	public void onResume() {
		super.onResume();
		registerReceiver(receiverFoto, filter);
	}

	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(receiverFoto);
	}

	private BroadcastReceiver receiverFoto = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {

			byte[] fertigesBild = intent.getByteArrayExtra("fertigesBild");
			Bitmap image = BitmapFactory.decodeByteArray(fertigesBild, 0, fertigesBild.length);

			ImageView view = (ImageView) findViewById(R.id.imageView);
			view.setImageBitmap(image);
		}
	};

	public float getBatteryLevel() {

		Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
		int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

		return ((float)level / (float)scale) * 100;
	}


	private boolean isWifiAvailable() {

		final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

		//mobiles Internet = 0 WIFI = 1


		if (activeNetwork.getType() == 1) {
			return true;
		} else {

			return false;
		}
	}


	public boolean onCreateOptionsMenu(Menu menu) {
		MenuItem controlCenterMenuItem = menu.add(0, 0, 0, "Control Center");
		controlCenterMenuItem.setIcon(android.R.drawable.ic_menu_manage);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		return true;
	}
	// END -------- show control center in menu ---------

}