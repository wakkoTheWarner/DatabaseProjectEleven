package org.eduardososa.databaseprojecteleven.backend;

public class Competency {
    int competencyId;
    String competencyDescription;

    public Competency(int competencyId, String competencyDescription) {
        this.competencyId = competencyId;
        this.competencyDescription = competencyDescription;
    }

    public int getCompetencyId() {
        return competencyId;
    }

    public void setCompetencyId(int competencyId) {
        this.competencyId = competencyId;
    }

    public String getCompetencyDescription() {
        return competencyDescription;
    }

    public void setCompetencyDescription(String competencyDescription) {
        this.competencyDescription = competencyDescription;
    }
}
