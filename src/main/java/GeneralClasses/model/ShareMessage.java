package GeneralClasses.model;

import lombok.Data;
import org.json.simple.JSONObject;

@Data
public class ShareMessage implements CloudMessage{

    private final JSONObject jsonMessage;

    private final String fileName;

    @Override
    public CommandType getType() {
        return CommandType.SHARE;
    }
}
