package com.pslin.rover.data;

import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import com.pslin.rover.entity.NHLTeam;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.dao.BasicDAO;

/**
 * @author plin
 */
public class HockeyDAO extends BasicDAO<NHLTeam, ObjectId> {

    public HockeyDAO(MongoClient mongoClient, Morphia morphia) {
        super(mongoClient, morphia, "teams");
    }

    public NHLTeam getTeamByName(String teamName) {
        return getDs().find(NHLTeam.class).filter("name", teamName).get();
    }

    public boolean saveTeam(NHLTeam nhlTeam) {
        Key key = save(nhlTeam, WriteConcern.ACKNOWLEDGED);
        return key != null;
    }
}
