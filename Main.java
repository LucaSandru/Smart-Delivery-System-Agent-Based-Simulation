package agents;

import jade.core.Runtime;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

public class Main {
    public static void main(String[] args) {
        try {
            Runtime rt = Runtime.instance();

            Profile profile = new ProfileImpl();
            profile.setParameter(Profile.MAIN_HOST, "localhost");
            profile.setParameter(Profile.GUI, "true");
            
            ContainerController container = rt.createMainContainer(profile);
            container.createNewAgent("customer", "agents.CustomerAgent", null).start();
            container.createNewAgent("dispatcher", "agents.DispatcherAgent", null).start();
            container.createNewAgent("courier1", "agents.CourierAgent", null).start();
            container.createNewAgent("courier2", "agents.CourierAgent", null).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
