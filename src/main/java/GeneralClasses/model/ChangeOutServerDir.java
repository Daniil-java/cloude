package GeneralClasses.model;


import lombok.Data;

@Data
public class ChangeOutServerDir implements CloudMessage {
    @Override
    public CommandType getType() {
        return CommandType.CHANGE_OUT_SERVER_DIR;
    }
}
