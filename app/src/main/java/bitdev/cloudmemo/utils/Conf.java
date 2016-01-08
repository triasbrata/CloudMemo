package bitdev.cloudmemo.utils;

import java.util.HashMap;

/**
 * Created by triasbrata on 12/24/15.
 */
public class Conf {
    public static final String host = "http://bitdev-android.herokuapp.com/";
    public static final String apiVersion = "1";
    public static final String masterURL = host+"api/"+apiVersion+'/';
    public static final String cacheName = "com.bitdev.cloudmemo";
    public static final String api_key = "api_token";
    public static HashMap<String,String> masterData(String api){
        HashMap<String,String> data = new HashMap<>();
        data.put(api_key,api);
        return data;
    }
}
