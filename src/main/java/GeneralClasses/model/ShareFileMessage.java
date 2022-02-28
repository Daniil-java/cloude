package GeneralClasses.model;

import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Data
public class ShareFileMessage implements CloudMessage{

    private final String fileName;
    private final byte[] bytes;

    public ShareFileMessage(Path path) throws IOException {
        fileName = path.getFileName().toString();
        bytes = Files.readAllBytes(path);
    }

    @Override
    public CommandType getType() {
        return CommandType.SHARE_FILE;
    }
}
