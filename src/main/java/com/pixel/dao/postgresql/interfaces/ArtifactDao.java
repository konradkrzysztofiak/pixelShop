package com.pixel.dao.postgresql.interfaces;

import com.pixel.model.Artifact;

import java.sql.SQLException;
import java.util.List;

public interface ArtifactDao {

    List<Artifact> getListFull() throws SQLException;
    List<Artifact> getListBy(String valueName, String value) throws SQLException;
    boolean updateArtifact(Artifact artifact, int id);
    boolean insertArtifact(Artifact artifact);
    boolean deleteArtifact(Artifact artifact);

}
