package bitdev.cloudmemo.utils;

/**
 * Created by triasbrata on 12/24/15.
 */

    import android.content.Context;
    import android.content.SharedPreferences;
    import android.content.SharedPreferences.Editor;

    import java.util.HashMap;

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = Conf.cacheName;

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    public final SharedPreferences getPref(){
        return  pref;
    }
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    public void setSession(HashMap<String,String> data){
        if(data.size() > 0){
            for (String key : data.keySet()){
                editor.putString(key,data.get(key));
            }
            editor.commit();
        }

    }
    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);

        // commit changes
        editor.commit();
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }
}
