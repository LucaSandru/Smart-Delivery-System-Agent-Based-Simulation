package agents;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.basic.Action;
import jade.lang.acl.ACLMessage;
import ontology.*;
import java.util.Random;


public class CourierAgent extends Agent {
    private SLCodec codec = new SLCodec();
    private Ontology ontology = DeliveryOntology.getInstance();

    @Override
    protected void setup() {
        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontology);

        System.out.println(getLocalName() + " is ready to receive requests.");

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {
                ACLMessage msg = receive();
                if (msg != null && msg.getPerformative() == ACLMessage.REQUEST) {
                    try {
                        Object content = getContentManager().extractContent(msg);
                        if (content instanceof Action) {
                            Object act = ((Action) content).getAction();
                            if (act instanceof DeliveryRequest) {
                                DeliveryRequest req = (DeliveryRequest) act;

                                System.out.println(getLocalName() + ": Received delivery request for " + req.getPackageId());


                                double basePrice = 5.0;
                                double pricePerKm = 0.8;
                                double pricePerKg = 1.2;
                                double urgencyMultiplier = 1.0;

                                if ("High".equalsIgnoreCase(req.getUrgency())) {
                                    urgencyMultiplier = 1.5;
                                } else if ("Low".equalsIgnoreCase(req.getUrgency())) {
                                    urgencyMultiplier = 0.9;
                                }

                                Random rand = new Random();
                                double price = (basePrice + rand.nextDouble(5, 15) + 
                                               (req.getDistance() * pricePerKm) + 
                                               (req.getWeight() * pricePerKg)) * urgencyMultiplier;

                                // Simulate estimated delivery time
                                int deliveryTime = rand.nextInt(1, 5) + (int)(req.getDistance() / 10);

                                // === CREATE OFFER ===
                                DeliveryOffer offer = new DeliveryOffer();
                                offer.setCourierName(getLocalName());
                                offer.setPrice((float) price);
                                offer.setDeliveryTimeHours(deliveryTime);
                                offer.setPackageId(req.getPackageId());

                                ACLMessage reply = msg.createReply();
                                reply.setPerformative(ACLMessage.PROPOSE);
                                reply.setLanguage(codec.getName());
                                reply.setOntology(ontology.getName());

                                getContentManager().fillContent(reply, new Action(getAID(), offer));
                                send(reply);

                                System.out.println(getLocalName() + ": Sent offer -> Price: " + price + " | Time: " + deliveryTime + "h");
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    block();
                }
            }
        });
    }
}
