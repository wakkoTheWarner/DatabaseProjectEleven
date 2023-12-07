package org.eduardososa.databaseprojecteleven.backend;

public class Course {
    String courseId;
    int competencyId;
    String professorId, courseName, objectiveDescription,evaluationInstrument,compMetric;

    public Course(String courseId, int competencyId, String professorId, String courseName, String objectiveDescription, String evaluationInstrument, String compMetric) {
        this.courseId = courseId;
        this.competencyId = competencyId;
        this.professorId = professorId;
        this.courseName = courseName;
        this.objectiveDescription = objectiveDescription;
        this.evaluationInstrument = evaluationInstrument;
        this.compMetric = compMetric;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public int getCompetencyId() {
        return competencyId;
    }

    public void setCompetencyId(int competencyId) {
        this.competencyId = competencyId;
    }

    public String getProfessorId() {
        return professorId;
    }

    public void setProfessorId(String professorId) {
        this.professorId = professorId;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getObjectiveDescription() {
        return objectiveDescription;
    }

    public void setObjectiveDescription(String objectiveDescription) {
        this.objectiveDescription = objectiveDescription;
    }

    public String getEvaluationInstrument() {
        return evaluationInstrument;
    }

    public void setEvaluationInstrument(String evaluationInstrument) {
        this.evaluationInstrument = evaluationInstrument;
    }

    public String getCompMetric() {
        return compMetric;
    }

    public void setCompMetric(String compMetric) {
        this.compMetric = compMetric;
    }
}
