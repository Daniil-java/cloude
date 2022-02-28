package GeneralClasses.model;

import lombok.Data;

@Data
public class ViewServerDir implements CloudMessage {

    private final String serverDir;

    @Override
    public CommandType getType() {
        return CommandType.VIEW_SERVER_DIR;
    }
}
