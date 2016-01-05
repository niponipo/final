package tuorial;

import jadex.commons.future.IFuture;

public interface IServerAgentInterface
{
    void message(String sender, String text);

    IFuture<String> getString();

    IFuture<byte[]> erkenneGesichRemote(byte[] byteArray);
}
