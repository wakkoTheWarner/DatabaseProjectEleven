package org.eduardososa.databaseprojecteleven.backend;

public class Program {

    int programId;
    String programName;

    public Program(int programID, String programName) {
        this.programId = programID;
        this.programName = programName;
    }

    public int getProgramId() {
        return programId;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramId(int programId) {
        this.programId = programId;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }
}
