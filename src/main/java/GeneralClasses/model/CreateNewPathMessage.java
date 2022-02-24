package GeneralClasses.model;

import lombok.Data;

@Data
public class CreateNewPathMessage implements CloudMessage {

    private final String dirName;

    public CreateNewPathMessage(String dirName) {
        this.dirName = dirName;
    }

    @Override
    public CommandType getType() {
        return CommandType.CREATE_NEW_PATH;
    }
}
