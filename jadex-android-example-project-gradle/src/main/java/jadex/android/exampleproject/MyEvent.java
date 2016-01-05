package jadex.android.exampleproject;
import jadex.bridge.service.types.context.JadexAndroidEvent;

/**
 * Created by Nico on 10.10.2015.
 */
public class MyEvent extends JadexAndroidEvent {

	private Integer anzahl;
	private byte[] fertigesBild;

	public byte[] getFertigesBild()
	{
		return fertigesBild;
	}

	public void setFertigesBild(byte[] fertigesBild)
	{
		this.fertigesBild = fertigesBild;
	}


	public Integer getAnzahl()
	{
		return anzahl;
	}

	public void setAnzahl(Integer anzahl)
	{
		this.anzahl = anzahl;
	}


}
