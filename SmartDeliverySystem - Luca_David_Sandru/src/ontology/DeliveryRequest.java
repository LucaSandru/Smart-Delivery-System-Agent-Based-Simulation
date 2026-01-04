package ontology;

import jade.content.Concept;

public class DeliveryRequest implements Concept {
    private String packageId;
    private float weight;
    private float distance;
    private String urgency;
    private String destination;
    
    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getPackageId() { return packageId; }
    public void setPackageId(String packageId) { this.packageId = packageId; }

    public float getWeight() { return weight; }
    public void setWeight(float weight) { this.weight = weight; }

    public float getDistance() { return distance; }
    public void setDistance(float distance) { this.distance = distance; }

    public String getUrgency() { return urgency; }
    public void setUrgency(String urgency) { this.urgency = urgency; }
}
