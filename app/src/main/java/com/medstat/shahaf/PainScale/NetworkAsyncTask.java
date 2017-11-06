package com.medstat.shahaf.PainScale;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by Shahaf on 3/23/2016.
 */
public class NetworkAsyncTask extends AsyncTask<String, Void, String> {

    public AsyncResponse delegate = null;
    private String task;
    OkHttpClient client = new OkHttpClient();
    RequestBody body;
    Response response;

    @Override
    protected String doInBackground(String... url) {
        String serverAdress = Defs.serverAddressProduction;
        try {
            OkHttpClient client2 = getUnsafeOkHttpClient();
            Request request;
            String result;
            String patientID;
            JSONObject oneData;
            task = url[0];
            Log.e("address", serverAdress + url[1]);

            switch (task) {

                case Defs.LOGIN:
                    oneData = new JSONObject();
                    patientID = url[2];
                    if (patientID.substring(0,Defs.testPrefix.length()).equals(Defs.testPrefix)){
                        serverAdress = Defs.serverAddressTest;
                        patientID = patientID.substring(Defs.testPrefix.length());
                    }
                    else{
                        serverAdress = Defs.serverAddressProduction;
                    }
                    oneData.put("PatientID", patientID);
                    String tokenId = registerToGSM();
                    oneData.put("TokenID", tokenId);
                    Log.e("tokenId", tokenId);
                    oneData.put("ClientDateTime", url[3]);
                    body = RequestBody.create(Defs.JSON, oneData.toString());
                    Log.e("data",oneData.toString());
                    request = new Request.Builder()
                            .url(serverAdress+url[1])
                            .post(body)
                            .build();
                    response = client2.newCall(request).execute();
                    result = response.body().string();
                    Log.e("response",result);

                    return result;
                case Defs.SEND:
                    oneData = new JSONObject();
                    patientID = url[2];
                    if (patientID.substring(0,Defs.testPrefix.length()).equals(Defs.testPrefix)){
                        serverAdress = Defs.serverAddressTest;
                        patientID = patientID.substring(Defs.testPrefix.length());
                    }
                    else{
                        serverAdress = Defs.serverAddressProduction;
                    }
                    oneData.put("PatientID", patientID);
                    oneData.put("ClientDateTime", url[3]);
                    JSONObject twoData = new JSONObject();
                    twoData.put("q1", url[4]);
                    twoData.put("q2", url[5]);
                    twoData.put("q3", url[6]);
                    twoData.put("q4", url[7]);
                    twoData.put("q5", url[8]);

                    ArrayList<String> list = new ArrayList<String>();
                    for (int i = 9; i<url.length;i++){
                        list.add(url[i]);
                    }
                    twoData.put("m6",new JSONArray(list));
                    oneData.put("data", twoData);

                    body = RequestBody.create(Defs.JSON, oneData.toString());
                    request = new Request.Builder()
                            .url(serverAdress+url[1])
                            .post(body)
                            .build();
                    response = client2.newCall(request).execute();
                    result = response.body().string();
                    return result;
                case Defs.CHECK:
                    oneData = new JSONObject();
                    patientID = url[2];
                    if (patientID.substring(0,Defs.testPrefix.length()).equals(Defs.testPrefix)){
                        serverAdress = Defs.serverAddressTest;
                        patientID = patientID.substring(Defs.testPrefix.length());
                    }
                    else{
                        serverAdress = Defs.serverAddressProduction;
                    }
                    oneData.put("PatientID", patientID);
                    oneData.put("ClientDateTime",url[3]);
                    body = RequestBody.create(Defs.JSON, oneData.toString());
                    request = new Request.Builder()
                            .url(serverAdress+url[1])
                            .post(body)
                            .build();
                    response = client2.newCall(request).execute();
                    result = response.body().string();
                    return result;
            }

        } catch (Exception e) {

            String message = e.getMessage();
            Log.e("error",message);
            return message;
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
       // Log.e("result", result);
        delegate.processFinish(task,result);
    }

    private String registerToGSM(){
        Context context = (Context) delegate;
        InstanceID instanceID = InstanceID.getInstance(context);
        try {
            String token = instanceID.getToken(context.getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                Log.e("token",token);
            return token;
        } catch (IOException e) {
            Log.e("error token",e.getMessage());
            return null;
        }
    }

    private static OkHttpClient getUnsafeOkHttpClient() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.sslSocketFactory(sslSocketFactory);
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            OkHttpClient okHttpClient = builder.build();
            return okHttpClient;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
