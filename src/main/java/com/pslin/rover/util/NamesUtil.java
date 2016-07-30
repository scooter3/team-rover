package com.pslin.rover.util;

import java.io.IOException;
import java.util.Properties;

/**
 * @author plin
 */
public class NamesUtil {

    private Properties nhlAbbreviationProp;
    private Properties nhlNamesProp;
    private Properties nhlSlugProp;

    private Properties nbaAbbreviationProp;
    private Properties nbaNamesProp;
    private Properties nbaSlugProp;

    public NamesUtil() {
        nhlAbbreviationProp = new Properties();
        nhlNamesProp = new Properties();
        nhlSlugProp = new Properties();
        nbaAbbreviationProp = new Properties();
        nbaNamesProp = new Properties();
        nbaSlugProp = new Properties();

        try {
            nhlAbbreviationProp.load(getClass().getResourceAsStream("/nhl-abbr.properties"));
            nhlNamesProp.load(getClass().getResourceAsStream("/nhl-team-names.properties"));
            nhlSlugProp.load(getClass().getResourceAsStream("/nhl-slug-names.properties"));

            nbaAbbreviationProp.load(getClass().getResourceAsStream("/nba-abbr.properties"));
            nbaNamesProp.load(getClass().getResourceAsStream("/nba-team-names.properties"));
            nbaSlugProp.load(getClass().getResourceAsStream("/nba-slug-names.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String toNiceName(String teamName) {
        return teamName.toLowerCase().replace(".", "").replace(" ", "");
    }

    /**
     * Returns the two or three-letter team abbreviation.
     *
     * @param teamName
     * @return abbreviation
     */
    public String getNHLTeamAbbreviation(String teamName) {
        return nhlAbbreviationProp.getProperty(teamName);
    }

    /**
     * Returns the team name given the team's full name as a string with no space
     * Example: LosAngelesKings
     * Returns: Kings
     *
     * @param teamFullName
     * @return team name
     */
    public String getNHLTeamName(String teamFullName) {
        return nhlNamesProp.getProperty(teamFullName);
    }

    /**
     * Get the slug name
     *
     * @param teamName
     * @return
     */
    public String getNHLSlugName(String teamName) {
        return nhlSlugProp.getProperty(teamName);
    }

    /**
     * Returns the two or three letter team abbreviation.
     *
     * @param teamName
     * @return abbreviation
     */
    public String getNBATeamAbbreviation(String teamName) {
        return nbaAbbreviationProp.getProperty(teamName);
    }

    /**
     *
     * @param teamFullName
     * @return
     */
    public String getNBATeamName(String teamFullName) {
        return nbaNamesProp.getProperty(teamFullName);
    }

    /**
     * Get the slug name
     *
     * @param teamName
     * @return
     */
    public String getNBASlugName(String teamName) {
        return nbaSlugProp.getProperty(teamName);
    }

    public Properties getNhlAbbreviationProp() {
        return nhlAbbreviationProp;
    }

    public Properties getNhlNamesProp() {
        return nhlNamesProp;
    }

    public Properties getNhlSlugProp() {
        return nhlSlugProp;
    }

    public void setNhlSlugProp(Properties nhlSlugProp) {
        this.nhlSlugProp = nhlSlugProp;
    }

    public Properties getNbaAbbreviationProp() {
        return nbaAbbreviationProp;
    }

    public Properties getNbaNamesProp() {
        return nbaNamesProp;
    }

    public Properties getNbaSlugProp() {
        return nbaSlugProp;
    }
}
