package GeneralClasses.model;

import java.io.Serializable;

public interface CloudMessage extends Serializable {
    CommandType getType();
}
