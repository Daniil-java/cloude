package GeneralClasses.Processors;

import GeneralClasses.model.CloudMessage;

public interface MessageProcessor {

    void processMessage(CloudMessage msg);

}
