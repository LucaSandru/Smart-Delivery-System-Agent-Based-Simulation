package ontology;

import jade.content.onto.BeanOntology;
import jade.content.onto.BeanOntologyException;

public class DeliveryOntology extends BeanOntology {

    private static DeliveryOntology instance = new DeliveryOntology();

    public static DeliveryOntology getInstance() {
        return instance;
    }

    private DeliveryOntology() {
        super("Delivery-Ontology");

        try {
            add(DeliveryRequest.class);
            add(DeliveryOffer.class);
            add(ConfirmationRequest.class);
            add(ConfirmationResponse.class);

        } catch (BeanOntologyException e) {
            e.printStackTrace();
        }
    }
}
