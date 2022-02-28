package GeneralClasses.model;

import GeneralClasses.model.CloudMessage;
import GeneralClasses.model.CommandType;

public class AccessedMessage implements CloudMessage {

    @Override
    public CommandType getType() {
        return CommandType.ACCESSED_MESSAGE;
    }
}
