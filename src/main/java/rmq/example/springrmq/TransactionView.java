package rmq.example.springrmq;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.UI;

@Route("view")
public class TransactionView extends VerticalLayout {

    private final TextField classicPublished = new TextField("Classic Queue Published");
    private final TextField classicConfirmed = new TextField("Classic Queue Confirmed");
    private final TextField quorumPublished = new TextField("Quorum Queue Published");
    private final TextField quorumConfirmed = new TextField("Quorum Queue Confirmed");
    private final TextField streamPublished = new TextField("Stream Queue Published");
    private final TextField streamConfirmed = new TextField("Stream Queue Confirmed");

    public TransactionView(DirectPublisherService publisherService) {
        classicPublished.setReadOnly(true);
        classicConfirmed.setReadOnly(true);
        quorumPublished.setReadOnly(true);
        quorumConfirmed.setReadOnly(true);
        streamPublished.setReadOnly(true);
        streamConfirmed.setReadOnly(true);

        add(classicPublished, classicConfirmed, quorumPublished, quorumConfirmed, streamPublished, streamConfirmed);

        // Set polling interval to 1 second
        UI.getCurrent().setPollInterval(1000);
        UI.getCurrent().addPollListener(event -> {
            classicPublished.setValue(String.valueOf(publisherService.getPublishedCount("classic.transactions")));
            classicConfirmed.setValue(String.valueOf(publisherService.getConfirmedCount("classic.transactions")));
            quorumPublished.setValue(String.valueOf(publisherService.getPublishedCount("quorum.transactions")));
            quorumConfirmed.setValue(String.valueOf(publisherService.getConfirmedCount("quorum.transactions")));
            streamPublished.setValue(String.valueOf(publisherService.getPublishedCount("stream.transactions")));
            streamConfirmed.setValue(String.valueOf(publisherService.getConfirmedCount("stream.transactions")));
        });
    }
}