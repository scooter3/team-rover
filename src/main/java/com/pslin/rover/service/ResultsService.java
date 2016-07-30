package com.pslin.rover.service;

import com.pslin.rover.dto.CompletedGame;

import java.util.List;

/**
 * @author plin
 */
public interface ResultsService {

    public boolean processAllResults();

    public List<CompletedGame> getGamesResults(String teamName);
}
