package org.eduardososa.databaseprojecteleven.backend;

public class ProgramCourse {
    public int selectionId;
    public String programNameSelection;
    public String courseIdSelection;

    public ProgramCourse(int selectionId, String programNameSelection, String courseIdSelection) {
        this.selectionId = selectionId;
        this.programNameSelection = programNameSelection;
        this.courseIdSelection = courseIdSelection;
    }

    public int getSelectionId() {
        return selectionId;
    }

    public void setSelectionId(int selectionId) {
        this.selectionId = selectionId;
    }

    public String getProgramNameSelection() {
        return programNameSelection;
    }

    public void setProgramNameSelection(String programNameSelection) {
        this.programNameSelection = programNameSelection;
    }

    public String getCourseIdSelection() {
        return courseIdSelection;
    }

    public void setCourseIdSelection(String courseIdSelection) {
        this.courseIdSelection = courseIdSelection;
    }
}
