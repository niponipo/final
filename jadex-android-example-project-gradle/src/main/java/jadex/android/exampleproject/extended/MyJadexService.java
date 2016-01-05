package jadex.android.exampleproject.extended;

import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.opencv.android.OpenCVLoader;

import jadex.android.EventReceiver;
import jadex.android.exampleproject.MyEvent;
import jadex.android.exampleproject.extended.agent.IAgentInterface;
import jadex.android.exampleproject.extended.agent.MyAgent;
import jadex.android.service.JadexPlatformManager;
import jadex.android.service.JadexPlatformService;
import jadex.base.PlatformConfiguration;
import jadex.base.RootComponentConfiguration;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.annotation.Security;
import jadex.commons.future.DefaultResultListener;
import tuorial.IClientAgentInterface;


/**
 * Created by Nico on 02.08.2015.
 */
@Security(Security.UNRESTRICTED)
public class MyJadexService extends JadexPlatformService {

	byte[] bytearray;
	int level;
	boolean wifi;

	// JadexPlatform wird gestartet
	public MyJadexService() {
		setPlatformAutostart(false);
		setPlatformName("NicosHandy");
		setPlatformKernels(JadexPlatformManager.KERNEL_MICRO);
		//setPlatformOptions("-awareness true");
		PlatformConfiguration config = getPlatformConfiguration();
		RootComponentConfiguration rootConfig = config.getRootConfig();
		rootConfig.setAwareness(true);
		rootConfig.setRelayTransport(true);
		rootConfig.setUsePass(false);


	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		registerEventReceiver(new EventReceiver<MyEvent>(MyEvent.class) {

			public void receiveEvent(final MyEvent event) {

				Intent intent = new Intent();;

				byte[] fertigesBild = event.getFertigesBild();
				intent.putExtra("fertigesBild", fertigesBild);

				sendBroadcast(intent);
			}
		});
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//Hier wird das Bild aus dem durchgereichten Bundle extrahiert
		startPlatform();
		bytearray = intent.getByteArrayExtra("Bytearray");
		level = intent.getIntExtra("level",12345);
		wifi = intent.getBooleanExtra("wifi",false);


		return START_STICKY;

	}

	@Override
	protected void onPlatformStarted(IExternalAccess result) {
		super.onPlatformStarted(result);

		//Wenn die Platform getstartet ist dann wird er Agent initialisiert
		startMicroAgent("MyAgent", MyAgent.class).addResultListener(new DefaultResultListener<IComponentIdentifier>() {
			@Override
			public void resultAvailable(IComponentIdentifier iComponentIdentifier) {
				// Diese Abfrage muss sein sonst funktioniert OpenCV nicht
				if (!OpenCVLoader.initDebug()) {
					Log.d("OpenCV", "nicht initialisiert");
				}
				//Hier wird OpenCV initialisiert
				// initializeOpenCVDependencies();

				try {
					IClientAgentInterface agent = getsService(IClientAgentInterface.class);

					//Hier wird die Methode erkenneGesicht() aufgerufen, es wird das Mat und
					//und der faceDectector übergeben, der in initializeOpenCVDependencies() erzeugt wird
					// agent.erkenneGesicht(bytearray);
					agent.sollAusgelagertWerden(bytearray,level,wifi);
				} catch (RuntimeException e) {
					Log.e("Fehler", "Hier ist der Fehler");

				}

			}
		});
	}
}
