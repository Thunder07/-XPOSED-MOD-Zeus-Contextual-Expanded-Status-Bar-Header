package net.madnation.zeus.contextual.xposed;

import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SettingsManager {
    static String PREF_ENABLE_CUSTOM_IMAGES = "EnableCustomImages";


    private String path = Environment.getExternalStorageDirectory().getPath() + "/ZCESH_BG/";
    private String _configFile = "config.json";
    private HashMap<String, Boolean> boolPref = new HashMap<>();
    private boolean loadSettings;

    public SettingsManager() {
    }

    public SettingsManager(boolean loadsettings) {
        if (loadsettings) loadSettings();
    }

    private void saveSettings() {
        JSONObject jsonArr = new JSONObject();
        JSONObject jsonData = new JSONObject();
        try {
            for (Map.Entry<String, Boolean> pref : boolPref.entrySet()) {
                jsonData.put(pref.getKey(), pref.getValue());
            }
            jsonArr.put("bool", jsonData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        writeToFile(jsonArr.toString());
    }

    private boolean loadSettings() {
        String file = readFromFile();
        try {
            JSONObject jsonArr = new JSONObject(file);
            JSONObject jsonData = jsonArr.getJSONObject("bool");
            Iterator it = jsonData.keys();
            while (it.hasNext()) {
                String prefname = it.next().toString();
                boolPref.put(prefname, jsonData.getBoolean(prefname));
            }
            loadSettings = true;
        } catch (JSONException e) {
            e.printStackTrace();
            loadSettings = false;
        }
        return loadSettings;
    }

    private void writeToFile(String data) {
        File dir = new File(path);
        if (!dir.exists())
            dir.mkdirs();
        try {
            FileOutputStream fos = new FileOutputStream(new File(path, _configFile));
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fos);
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile() {

        String ret = "";

        try {
            FileInputStream fis = new FileInputStream(new File(path, _configFile));
            Log.i("Zeus", path);

            if (fis != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(fis);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                fis.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    void setBooleanPref(String name, boolean value) {
        boolPref.put(name, value);
        saveSettings();
    }

    boolean getBooleanPref(String name) {
        return boolPref.get(name) == null ? false : boolPref.get(name);
    }

    boolean reload() {
        boolPref = new HashMap<>();
        return loadSettings();
    }

    boolean isLoadSettingError() {
        return !loadSettings;
    }
}
