package com.pslin.rover.data;

import com.mongodb.MongoClient;
import com.pslin.rover.entity.NBATeam;
import org.mongodb.morphia.Morphia;

/**
 * @author plin
 */
public class NbaRepository {

    private NbaDAO nbaDAO;

    public NbaRepository() {
        MongoClient mongoClient = new MongoClient();
        Morphia morphia = new Morphia();
        morphia.map(NBATeam.class);
        nbaDAO = new NbaDAO(mongoClient, morphia);
    }

    public NBATeam getTeam(String teamName) {
        return nbaDAO.getTeamByName(teamName);
    }

    public boolean saveteam(NBATeam team) {
        return nbaDAO.saveTeam(team);
    }
}
