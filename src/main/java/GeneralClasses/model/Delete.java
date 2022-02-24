package GeneralClasses.model;

import lombok.Data;

@Data
public class Delete implements CloudMessage {

    private final String fileName;

    @Override
    public CommandType getType() {
        return CommandType.DELETE;
    }
}
