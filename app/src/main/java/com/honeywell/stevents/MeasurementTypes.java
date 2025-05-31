package com.honeywell.stevents;
import java.io.Serializable;
public class MeasurementTypes implements Serializable {

    public static enum MEASUREMENT_TYPES {
         PH, VOC, NOISE, GENERAL_BARCODE,OTHER;

        @Override
        public String toString() {
            return "MEASUREMENT_TYPES{}";
        }

        public int value() {

            if (this.toString().equals("PH")) return 0;
            if (this.toString().equals("VOC")) return 1;
            if (this.toString().equals("NOISE")) return 2;
            if (this.toString().equals("GENERAL_BARCODE")) return 3;

            else return 4;
        }

    }

    public static MEASUREMENT_TYPES GetFrom_SE_ID(String strEqID, String strEqType) {
        if (strEqID.toLowerCase().startsWith("ae"))
            return MEASUREMENT_TYPES.PH;

        if (strEqID.toLowerCase().startsWith("voc"))
            return MEASUREMENT_TYPES.VOC;

        if (strEqID.toLowerCase().startsWith("noise"))
            return MEASUREMENT_TYPES.NOISE;

        if (strEqID.toLowerCase().startsWith("PCTF-900"))
            return MEASUREMENT_TYPES.GENERAL_BARCODE;

        if (strEqType.toLowerCase().startsWith("noise"))
            return MEASUREMENT_TYPES.NOISE;

        if (strEqType.toLowerCase().startsWith("phi"))
            return MEASUREMENT_TYPES.PH;

        if (strEqType.toLowerCase().startsWith("voc"))
            return MEASUREMENT_TYPES.VOC;

        return MEASUREMENT_TYPES.OTHER;

    }

}

