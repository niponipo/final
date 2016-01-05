	package jadex.android.exampleproject.extended.agent;

import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.chat.IChatService;
import jadex.bridge.service.types.context.IContextService;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentService;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import tuorial.IClientAgentInterface;
import tuorial.IServerAgentInterface;

/**
 *  Simple example agent that shows messages
 *  when it is started or stopped.
 */
@Description("Sample Android Agent.")
@ProvidedServices({
        @ProvidedService(name="agentinterface", type= IClientAgentInterface.class)
})
@RequiredServices({
        @RequiredService(name="context", type=IContextService.class, binding=@Binding(scope= RequiredServiceInfo.SCOPE_PLATFORM)),
        @RequiredService(name="test",type = IServerAgentInterface.class, binding = @Binding(scope = RequiredServiceInfo.SCOPE_GLOBAL, dynamic = true),multiple=true),
        @RequiredService(name="chatservices",type = IChatService.class, binding = @Binding(scope = RequiredServiceInfo.SCOPE_GLOBAL))
})
@Service
public class MyAgent2 implements IServerAgentInterface {
    /**
     * Context service injected at startup.
     */
    @AgentService
    protected IContextService context;

    @Agent
    protected IInternalAccess inAgent;

    @Agent
    protected IExternalAccess exAgent;

    byte[] byteArray;

    @Override
    public void message(String sender, String text) {

    }

    @Override
    public IFuture<Integer> test(int i) {
        int x = 234567 + i;
      IFuture<Integer> future = new jadex.commons.future.Future<Integer>(x);
        return future;

    }

    @Override
    public IFuture<String> getString() {
        return null;
    }

    @Override
    public void erkenneGesicht(byte[] byteArray) {

    }
}