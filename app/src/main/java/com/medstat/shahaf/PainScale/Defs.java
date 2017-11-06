package com.medstat.shahaf.PainScale;

import okhttp3.MediaType;

/**
 * Created by Shahaf on 3/24/2016.
 */
public class Defs {
    public static final String INVALID_ID = "INVALID";
    public static final String PAIN_SCALE_ID = "painScaleID";
    public static final String SUCCESS = "success";
    public static final String FAILED = "failed";
    public static final String NO_DATA = "noData";
    public static final String LOGIN = "login";
    public static final String CHECK = "check";
    public static final String SEND = "send";
    public static final String serverAddressTest ="https://medinfo2.ise.bgu.ac.il:59144";
    public static final String serverAddressProduction ="https://mtbistudy.technion.ac.il";
    public static final String testPrefix = "test.";

    public static final MediaType JSON = MediaType.parse("application/json;charset=utf-8");
}
