package com.pixel.controller;

import com.pixel.dao.postgresql.implementations.*;
import com.pixel.model.*;

import java.sql.SQLException;
import java.util.*;

public class
StudentController {
    StudentDAOI studentDAOI ;
    LevelsDAOI levelsDAOI;
    QuestDAOI questDAOI;
    QuestCategoryDAOI questCategoryDAOI;
    ClassesDAOI classesDAOI;
    ArtifactDAOI artifactDAOI;
    SackInventoryDAOI sackInventoryDAOI;

    public StudentController(StudentDAOI studentDAOI, LevelsDAOI levelsDAOI, QuestDAOI questDAOI, ClassesDAOI classesDAOI, ArtifactDAOI artifactDAOI, SackInventoryDAOI sackInventoryDAOI, QuestCategoryDAOI questCategoryDAOI) {
        this.studentDAOI = studentDAOI;
        this.levelsDAOI = levelsDAOI;
        this.questDAOI = questDAOI;
        this.classesDAOI = classesDAOI;
        this.artifactDAOI = artifactDAOI;
        this.sackInventoryDAOI = sackInventoryDAOI;
    }

    public List<Student> getStudentList() {
        List<Student> studentList = new ArrayList<>();
        try {
            studentList = studentDAOI.getListFull();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return studentList;
    }

    public Student getStudent(int id) throws SQLException {
        return studentDAOI.getById(id);
    }

    public String getStudentName(Student student) {
        return student.getName();
    }

    public int getStudentExperience(Student student) {
        try {
            return studentDAOI.getExperience(student);
        } catch (SQLException e) {
            return 0;
        }
    }

    public String getUserLevel(Student student) {
        int experience = getStudentExperience(student);
        HashMap<String, Integer> levels = null;
        try {
            levels = levelsDAOI.getLevelMap();
        } catch (SQLException e) {
            return "";
        }
        int topRank = 0;
        String rankName = "";
        for (Map.Entry<String, Integer> entry : levels.entrySet()){
            int expNeeded = entry.getValue();
            if (expNeeded>topRank && experience>expNeeded){
                topRank = expNeeded;
                rankName = entry.getKey();
            }
        }
        return rankName;
    }

    public HashMap<Quest, Integer> getAllQuestCompleted(Student student){
        try {
            return studentDAOI.getQuestCompleted(student);
        } catch (SQLException e) {
            return new HashMap<>();
        }
    }

    public int getPercentageOfCompleted(Student student){
        int questCompleted = getUniqueQuestCompleted(student);
        int totalQuests = new QuestController(questDAOI,questCategoryDAOI).getNumberOfActiveQuest();
        return 100*questCompleted /totalQuests;
    }

    public String getMentorName(Student student){
        try {

            return studentDAOI.getMentorName(student);
        } catch (SQLException e) {
            return "";
        }
    }

    public String getClassName(Student student){
        try {
            return classesDAOI.getClassById(student.getCass_id()).getName();
        } catch (SQLException e) {
            return "";
        }
    }

    public boolean buyArtifactById(int user_id, int artifact_id){
        try {
            Student buyer = studentDAOI.getById(user_id);
            Artifact toBuy = artifactDAOI.getById(artifact_id);
            if (getStudentMoney(buyer) > toBuy.getPrice()) {
                sackInventoryDAOI.insertSackInventory(new SackInventory(buyer.getId(), toBuy.getId(), true, toBuy.getPrice()));
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getStudentMoney(Student student){
        return getStudentExperience(student) - getStudentSpending(student);
    }

    private int getStudentSpending(Student student){
        try {
            return studentDAOI.getSpendings(student);
        } catch (SQLException e) {
            return 0;
        }
    }

    private int getUniqueQuestCompleted(Student student){
        try {
            return studentDAOI.getQuestCompleted(student).keySet().size();
        } catch (SQLException e) {
            return 0;
        }
    }

}




