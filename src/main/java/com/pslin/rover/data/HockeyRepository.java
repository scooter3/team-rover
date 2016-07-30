package com.pslin.rover.data;

import com.mongodb.MongoClient;
import com.pslin.rover.entity.NHLTeam;
import org.mongodb.morphia.Morphia;

/**
 * @author plin
 */
public class HockeyRepository {

    private HockeyDAO hockeyDAO;

    public HockeyRepository() {
        MongoClient mongoClient = new MongoClient();
        Morphia morphia = new Morphia();
        morphia.map(NHLTeam.class);

        hockeyDAO = new HockeyDAO(mongoClient, morphia);
    }

    public NHLTeam getTeam(String teamName) {
        return hockeyDAO.getTeamByName(teamName);
    }

    public boolean saveTeam(NHLTeam team) {
        return hockeyDAO.saveTeam(team);
    }
}
