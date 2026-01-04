package agents;

import jade.core.Agent;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.*;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.basic.Action;
import ontology.*;

import java.util.*;

public class DispatcherAgent extends Agent {

    private SLCodec codec = new SLCodec();
    private Ontology ontology = DeliveryOntology.getInstance();

    private List<ACLMessage> offers = new ArrayList<>();
    private ACLMessage originalRequest;

    private String selectedCourier;
    private DeliveryOffer selectedOffer;

    @Override
    protected void setup() {

        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontology);

        System.out.println(getLocalName() + " is up and coordinating deliveries.");

        addBehaviour(new CyclicBehaviour() {
            @Override
            public void action() {

                ACLMessage msg = receive();
                if (msg == null) {
                    block();
                    return;
                }

                try {
                    Object content = getContentManager().extractContent(msg);

                    if (msg.getPerformative() == ACLMessage.REQUEST && content instanceof Action) {
                        Action act = (Action) content;

                        if (act.getAction() instanceof DeliveryRequest) {
                            originalRequest = msg;

                            DeliveryRequest delivery = (DeliveryRequest) act.getAction();
                            System.out.println(getLocalName() + ": Received request for package '" + delivery.getPackageId() + "'. Forwarding request to couriers...");


                            ACLMessage forward = new ACLMessage(ACLMessage.REQUEST);
                            forward.setLanguage(codec.getName());
                            forward.setOntology(ontology.getName());

                            forward.addReceiver(new AID("courier1", AID.ISLOCALNAME));
                            forward.addReceiver(new AID("courier2", AID.ISLOCALNAME));

                            getContentManager().fillContent(forward, act);
                            send(forward);
                        }
                    }

                    
                    else if (msg.getPerformative() == ACLMessage.PROPOSE && content instanceof Action) {
                        Action act = (Action) content;

                        if (act.getAction() instanceof DeliveryOffer) {
                            offers.add(msg);

                            if (offers.size() == 2) { // 2 couriers for now
                                ACLMessage best = chooseBestOffer();
                                sendDecisionToCustomer(best);
                                offers.clear();
                            }
                        }
                    }


                    else if (msg.getPerformative() == ACLMessage.INFORM && content instanceof Action) {
                        Object act = ((Action) content).getAction();

                        if (act instanceof ConfirmationResponse) {
                            ConfirmationResponse resp = (ConfirmationResponse) act;

                            if (resp.isConfirmed()) {
                            	System.out.println("dispatcher: Customer confirmed. Notifying " + selectedCourier);
                            	System.out.println(selectedCourier + " is going to the destination...");

                                ACLMessage notify = new ACLMessage(ACLMessage.INFORM);
                                notify.addReceiver(new AID(selectedCourier, AID.ISLOCALNAME));
                                notify.setLanguage(codec.getName());
                                notify.setOntology(ontology.getName());
                                notify.setContent("Delivery confirmed. Proceed.");

                                send(notify);
                                
                                System.out.println("dispatcher: Shutting down...");
                                doDelete();

                            }
                            else {
                                System.out.println("dispatcher: Customer rejected the offer.");
                            }
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private ACLMessage chooseBestOffer() {

        ACLMessage best = offers.get(0);

        try {
            DeliveryOffer offer1 = (DeliveryOffer) ((Action) getContentManager()
                    .extractContent(offers.get(0))).getAction();

            DeliveryOffer offer2 = (DeliveryOffer) ((Action) getContentManager()
                    .extractContent(offers.get(1))).getAction();

            if (offer2.getPrice() < offer1.getPrice()) {
                best = offers.get(1);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return best;
    }


    private void sendDecisionToCustomer(ACLMessage bestOfferMsg) {

        try {
            Action a = (Action) getContentManager().extractContent(bestOfferMsg);

            selectedOffer = (DeliveryOffer) a.getAction();
            selectedCourier = selectedOffer.getCourierName();

            ACLMessage result = originalRequest.createReply();
            result.setPerformative(ACLMessage.INFORM);
            result.setLanguage(codec.getName());
            result.setOntology(ontology.getName());
            getContentManager().fillContent(result, a);
            send(result);

            System.out.println(getLocalName() + ": Sent best offer to " + getLocalName());

            ConfirmationRequest confirmReq = new ConfirmationRequest();
            confirmReq.setOffer(selectedOffer);

            ACLMessage confirmMsg = new ACLMessage(ACLMessage.REQUEST);
            confirmMsg.addReceiver(originalRequest.getSender());
            confirmMsg.setLanguage(codec.getName());
            confirmMsg.setOntology(ontology.getName());
            getContentManager().fillContent(confirmMsg, new Action(getAID(), confirmReq));
            send(confirmMsg);

            System.out.println(getLocalName() + ": Asking customer for confirmation...");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
