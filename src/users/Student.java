package users;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import things.*;

public class Student extends User{
    private Map<String, Double> grades = new HashMap<>();
    private double gradesSum;
    private int absence;

    public Student(String name, String email){
        super(name, email);
        gradesSum = 0.0;
        absence = 0;
    }

    public Student(String name, String email, String password, int absence){
        super(name, email, password);
        this.gradesSum = grades.values().stream().mapToDouble(Number::doubleValue).sum();
        this.absence = absence;
    }

    public double getGradesSum(){
        return gradesSum;
    }

    public void addGradesSum(double points){
        gradesSum += points;
    }

    public int getAbsence(){
        return absence;
    }

    public void incrementAbsence(){
        absence++;
    }

    public String toString(){
        return String.format("\n%s \t\tAbsence: %d",super.toString(), absence);

    }

    public Map<String,Double> getGrades() {
        return grades;
    }
}