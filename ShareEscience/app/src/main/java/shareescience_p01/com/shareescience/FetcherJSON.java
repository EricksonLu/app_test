package shareescience_p01.com.shareescience;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by lushan on 2015/8/31.
 */
public class FetcherJSON {
    private static String API_KEY;
    private static String VALUE;
    private static String Localhost= "http://localhost/ShareEscience_p01/source/api.php";
    private static JSONObject jsonObject;
    public FetcherJSON(String value,String api_key){

          String API_KEY =api_key;
          String VALUE = value;

    }


    public byte[] getUrlBytes() throws IOException{

        URL url = new URL(Localhost);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setDoOutput(true);
        connection.setDoInput(true);
        connection.setUseCaches(false);
        connection.setInstanceFollowRedirects(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Accpet", "application/json");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.connect();


        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());

            outputStreamWriter.append(jsonObject.toString());
            outputStreamWriter.flush();
            outputStreamWriter.close();


            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK){return  null;}

            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0){
                out.write(buffer, 0 ,bytesRead);
            }
            out.close();
            return out.toByteArray();

        }finally {
            connection.disconnect();
        }


    }

    String getresult(String urlSpec) throws IOException {
        return new String(getUrlBytes());
    }

//    public JSONObject JSON_2_string() {
//
//
//        return ;
//    }

//get方式的检索式，服务器端没有采用，而是采用POST-JSON
    public String  URL_4_search (String query) throws JSONException{
        String url = Uri.parse(Localhost).buildUpon()
                .appendQueryParameter("api",API_KEY)
                .appendQueryParameter("value",query).build().toString();
//        JSONObject query_json = new JSONObject();
//        String api = API_KEY;
//        String value = query;
        jsonObject.put("api",API_KEY);
        jsonObject.put("value",query);

        return url;
    }




}
