/* Do not remove or modify this comment!  It is required for file identification!
DNL
platform:/resource/ReducedFM/src/Models/dnl/Sensor2.dnl
 Do not remove or modify this comment!  It is required for file identification! */
package Models.java;

import java.io.Serializable;

public class Stimulus implements Serializable {
    private static final long serialVersionUID = 1L;

    //ID:VAR:Stimulus:0
    Integer value;

    //ENDIF
    public Stimulus() {
    }

    public Stimulus(Integer value) {
        this.value = value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }

    public String toString() {
        String str = "Stimulus";
        str += "\n\tvalue: " + this.value;
        return str;
    }
}
