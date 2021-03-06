package database;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import java.util.Arrays;

import java.io.IOException;
import users.*;

public class DataReglamentator{
    private DataManager usersDataProvider;
    private DataManager usersAssignmentDataProvider;
    private List<Supervisor> supervisors = new ArrayList<>();
    private List<Student> students =  new ArrayList<>();
    private List<Employee> employees = new ArrayList<>();
    private List <User> allUsers = new ArrayList<>();

    public DataReglamentator(String filePath, String filePath2) throws IOException{
        usersDataProvider = new DataManager(filePath);
        usersAssignmentDataProvider = new DataManager(filePath2);
        createListsOfAllTypeOfUser();
        updateAllUssersList();
    }

    private void updateAllUssersList(){
        allUsers.clear();
        allUsers.addAll(supervisors);
        allUsers.addAll(students);
        allUsers.addAll(employees);
    }

    private void createListsOfAllTypeOfUser(){
        for(String[] line: usersDataProvider){
            String email = line[0];
            String name = line[1];
            String password = line[2];
            String status = line[3];
            String absence = line[4];
            String salary = line[5];
            if(status.equals("Supervisor"))
                supervisors.add(new Supervisor(name, email, password));
            else if(status.equals("Student"))
                students.add(new Student(name, email, password, Integer.parseInt(absence)));
            else if (status.equals("Mentor"))
                employees.add(new Mentor(name, email, password, Double.parseDouble(salary)));
            else employees.add(new Employee(name, email, password, Double.parseDouble(salary)));
        }
        createGrades();
    }

    private void createGrades(){
        for(String[]line: usersAssignmentDataProvider){
            //* for array of String lines takes index 0 of each line as String paramether by wich
            /*takes a Student element of List<Student>  and using createAssigmentsMap(String[]) sets
            /*its pool;
            */ 
            String studentEmail = line[0];
            Map<String, Double> studentGrades = createMapFromStringArray(line);
            getStudentByEmail(studentEmail).setGrades(studentGrades);
        }
    }

    private static Map<String, Double> createMapFromStringArray(String[]line){
        //*for String line as: [email,assignment name,points,assignments name,points...]
        /* creates a Map as following: < String assignmantName, Double points >
        */

        Map<String, Double> studentGrades = new HashMap<>();
        for(int i=1; i<line.length-1; i+=2){
            String assignment = line[i];
            double points = Double.parseDouble(line[i+1]);
            studentGrades.put(assignment,points);
        }
        return studentGrades;

    }
    
    public Collection<User> getUsersList(){
        return allUsers;
    }
    
    public List<Student> getStudentsList(){
        return students;
    }

    public List<Employee> getEmployeesList(){
        return employees;
    }

    public List<Employee> extractMentorsList(){
        List<Employee> mentorsPicked = new ArrayList<>();
        for(Employee employee: employees){
            if(employee.getStatus().equals("Mentor"))
                mentorsPicked.add(employee);
        }
        return mentorsPicked;
    }

    public User getUserByName(String name)throws NoSuchElementException{
        for( User user: allUsers){
            if (user.getName().equals(name)) return user;
        }
        throw new NoSuchElementException();
    }

    public User getUserByEmail(String email) throws NoSuchElementException{
        for (User user: allUsers){
            if (user.getEmail().equals(email)) return user;
        }
        throw new NoSuchElementException();
    }

    public Student getStudentByName(String name)throws NoSuchElementException{
        for (Student student: students){
            if (student.getName().equals(name)) return student;
        }
        throw new NoSuchElementException();
    }

    public Student getStudentByEmail(String email) throws NoSuchElementException{
        for (Student student: students){
            if (student.getEmail().equals(email)) return student;
        }
        throw new NoSuchElementException();
    }
    

    public Employee getEmployeeByName(String name){
        for (Employee employee: employees){
            if (employee.getName().equals(name)) return employee;
        }
        throw new NoSuchElementException();
    }

    public Employee getEmployeeByEmail(String email){
        for (Employee employee: employees){
            if (employee.getEmail().equals(email)) return employee;
        }
        throw new NoSuchElementException();
    }

    public void addStudent(String name, String email){
        students.add(new Student(name,email));
        updateAllUssersList();
    }

    public boolean removeStudentByEmail(String email ){
        if (students.removeIf(s -> s.getEmail().equals(email))) {
            updateAllUssersList();
            return true;
        }
        else
            return false;
    }

    public void removeStudent(Student student){
        students.remove(student);
        updateAllUssersList();
    }

    
    public void addMentor(String name, String email){
        employees.add(new Mentor(name, email));
        updateAllUssersList();
    }

    public void addSimpleEmployee(String name, String email){
        employees.add(new Employee(name, email));
        updateAllUssersList();
    }

    public boolean removeEmployeeByEmail(String email){
        if (employees.removeIf(emp -> emp.getEmail().equals(email))) {
            updateAllUssersList();
            return true;
        }
        else
            return false;
    }
    
    public void removeEmployee(Employee employee){
        employees.remove(employee);
        updateAllUssersList();
    }
   
    public void updateDataManager(){
        List <String[]> lines = transformUsersListToExportLines();
        usersDataProvider.setUsersLines(lines);
        List<String[]> assignmentsLines = transformAssignmentsMapToExportLines();
        usersAssignmentDataProvider.setUsersLines(assignmentsLines);
    }

    private List<String[]> transformUsersListToExportLines(){
        updateAllUssersList();
        List<String[]> usersLines = new ArrayList<>();
        String salary;
        String absence;
        for (User user: allUsers){
            if(user.getStatus().equals("Student")){
                Student student = (Student)user;
                salary = "null";
                absence = String.valueOf(student.getAbsence());
            }
            else if (user.getStatus().equals("Supervisor")){
                absence = "null";
                salary = "null";
            }
            else{
            Employee employee = (Employee)user;
                salary = String.valueOf(employee.getSalary());
                absence = "null";
            }
            String[] line = {user.getEmail(),user.getName(),user.getPassword(),user.getStatus(),absence,salary};
            usersLines.add(line);
        }
        return usersLines;        
    }

    private List<String[]> transformAssignmentsMapToExportLines(){
        List<String[]> assignmentsLines = new ArrayList<>();
        for (Student student : students) {
            String email = student.getEmail();
            Map<String, Double> assignmentMap = student.getGrades();
            List <String> listedAssignment = mapToStringList(assignmentMap);
            listedAssignment.add (0,email);
            assignmentsLines.add(listedAssignment.toArray(new String[listedAssignment.size()]));
        }
        return assignmentsLines;
    }

    private static List <String> mapToStringList(Map<String, Double> assignmentMap){
        List<String> list = new ArrayList<>();
        assignmentMap.forEach((k,v)-> {list.add(k); list.add(String.valueOf(v));});
        return list;   
    }
}

