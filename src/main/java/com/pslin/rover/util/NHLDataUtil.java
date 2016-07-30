package com.pslin.rover.util;

import com.pslin.rover.data.HockeyRepository;
import com.pslin.rover.entity.NHLTeam;
import com.pslin.rover.util.NamesUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Used to populate mongo with NHL teams
 *
 * @author plin
 */
public class NHLDataUtil {

    private static final String NHL_CONF_STANDINGS_PAGE = "http://www.nhl.com/ice/standings.htm?season=20152016&type=CON";

    public static void main(String[] args) throws IOException {

        HockeyRepository hockeyRepository = new HockeyRepository();

        Map<String, NHLTeam> nhlTeamsMap = new HashMap<>();

        Document document = Jsoup.connect(NHL_CONF_STANDINGS_PAGE).get();
        Element eastTable = document.select("#wideCol > div.contentBlock > table:nth-child(5)").first();
        Element westTable = document.select("#wideCol > div.contentBlock > table:nth-child(8)").first();

        populateTeamMap(eastTable, nhlTeamsMap, NHLTeam.Conference.EASTERN);
        populateTeamMap(westTable, nhlTeamsMap, NHLTeam.Conference.WESTERN);

        int count = 0;
        for(Map.Entry<String, NHLTeam> entry : nhlTeamsMap.entrySet()) {
            System.out.println(count++);
            System.out.println(entry.getValue().toString());
            System.out.println();
            hockeyRepository.saveTeam(entry.getValue());
        }
    }

    private static void populateTeamMap(Element table, Map<String, NHLTeam> map, NHLTeam.Conference conference) {
        NamesUtil namesUtil = new NamesUtil();

        for(Object name : namesUtil.getNhlNamesProp().keySet()) {
            String teamName = (String) name;
            Element tbody = table.getElementsByTag("tbody").first();
            for(Element row : tbody.getElementsByTag("tr")) {
                Elements tds = row.getElementsByTag("td");
                String rowTeam = tds.get(1).text();

                if(!teamName.toLowerCase().contains(rowTeam.toLowerCase().replace("\u00a0", "").replace("\u00E9", "e").replace(".", ""))) {
                    continue;
                }

                NHLTeam nhlTeam = new NHLTeam();
                nhlTeam.setName(namesUtil.getNHLTeamName(teamName));
                nhlTeam.setCity(rowTeam.replace("\u00a0", ""));
                nhlTeam.setConference(conference);

                if(map.get(nhlTeam.getName()) != null) {
                    System.out.println("Team already exists: " + nhlTeam.getName());
                } else {
                    map.put(nhlTeam.getName(), nhlTeam);
                }
            }
        }
    }
}
