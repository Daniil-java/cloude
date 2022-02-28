package GeneralClasses.Processors;

import GeneralClasses.model.CloudMessage;
import GeneralClasses.model.CommandType;

import java.util.HashMap;
import java.util.Map;

public class ProcessorRegistry {

    private Map<CommandType, MessageProcessor> map;

    public ProcessorRegistry() {
        map = new HashMap<>();
        map.put(CommandType.FILE, msg -> {

        });
    }

    public void process(CloudMessage msg) {
        map.get(msg.getType()).processMessage(msg);
    }

}
