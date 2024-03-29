package com.pixel.controller;

import com.pixel.dao.postgresql.implementations.*;
import com.pixel.model.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class
CreepController {
    MentorDAOI mentorDAOI;
    ClassesDAOI classesDAOI;
    LevelsDAOI levelDAOI;

    public CreepController(MentorDAOI mentorDAOI, ClassesDAOI classesDAOI, LevelsDAOI levelDAOI) {
        this.classesDAOI = classesDAOI;
        this.mentorDAOI = mentorDAOI;
        this.levelDAOI = levelDAOI;
    }

    public void createMentor(String name, String password, int cass_id){
        try {
            mentorDAOI.save(new Mentor(name, password, "mentor",  cass_id));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Mentor viewMentorByID(int id){
        Mentor mentor = new Mentor();
        try {
            mentor = mentorDAOI.getById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return mentor;
    }

    public void updateMentor(int id, String name, int cass_id){
        try {
            mentorDAOI.update(new Mentor(id, name, "mentor", cass_id));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createClass(String name){
        try {
            classesDAOI.createClass(name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addLevel(String levelName, int expRequired){
        try {
            levelDAOI.addLevel(new Level(levelName, expRequired));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeLevelByID(int id){
        try {
            levelDAOI.deleteLevelById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public int getLevelIdByName(String name){
        try {
            return levelDAOI.getLevelIdByName(name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Map<String,Integer> getAllLevel(){
        Map<String,Integer> levelMap = new HashMap<>();

        try {
            levelMap = levelDAOI.getLevelMap();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return levelMap;
    }


}




