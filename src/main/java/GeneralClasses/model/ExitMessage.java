package GeneralClasses.model;

public class ExitMessage implements CloudMessage{
    @Override
    public CommandType getType() {
        return CommandType.EXIT;
    }
}
