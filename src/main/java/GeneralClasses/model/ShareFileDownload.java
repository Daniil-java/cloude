package GeneralClasses.model;

import lombok.Data;

@Data
public class ShareFileDownload implements CloudMessage{

    private final String dirName;

    @Override
    public CommandType getType() {
        return CommandType.SHARE_FILE_DOWNLOAD;
    }
}
