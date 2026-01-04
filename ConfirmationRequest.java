package ontology;
import jade.content.AgentAction;

public class ConfirmationRequest implements AgentAction {
    private DeliveryOffer offer;

    public DeliveryOffer getOffer() {
        return offer;
    }

    public void setOffer(DeliveryOffer offer) {
        this.offer = offer;
    }
}
