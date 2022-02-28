package GeneralClasses.model;

import lombok.Data;

@Data
public class ChangeInServerDir implements CloudMessage {

    private final String fileName;


    @Override
    public CommandType getType() {
        return CommandType.CHANGE_IN_SERVER_DIR;
    }
}
