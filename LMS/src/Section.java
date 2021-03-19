
import java.util.LinkedList;
import java.util.List;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Section extends Subject {
    private String s_name;
    private int semester;
    private int max_students;
    private int no_of_students;
    private int s_id;
    private Teacher teacher;
    private Course course;
    private boolean offered;
    List<Evaluation> evaluations;
    List<Registeration> registerations;

    public Section() {
        s_name=null;
        semester=0;
        max_students=0;
        no_of_students=0;
        s_id=-1;
        teacher=null;
        course=null;
        offered=false;
        evaluations=new LinkedList<>();
        registerations=new LinkedList<>();
    }
    
    public String getS_name() {
        return s_name;
    }

    public void setS_name(String s_name) {
        this.s_name = s_name;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public int getMax_students() {
        return max_students;
    }

    public void setMax_students(int max_students) {
        this.max_students = max_students;
    }

    public int getNo_of_students() {
        return no_of_students;
    }

    public void incrNo_of_students() {
        this.no_of_students++;
    }
    
    public void decrNo_of_students(){
        this.no_of_students--;
        notifyObserver();
    }

    public int getS_id() {
        return s_id;
    }

    public void setS_id(int s_id) {
        this.s_id = s_id;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public boolean isOffered() {
        return offered;
    }

    public void setOffered(boolean offered) {
        this.offered = offered;
    }
    
    public Evaluation get_eval(int ev_id)
    {
        for(int i = 0; i < evaluations.size();i++)
        {
            if(evaluations.get(i).getE_id() == ev_id)
            {
                return evaluations.get(i);
            }
        }
        
        return null;
    }
    
    @Override
    public String toString() {
        return s_name;
    }
    
    public void printEvaluations(){
        for (Evaluation e:evaluations){
            System.out.println(e);
        }
    }
    
    public void printRegisterations(){
        for(Registeration r:registerations){
            System.out.println(r);
        }
    }
    
    public boolean seatsAvailable(){
        return max_students-no_of_students != 0;
    }
    
    @Override
    public void notifyObserver(){
        if(stds.isEmpty())
        {
            return;
        }
        Observer ob=stds.get(0);
        stds.remove(ob);
        
        String sql="delete from waiting_list where roll_no='"+ob.getRoll_no()+"' and sec_id="+this.s_id;
        Sql dbsql=new Sql();
        dbsql.execUpdate(sql);
        
        ob.update(s_name);
    }
    
    public void addEvaluation(String name, String type, float weightage, int total_marks) throws SQLException{
        Evaluation e=new Evaluation(name,type,weightage,total_marks);
        String sql="insert into evaluation (s_id,[name],[type],weightage,total_marks) values("+s_id+", '"+name+"', '"+type+"', "+weightage+
                ", "+total_marks+")";
        Sql dbsql=new Sql();
        dbsql.execUpdate(sql);
        String sql1="SELECT e_id FROM evaluation WHERE e_id = @@Identity;";
        ResultSet rs=dbsql.execQuery(sql1);
        rs.next();
        e.setE_id(rs.getInt("e_id"));
        evaluations.add(e);
        
        for(Registeration r:registerations){
            r.addMarks(e);
        }
    }
    
    public void addToSubscriberList(Student s){
        stds.add(s);
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");  
        LocalDateTime now = LocalDateTime.now();  
        String sql="insert into waiting_list values('"+s.getRoll_no()+"', "+s_id+", '"+dtf.format(now)+"')";
        Sql dbsql=new Sql();
        dbsql.execUpdate(sql);
    }
    
    public boolean alreadyRegistered(Student s){
        for(Registeration r:registerations){
            if(r.getStudent()==s){
                return true;
            }
        }
        return false;
    }
    
    public void addAttendance(String date, char presence, float duration){
        for(Registeration r: registerations){
            r.addAttendance(new Attendance(date,presence,duration));
        }
    }
    
}