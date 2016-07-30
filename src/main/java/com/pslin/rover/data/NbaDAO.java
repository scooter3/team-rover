package com.pslin.rover.data;

import com.mongodb.MongoClient;
import com.mongodb.WriteConcern;
import com.pslin.rover.entity.NBATeam;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.dao.BasicDAO;

/**
 * @author plin
 */
public class NbaDAO extends BasicDAO<NBATeam, ObjectId>{

    public NbaDAO(MongoClient mongoClient, Morphia morphia) {
        super(mongoClient, morphia, "teams");
    }

    public NBATeam getTeamByName(String teamName) {
        return getDs().find(NBATeam.class).filter("name", teamName).get();
    }

    public boolean saveTeam(NBATeam nbaTeam) {
        Key key = save(nbaTeam, WriteConcern.ACKNOWLEDGED);
        return key != null;
    }
}
