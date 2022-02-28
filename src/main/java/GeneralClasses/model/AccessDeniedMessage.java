package GeneralClasses.model;

public class AccessDeniedMessage implements CloudMessage {


    @Override
    public CommandType getType() {
        return CommandType.ACCESS_DENIED_MESSAGE;
    }
}
