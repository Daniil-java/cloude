package GeneralClasses.model;

public class ShareFileRequest implements CloudMessage{

    @Override
    public CommandType getType() {
        return CommandType.SHARE_FILE_REQUEST;
    }
}
