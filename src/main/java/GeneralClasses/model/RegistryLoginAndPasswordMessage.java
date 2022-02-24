package GeneralClasses.model;

import lombok.Data;
import org.json.simple.JSONObject;

@Data
public class RegistryLoginAndPasswordMessage implements CloudMessage{

//    private final String login;
//    private final String password;

    private final JSONObject jsonMessage;

    @Override
    public CommandType getType() {
        return CommandType.REGISTRY_LOGIN_AND_PASSWORD_MESSAGE;
    }
}
