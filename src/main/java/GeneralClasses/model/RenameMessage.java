package GeneralClasses.model;

import lombok.Data;

@Data
public class RenameMessage implements CloudMessage{

    private final String oldName;
    private final String newName;

    @Override
    public CommandType getType() {
        return CommandType.RENAME;
    }
}
