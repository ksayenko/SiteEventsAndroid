package com.honeywell.stevents;
import androidx.annotation.NonNull;

import java.io.Serializable;
public class MeasurementTypes implements Serializable {

    public static enum MEASUREMENT_TYPES {
         PH, VOC, NOISE, GENERAL_BARCODE,OTHER;

   

        public String valueToString() {
            int rv = value();
            String str = "NA";
            str =  Integer.toString(rv);
            return str;
        }



        public int value() {

            if (this.toString().equals("PH")) return 0;
            if (this.toString().equals("VOC")) return 1;
            if (this.toString().equals("NOISE")) return 2;
            if (this.toString().equals("GENERAL_BARCODE")) return 3;

            else return 4;
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString();
        }
    }
    public static MEASUREMENT_TYPES  GetTypeFromNumber(Integer n)
    {
        MEASUREMENT_TYPES rvOther = MEASUREMENT_TYPES.OTHER;
        MEASUREMENT_TYPES rvph = MEASUREMENT_TYPES.PH;
        MEASUREMENT_TYPES rvVOC = MEASUREMENT_TYPES.VOC;
        MEASUREMENT_TYPES rvNOISE = MEASUREMENT_TYPES.NOISE;
        MEASUREMENT_TYPES rvGeneral = MEASUREMENT_TYPES.GENERAL_BARCODE;

        if (n==  rvph.value())
            return rvph;
        if (n==  rvVOC.value())
            return rvVOC;
        if (n==  rvNOISE.value())
            return rvNOISE;
        if (n==  rvGeneral.value())
            return rvGeneral;

        return rvOther;
    }

    public static MEASUREMENT_TYPES GetFrom_SE_ID(String strEqID, String strEqType) {
        if (strEqID.toLowerCase().startsWith("ae"))
            return MEASUREMENT_TYPES.PH;

        if (strEqID.toLowerCase().startsWith("voc"))
            return MEASUREMENT_TYPES.VOC;

        if (strEqID.toLowerCase().startsWith("noise"))
            return MEASUREMENT_TYPES.NOISE;

        if (strEqID.toLowerCase().startsWith("pctf-900"))
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

