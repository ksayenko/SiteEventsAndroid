package com.honeywell.stevents;
import java.io.Serializable;
public class MeasurementTypes implements Serializable {

    public enum MEASUREMENT_TYPES {
         PH, VOC, NOISE, SE,OTHER;

        @Override
        public String toString() {
            return "MEASUREMENT_TYPES{}";
        }

        public int value() {

            if (this.toString().equals("PH")) return 0;
            if (this.toString().equals("VOC")) return 1;
            if (this.toString().equals("NOISE")) return 2;
            if (this.toString().equals("SE")) return 3;

            else return 4;
        }

    }

    public static MEASUREMENT_TYPES GetFrom_SE_ID(String strEqID, String strEqType) {
        if (strEqID.toLowerCase().startsWith("ae"))
            return MEASUREMENT_TYPES.PH;

        if (strEqID.toLowerCase().startsWith("voc"))
            return MEASUREMENT_TYPES.VOC;

        //if (strEqID.toLowerCase().startsWith("noise"))
            return MEASUREMENT_TYPES.SE;

       // return MEASUREMENT_TYPES.OTHER;

    }

}

