package GeneralClasses.model;

import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ShareListMessage implements CloudMessage{

    private List<String> files = new ArrayList<>();

    public ShareListMessage(List<String> paths) throws IOException {
        files = paths;
//        List<String> temp;
//        for (Path path : paths) {
//            temp = Files.list(path)
//                    .map(p -> p.getFileName().toString())
//                    .collect(Collectors.toList());
//
//            files.addAll(temp);
//        }
    }

    @Override
    public CommandType getType() {
        return CommandType.SHARE_LIST;
    }
}
