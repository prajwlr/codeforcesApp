package com.example.myapplication.Extraction;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.json.simple.JSONArray;

import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.util.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
public class getPersonalInfo extends Thread{
    String userName;
    Map<String, Integer> finalOutput;
    public getPersonalInfo(String username, Map<String, Integer> finalutput) {
        super();
        finalOutput=finalutput;
        userName = username;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void run() {

        String apiBaseUrl = "https://codeforces.com/api/user.status?handle=";
        int successfulSubmissions = 0;
        int unsuccessfulSubmissions = 0;
        URL obj;
        try {
            obj = new URL(apiBaseUrl+userName);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            finalOutput.put("status", 0);
            return;
        }
        HttpURLConnection con;
        try {
            con = (HttpURLConnection)obj.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
            finalOutput.put("status", 0);
            return;
        }
        try {
            if(con==null) {
                finalOutput.put("status", 0);
                return;
            }
            con.setRequestMethod("GET");
        } catch (ProtocolException e) {
            e.printStackTrace();
            finalOutput.put("status", 0);
            return;
        }
        try {
            if(con.getResponseCode()==HttpURLConnection.HTTP_BAD_REQUEST) {
                finalOutput.put("status",-1);
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
            finalOutput.put("status", 0);
            return;
        }
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
            finalOutput.put("status", 0);
            return;
        }
        String inputLine = null;
        StringBuilder response= new StringBuilder();
        while(true) {
            try {
                if ((inputLine = in.readLine()) == null) break;
            } catch (IOException e) {
                e.printStackTrace();
                finalOutput.put("status", 0);
                return;
            }
            response.append(inputLine);
        }
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String bodyData = response.toString();
        JSONParser parser = new JSONParser();
        JSONObject jsonData = null;
        try {
            jsonData = (JSONObject)parser.parse(bodyData);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(jsonData==null){            finalOutput.put("status",0);

            return;
        }
        String status = (String)jsonData.get("status");
        if(status==null){            finalOutput.put("status",0);

            return;
        }
        if(status.equals("OK")) {
            finalOutput.put("status",1);
            JSONArray results = (JSONArray)jsonData.get("result");
            for(int i=0;i<results.size();i++) {
                JSONObject result = (JSONObject) results.get(i);
                if (((String) result.get("verdict")).equals("OK")) {
                    JSONArray problemTags = (JSONArray) ((JSONObject) result.get("problem")).get("tags");
                    if (problemTags == null) {
                        continue;
                    }
                    for (int j = 0; j < problemTags.size(); j++) {
                        String tagValue = (String) problemTags.get(j);
                        if (finalOutput.containsKey(tagValue)) {
                            int temp = finalOutput.get(tagValue);
                            finalOutput.replace(tagValue, temp + 1);
                        } else {
                            finalOutput.put(tagValue, 1);
                        }
                    }
                    successfulSubmissions++;
                } else {
                    unsuccessfulSubmissions++;
                }
            }
            finalOutput.put("successfulSubmissions", successfulSubmissions);
            finalOutput.put("unsuccessfulSubmissions", unsuccessfulSubmissions);
        }
        else {
            finalOutput.put("status",-1);
        }
    }
}
