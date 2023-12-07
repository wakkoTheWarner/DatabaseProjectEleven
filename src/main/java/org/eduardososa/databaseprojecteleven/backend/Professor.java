package org.eduardososa.databaseprojecteleven.backend;

public class Professor {
    String professorID;
    String professorName;
    String professorLastName;
    String professorEmail;
    String professorPhone;

    public Professor(String professorID, String professorName, String professorLastName, String professorEmail, String professorPhone) {
        this.professorID = professorID;
        this.professorName = professorName;
        this.professorLastName = professorLastName;
        this.professorEmail = professorEmail;
        this.professorPhone = professorPhone;
    }

    public String getProfessorID() {
        return professorID;
    }

    public String getProfessorName() {
        return professorName;
    }

    public String getProfessorLastName() {
        return professorLastName;
    }

    public String getProfessorEmail() {
        return professorEmail;
    }

    public String getProfessorPhone() {
        return professorPhone;
    }

    public void setProfessorID(String professorID) {
        this.professorID = professorID;
    }

    public void setProfessorName(String professorName) {
        this.professorName = professorName;
    }

    public void setProfessorLastName(String professorLastName) {
        this.professorLastName = professorLastName;
    }

    public void setProfessorEmail(String professorEmail) {
        this.professorEmail = professorEmail;
    }

    public void setProfessorPhone(String professorPhone) {
        this.professorPhone = professorPhone;
    }
}
