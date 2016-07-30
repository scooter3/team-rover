package com.pslin.rover.service;

import com.pslin.rover.dto.SimpleGame;

import java.util.List;

/**
 * @author plin
 */
public interface ScheduleService {

    public List<SimpleGame> getSchedule(String teamName);
}
