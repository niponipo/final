package jadex.android.exampleproject.extended.agent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.objdetect.CascadeClassifier;

import java.io.ByteArrayOutputStream;
import java.util.Collection;
import java.util.Iterator;

import jadex.android.exampleproject.MyEvent;
import jadex.android.exampleproject.R;
import jadex.bridge.FactoryFilter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.context.IContextService;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroAgentFactory;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentService;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import tuorial.IClientAgentInterface;
import tuorial.IServerAgentInterface;

/**
 * Created by Nico on 07.08.2015.
 */

@Service
@Agent

@ProvidedServices({@ProvidedService(name = "agentinterface", type = IClientAgentInterface.class)})
@RequiredServices({@RequiredService(name = "context", type = IContextService.class, binding = @Binding(scope = RequiredServiceInfo.SCOPE_GLOBAL)),
		@RequiredService(name="test",type = IServerAgentInterface.class, binding = @Binding(scope = RequiredServiceInfo.SCOPE_GLOBAL, dynamic = true),multiple=true)

		})


public class MyAgent implements IClientAgentInterface {

	@Agent
	protected IInternalAccess inAgent;

	@Agent
	protected IExternalAccess exAgent;

	@AgentService
	protected IContextService context;

	//Hier läuft die eigentliche Gesichtserkenung ab, die Anzahl der Gesichter soll hier erstmal
	//im LogCat ausgegeben werden

	//@Override
	public void erkenneGesichtLokal(byte[] bytearray) {
		CascadeClassifier faceDetector;

		Bitmap image = BitmapFactory.decodeByteArray(bytearray, 0, bytearray.length);
		Mat mat = new Mat();
		Utils.bitmapToMat(image, mat);
		faceDetector = new CascadeClassifier("/data/data/jadex.android.exampleproject/app_cascade/lbpcascade_frontalface.xml");


		MatOfRect faceDetections = new MatOfRect();
		faceDetector.detectMultiScale(mat, faceDetections);

		Rect[] faces = faceDetections.toArray();

		for (int i = 0; i < faces.length; i++) {
			Core.rectangle(mat, faces[i].tl(), faces[i].br(), new Scalar(0, 255, 0, 255), 2);
		}

		Utils.matToBitmap(mat, image);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.PNG, 100, stream);
		byte[] fertigesBild = stream.toByteArray();


		int anzahl = faceDetections.toArray().length;
		Log.d("Anzahl im Agent", String.valueOf(faceDetections.toArray().length));

		sendEvent(fertigesBild);

	}


	public void sucheRemoteService(final byte[] bytearray){
		//Log.d("level",String.valueOf(level));
		//Log.d("wifi",String.valueOf(wifi));
		exAgent = inAgent.getExternalAccess();
		exAgent.scheduleStep(new IComponentStep<Void>() {
			@Override
			public IFuture<Void> execute(final IInternalAccess ia) {

				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				IIntermediateFuture<IServerAgentInterface>	fut	= ia.getComponentFeature(IRequiredServicesFeature.class).getRequiredServices("test");
								fut.addResultListener(new IIntermediateResultListener<IServerAgentInterface>()
								{
									public void resultAvailable(Collection<IServerAgentInterface> result)
									{
										for(Iterator<IServerAgentInterface> it=result.iterator(); it.hasNext(); )
										{
											IServerAgentInterface cs = it.next();
											try
											{
												System.out.println(cs);
											}
											catch(Exception e)
											{
												e.printStackTrace();
							}
						}
					}

					public void intermediateResultAvailable(IServerAgentInterface cs)
					{
						IFuture<byte[]> futFertigesBild = cs.erkenneGesichRemote(bytearray);
						byte[] fertigesBild = futFertigesBild.get();
						sendEvent(fertigesBild);



					}

					public void finished()
					{
						System.out.println("finished");
					}

					public void exceptionOccurred(Exception exception)
					{
						exception.printStackTrace();
					}

				});
				return IFuture.DONE;
			}
		});

	}


	@Override
	public void sollAusgelagertWerden(byte[] bytearray, int level, boolean wifi) {

		//Wenn WIFI vorhanden ist soll immer ausgelagert werden
		//Wenn kein WIFI vorhanden ist soll immer lokal ausgeführt werden außer Batterielevel liegt unter 20&
		if (wifi == false && level < 20) {
			sucheRemoteService(bytearray);
		}
		else {
			erkenneGesichtLokal(bytearray);
		}
	}

	private void sendEvent(byte[] fertigesBild){

		MyEvent myEvent = new MyEvent();
		myEvent.setFertigesBild(fertigesBild);
		context.dispatchEvent(myEvent);
	}
}
