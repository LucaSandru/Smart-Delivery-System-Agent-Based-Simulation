package ontology;
import jade.content.AgentAction;

public class ConfirmationResponse implements AgentAction {
    private boolean confirmed;

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}
