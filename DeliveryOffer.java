package ontology;

import jade.content.Concept;

public class DeliveryOffer implements Concept {

    private String courierName;
    private float price;
    private int deliveryTimeHours;
    private String packageId;

    public String getCourierName() { return courierName; }
    public void setCourierName(String courierName) { this.courierName = courierName; }

    public float getPrice() { return price; }
    public void setPrice(float price) { this.price = price; }
    
    public String getPackageId() { return packageId; }
    public void setPackageId(String packageId) { this.packageId = packageId; }

    public int getDeliveryTimeHours() { return deliveryTimeHours; }
    public void setDeliveryTimeHours(int deliveryTimeHours) { this.deliveryTimeHours = deliveryTimeHours; }
}
