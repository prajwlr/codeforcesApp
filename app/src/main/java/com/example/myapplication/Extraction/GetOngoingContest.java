package com.example.myapplication.Extraction;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.util.HashMap;
import java.util.Map;

public class GetOngoingContest extends Thread {

    JSONObject contestListWrapper;
    JSONArray contestList;
    public GetOngoingContest(JSONObject jsonObject) {
        super();
        contestListWrapper = jsonObject;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void run() {
        Connection conn = Jsoup.connect("https://codeforces.com/contests?complete=true");
        Connection.Response res = null;
        int status_code = 0;
        try {
            res = conn.execute();
            status_code = res.statusCode();
        }
        catch (IOException e) {
            e.printStackTrace();
            return;
        }
        if(status_code == 200) {
            Document doc = null;
            try {
                doc = res.parse();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Element body = doc.body();

            Element upperDiv = body.select("div[class='contestList'] > div[class='datatable']").first();
            Element lowerDiv = upperDiv.children().last();
            Elements ongoingContestList = lowerDiv.select("table").first().child(0).children();
            String[] val = {"contestId", "contestName", "divId", "startTime", "duration"};
            Map<Integer, String> contestDataMap = new HashMap<>();
            contestDataMap.put(-2, "divId");
            contestDataMap.put(-1, "contestId");
            contestDataMap.put(0, "contestName");
            contestDataMap.put(2, "startTime");
            contestDataMap.put(3, "duration");
            for (Element contest : ongoingContestList) {
                if (contest.hasAttr("data-contestid")) {
                    JSONObject newContest = new JSONObject();
                    String contestId = contest.attr("data-contestid");
                    int index = -1;
                    if (contestDataMap.containsKey(index)) {
                        String contestDataValue = contestDataMap.get(index);
                        newContest.put(contestDataValue, contestId);
                    }
                    index++;
                    for (Element data_elem : contest.children()) {
                        if (contestDataMap.containsKey(index)) {
                            String contestData = data_elem.text();
                            String contestDataId = contestDataMap.get(index);
                            if (contestDataId.equals("startTime")) {
                                contestData = contestData.replace('/', '-');
                            }
                            if (contestDataId.equals("contestName")) {
                                Long divId;
                                String divSectionId = contestDataMap.get(-2);
                                if (contestData.contains("Div. 1")) {
                                    divId = (long) 1;
                                } else if (contestData.contains("Div. 2")) {
                                    divId = (long) 2;
                                } else if (contestData.contains("Div. 3")) {
                                    divId = (long) 3;
                                } else if (contestData.contains("Div. 4")) {
                                    divId = (long) 4;
                                } else {
                                    divId =(long) 0;
                                }
                                contestData = contestData.split("\\(")[0];
                                newContest.put(divSectionId, divId);
                            }
                            newContest.put(contestDataId, contestData);
                        }
                        index++;
                    }
                    JSONArray temp = (JSONArray)contestListWrapper.get("result");
                    if(temp!=null) {
                        temp.add(newContest);
                    }
                }
                contestListWrapper.put("status", "True");
            }
        }
    }
}