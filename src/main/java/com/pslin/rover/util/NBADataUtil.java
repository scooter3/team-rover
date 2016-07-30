package com.pslin.rover.util;

import com.pslin.rover.data.NbaRepository;
import com.pslin.rover.entity.NBATeam;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to populate mongo with NBA teams
 *
 * @author plin
 */
public class NBADataUtil {

    public static void main(String[] args) throws IOException {
        NbaRepository nbaRepository = new NbaRepository();
        Map<String, NBATeam> nbaTeamMap = new HashMap<>();

        Document document = Jsoup.connect("http://espn.go.com/nba/standings").get();
        Element eastTable = document.select("#main-container > div > section > div.tab-content > div > div:nth-child(2) > table").first();
        Element westTable = document.select("#main-container > div > section > div.tab-content > div > div:nth-child(3) > table").first();

        populateTeamMap(eastTable, nbaTeamMap, NBATeam.Conference.EASTERN);
        populateTeamMap(westTable, nbaTeamMap, NBATeam.Conference.WESTERN);

        int count = 0;
        for(Map.Entry<String, NBATeam> entry : nbaTeamMap.entrySet()) {
            System.out.println(count++);
            System.out.println(entry.getValue().toString());
            System.out.println();
            nbaRepository.saveteam(entry.getValue());
        }
    }

    private static void populateTeamMap(Element table, Map<String, NBATeam> map, NBATeam.Conference conference) {
        NamesUtil namesUtil = new NamesUtil();

        for(Object name : namesUtil.getNbaNamesProp().keySet()) {
            String teamName = (String) name;
            Element tbody = table.getElementsByTag("tbody").first();
            for(Element row : tbody.getElementsByTag("tr")) {
                Elements tds = row.getElementsByTag("td");
                String rowTeam = tds.get(0).getElementsByAttributeValue("name", "&lpos=nba:standings:team").get(1).getElementsByTag("span").first().getElementsByAttributeValue("class", "team-names").first().text();
                if(!teamName.toLowerCase().contains(rowTeam.toLowerCase().replace(" ", ""))) {
                    continue;
                }

                NBATeam nbaTeam = new NBATeam();
                nbaTeam.setName(namesUtil.getNBATeamName(teamName));

                String city;
                String[] tokens = rowTeam.split(" ");
                switch (rowTeam) {
                    case "New York Knicks":
                    case "Golden State Warriors":
                    case "Oklahoma City Thunder":
                    case "Los Angeles Lakers":
                    case "Los Angeles Clippers":
                    case "New Orleans Pelicans":
                    case "San Antonio Spurs":
                        city = tokens[0] + " " + tokens[1];
                        break;

                    default:
                        city = tokens[0];
                        break;
                }

                nbaTeam.setCity(city);
                nbaTeam.setConference(conference);

                if(map.get(nbaTeam.getName()) != null) {
                    System.out.println("Team already exists: " + nbaTeam.getName());
                } else {
                    map.put(nbaTeam.getName(), nbaTeam);
                }
            }
        }
    }
}
