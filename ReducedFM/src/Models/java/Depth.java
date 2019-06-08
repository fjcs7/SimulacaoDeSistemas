/* Do not remove or modify this comment!  It is required for file identification!
DNL
platform:/resource/ReducedFM/src/Models/dnl/Gateway.dnl
 Do not remove or modify this comment!  It is required for file identification! */
package Models.java;

import java.io.Serializable;

public class Depth implements Serializable {
    private static final long serialVersionUID = 1L;

    //ID:VAR:Depth:0
    Integer value;

    //ENDIF
    public Depth() {
    }

    public Depth(Integer value) {
        this.value = value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }

    public String toString() {
        String str = "Depth";
        str += "\n\tvalue: " + this.value;
        return str;
    }
}
