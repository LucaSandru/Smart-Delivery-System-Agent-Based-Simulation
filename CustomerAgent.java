package agents;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.basic.Action;
import jade.core.behaviours.CyclicBehaviour;
import ontology.*;

import java.util.Scanner;

public class CustomerAgent extends Agent {
    private SLCodec codec = new SLCodec();
    private Ontology ontology = DeliveryOntology.getInstance();

    @Override
    protected void setup() {
        getContentManager().registerLanguage(codec);
        getContentManager().registerOntology(ontology);

        Object[] args = getArguments();

        if (args != null && args.length >= 1) {
            String[] parts = args[0].toString().trim().split("\\s+");

            if (parts.length < 4) {
            	System.out.println(getLocalName() + ": Not enough arguments.");
            	System.out.println("Usage: packageID(name of package) destination(city name) weight(kg) distance(km) [urgency]");
                doDelete();
                return;
            }

            String packageId = parts[0];
            String destination = parts[1];
            float weight = Float.parseFloat(parts[2]);
            float distance = Float.parseFloat(parts[3]);
            String urgency = (parts.length >= 5) ? parts[4] : "Normal";

            System.out.println(getLocalName() + ": Sending delivery request for package '" + packageId +
                    "', to destination '" + destination + "', with a weight of " + weight + " kg and distance of " + distance + " km...");


            DeliveryRequest request = new DeliveryRequest();
            request.setPackageId(packageId);
            request.setDestination(destination);
            request.setWeight(weight);
            request.setDistance(distance);
            request.setUrgency(urgency);

            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
            msg.addReceiver(new jade.core.AID("dispatcher", jade.core.AID.ISLOCALNAME));
            msg.setLanguage(codec.getName());
            msg.setOntology(ontology.getName());

            try {
                getContentManager().fillContent(msg, new Action(getAID(), request));
                send(msg);
                System.out.println(getLocalName() + ": Delivery request sent.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(getLocalName() + ": Missing arguments. Usage: packageId destination weight distance [urgency]");
            doDelete();
        }

        addBehaviour(new CyclicBehaviour() {
            public void action() {
                ACLMessage msg = receive();
                if (msg != null && msg.getPerformative() == ACLMessage.REQUEST) {
                    try {
                        Object content = getContentManager().extractContent(msg);
                        if (content instanceof Action) {
                            Object act = ((Action) content).getAction();
                            if (act instanceof ConfirmationRequest) {
                                ConfirmationRequest req = (ConfirmationRequest) act;
                                DeliveryOffer offer = req.getOffer();

                                System.out.println(getLocalName() + " received best offer from " + offer.getCourierName()
                                        + " -> Price: " + offer.getPrice() + " | Time: " + offer.getDeliveryTimeHours() + "h");

                                Scanner scanner = new Scanner(System.in);
                                boolean validInput = false;
                                boolean isConfirmed = false;

                                while (!validInput) {
                                    System.out.print("Do you confirm the delivery? (Yes/No): ");
                                    String input = scanner.nextLine().trim().toLowerCase();

                                    if (input.equals("yes")) {
                                        isConfirmed = true;
                                        validInput = true;
                                    } else if (input.equals("no")) {
                                        isConfirmed = false;
                                        validInput = true;
                                    } else {
                                        System.out.println("Invalid input. Please type 'Yes' or 'No'.");
                                    }
                                }

                                ConfirmationResponse response = new ConfirmationResponse();
                                response.setConfirmed(isConfirmed);

                                ACLMessage reply = msg.createReply();
                                reply.setPerformative(ACLMessage.INFORM);
                                reply.setLanguage(codec.getName());
                                reply.setOntology(ontology.getName());

                                getContentManager().fillContent(reply, new Action(getAID(), response));
                                send(reply);

                                System.out.println(getLocalName() + ": Sent confirmation: " + (isConfirmed ? "YES" : "NO"));
                                
                                System.out.println(getLocalName() + ": Shutting down after confirmation...");
                                doDelete();

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
