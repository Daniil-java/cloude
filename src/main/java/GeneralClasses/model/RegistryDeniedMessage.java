package GeneralClasses.model;

public class RegistryDeniedMessage implements CloudMessage{

    @Override
    public CommandType getType() {
        return CommandType.REGISTRY_DENIED;
    }
}
