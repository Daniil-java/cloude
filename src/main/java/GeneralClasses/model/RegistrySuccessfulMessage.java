package GeneralClasses.model;

public class RegistrySuccessfulMessage implements CloudMessage{

    @Override
    public CommandType getType() {
        return CommandType.REGISTRY_SUCCESSFUL;
    }
}
