package com.example.myapplication.Extraction;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import java.util.HashMap;
import java.util.Map;
import com.example.myapplication.MainActivity;

public class GetOngoingContest extends Thread{

    JSONArray contestList;
    public GetOngoingContest(JSONArray temp){
        super();
        contestList = temp;
    }

    @Override
    public void run() {
        Document doc = null;
        try {
            doc = Jsoup.connect("https://codeforces.com/contests?complete=true").get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element body = doc.body();

        Element upperDiv = body.select("div[class='contestList'] > div[class='datatable']").first();
        Element lowerDiv = upperDiv.children().last();
        Elements ongoingContestList = lowerDiv.select("table").first().child(0).children();
        String[] val = {"contestId", "contestName", "divId", "startTime", "duration"};
        Map<Integer, String> contestDataMap = new HashMap<>();
        contestDataMap.put(-2,"divId");
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
                        if (contestDataId.equals("contestName")){
                            Integer divId;
                            String divSectionId = contestDataMap.get(-2);
                            if(contestData.contains("Div. 1")){
                                divId = 1;
                            }
                            else if(contestData.contains("Div. 2")){
                                divId = 2;
                            }
                            else if(contestData.contains("Div. 3")){
                                divId = 3;
                            }
                            else if(contestData.contains("Div. 4")){
                                divId = 4;
                            }
                            else{
                                divId = 0;
                            }
                            contestData = contestData.split("\\(")[0];
                            newContest.put(divSectionId, divId);
                        }
                        newContest.put(contestDataId, contestData);
                    }
                    index++;
                }
                contestList.add(newContest);
            }
        }
    }
}
