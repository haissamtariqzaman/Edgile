import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Lms {
    
    private Hashtable<String,Student> students;
    private Hashtable<String,Teacher> teachers;
    private ArrayList<Course> courses;
    private java.util.Date regStart;
    private java.util.Date regEnd;
    private java.util.Date withdrawDeadline;
    
    public static void main(String args[]) throws SQLException{
        Lms lms=new Lms();
        lms.start();
    }
    
    public void start() throws SQLException{
        SplashScreen splash=SplashScreen.instance;
        splash.setVisible(true);
        splash.setStatus("Reading students...",25);
        students=readStudents();
        splash.setStatus("Reading teachers...",50);
        teachers=readTeachers();
        splash.setStatus("Reading courses...",75);
        courses=readCourses();
        splash.setStatus("Initializing...",100);
        readDates();
        
        Login login=new Login(this);
        splash.dispose();
        login.setVisible(true);
    }
    
    public void start2(JFrame jf){
        Login login=new Login(this);
        login.setVisible(true);
        jf.dispose();
    }
    
    public void student(Login login){
        Student s=students.get(login.username.getText().toString());
        if (s!=null && login.password.getText().toString().equals(s.getPassword())){
            Student_dashboard sDashBoard=new Student_dashboard(s,this);
            login.dispose();
            sDashBoard.setVisible(true);
            s.semesters.sort(Semester.getComparator());
        }
        else{
            JOptionPane.showMessageDialog(login, "Invalid Username or Password!");
        }
    }
    
    public List<Course> getRegData(Student s){
        List<Course> crs=new ArrayList<>();
        for(Course c:courses){
            if(c.getOffered() && c.sections.get(0).getSemester()<=s.getC_semester()){
                crs.add(c);
            }
        }
        return crs;
    }
    
    public void teacher(Login login){
        Teacher t=teachers.get(login.username.getText().toString());
        if (t!=null && login.password.getText().toString().equals(t.getPassword())){
            Teacher_dashboard tDashBoard=new Teacher_dashboard(t,this);
            login.dispose();
            tDashBoard.setVisible(true);
        }
        else{
            JOptionPane.showMessageDialog(login, "Invalid Username or Password!");
        }
    }
    
    public Hashtable<String,Teacher> readTeachers() throws SQLException{
        String sql="select* from teacher";
        Sql dbsql=new Sql();
        ResultSet rs = dbsql.execQuery(sql);
                    
        Hashtable<String,Teacher> tchrs=new Hashtable<>();

        while(rs.next()){
            Teacher t=new Teacher();
            t.setEmail(rs.getString("email"));
            t.setPassword(rs.getString("password"));
            t.setF_name(rs.getString("f_name"));
            t.setL_name(rs.getString("l_name"));
            t.setDob(rs.getString("dob"));
            t.setDepartment(rs.getString("department"));
            t.setRank(rs.getString("rank"));
            tchrs.put(t.getEmail(), t);
        }
        return tchrs;
    }
    
    public Hashtable<String,Student> readStudents() throws SQLException{
        String sql="select* from student";
        Sql dbsql=new Sql();
        ResultSet rs = dbsql.execQuery(sql);
                 
        Hashtable<String,Student> stds=new Hashtable<>();

        while(rs.next()){
            Student s=new Student();
            s.setRoll_no(rs.getString("roll_no"));
            s.setEmail(rs.getString("email"));
            s.setPassword(rs.getString("password"));
            s.setF_name(rs.getString("f_name"));
            s.setL_name(rs.getString("l_name"));
            s.setDob(rs.getString("dob"));
            s.setBatch(Integer.parseInt(rs.getString("batch")));
            s.setC_semester(Integer.parseInt(rs.getString("c_semester")));
            s.setDiscipline(rs.getString("discipline"));
            stds.put(s.getRoll_no(), s);
        }
        return stds;
    }
    
    public ArrayList<Course> readCourses() throws SQLException{
        String sql="select* from course order by c_id";
        Sql dbsql=new Sql();
        ResultSet rs = dbsql.execQuery(sql);
                    
        ArrayList<Course> crs=new ArrayList<>();

        while(rs.next()){
            Course c=new Course();
            c.setC_code(rs.getString("c_id"));
            c.setC_name(rs.getString("c_name"));
            c.setCrhr(Integer.parseInt(rs.getString("crhr")));
            crs.add(c);
            readSections(c);
        }
        return crs;
    }
    
    public void readSections(Course c) throws SQLException{
        String sql="select* from section where c_id='"+c.getC_code()+"' order by s_id";
        Sql dbsql=new Sql();
        ResultSet rs = dbsql.execQuery(sql);
            
        while(rs.next()){
            Section s=new Section();
            s.setS_name(rs.getString("section"));
            s.setSemester(Integer.parseInt(rs.getString("semester")));
            s.setMax_students(Integer.parseInt(rs.getString("max_students")));
            s.setOffered(rs.getBoolean("offered"));
            s.setS_id(Integer.parseInt(rs.getString("s_id")));
            String temp=rs.getString("teacher_email");
            if(temp!=null){
                Teacher t=teachers.get(temp);
                t.sections.add(s);
                s.setTeacher(t);
            }
            c.sections.add(s);
            s.setCourse(c);
            readEvaluations(s);
            readRegisterations(s);
            readWaitingList(s);
        }
    }
    
    public void readWaitingList(Section s) throws SQLException{
        String sql="select* from waiting_list where sec_id="+s.getS_id()+"order by date_time";
        Sql dbsql=new Sql();
        ResultSet rs=dbsql.execQuery(sql);
        
        while(rs.next()){
            String roll_no=rs.getString("roll_no");
            Student std=students.get(roll_no);
            s.stds.add(std);
        }
    }
    
    public void readEvaluations(Section s) throws SQLException{
        String sql="select* from evaluation where s_id="+s.getS_id()+" order by type";
        Sql dbsql=new Sql();
        ResultSet rs = dbsql.execQuery(sql);
                    
        while(rs.next()){
            Evaluation e=new Evaluation();
            e.setE_id(Integer.parseInt(rs.getString("e_id")));
            e.setName(rs.getString("name"));
            e.setType(rs.getString("type"));
            e.setWeightage(Float.parseFloat(rs.getString("weightage")));
            e.setTotal_marks(Integer.parseInt(rs.getString("total_marks")));
            s.evaluations.add(e);
        } 
    }
    
    public void readRegisterations(Section s) throws SQLException{
        String sql="select* from registeration where sec_id="+s.getS_id()+" order by std_semester";
        Sql dbsql=new Sql();
        ResultSet rs = dbsql.execQuery(sql);
            
        while(rs.next()){
            Registeration r=new Registeration();
            r.setGrade(rs.getString("grade"));
            r.setStd_semester(Integer.parseInt(rs.getString("std_semester")));
            r.setSection(s);
            s.registerations.add(r);
            Student std=students.get(rs.getString("roll_no"));
            r.setStudent(std);
            std.AddReg(r);
            readAttendance(r);
            s.incrNo_of_students();

            readmarks(r,s);
        }
    }
    
    public void readmarks(Registeration r, Section s) throws SQLException
    {
        Student st = r.getStudent();
        String sql="select* from marks join evaluation on marks.e_id=evaluation.e_id where roll_no='"+st.getRoll_no()+
                "' and s_id="+s.getS_id()+"order by marks.e_id";
        Sql dbsql=new Sql();
        ResultSet rs = dbsql.execQuery(sql);
        
        while(rs.next()){
            Marks m = new Marks();
            Evaluation e ;
            m.setScore(rs.getFloat("score"));
            e = s.get_eval(rs.getInt("e_id"));
            e.marks.add(m);
            r.marks.add(m);
            m.setEvaluation(e);
            m.setRegistration(r);
        }
    }
      
    public void readAttendance(Registeration r) throws SQLException{
        String sql="select* from attendance where sec_id="+r.getSection().getS_id()+" and roll_no='"+r.getStudent().getRoll_no()+"' order by date";
        Sql dbsql=new Sql();
        ResultSet rs = dbsql.execQuery(sql);
            
        while(rs.next()){
            Attendance a=new Attendance();
            a.setDate(rs.getString("date"));
            a.setPresence(rs.getString("presence").charAt(0));
            a.setDuration(Float.parseFloat(rs.getString("duration")));
            r.attendance.add(a);
        }
    }
    
    public void readDates() throws SQLException{
        String sql="Select* from imp_dates where name='REG OPEN' order by date desc";
        String sql1="Select* from imp_dates where name='REG CLOSED' order by date desc";
        String sql2="Select* from imp_dates where name='WITHDRAW DEADLINE' order by date desc";
        
        Sql dbsql=new Sql();
        ResultSet rs=dbsql.execQuery(sql);
        if(rs.next()){
            try {
                regStart=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(rs.getString("date"));
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        }
        rs=dbsql.execQuery(sql1);
        if(rs.next()){
            try {
                regEnd=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(rs.getString("date"));
            } catch (ParseException ex) {
               ex.printStackTrace();
            }
        }
        rs=dbsql.execQuery(sql2);
        if(rs.next()){
            try {
                withdrawDeadline=new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(rs.getString("date"));
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public Course binarySearchC(String code){
        
        Comparator<Course> cmp=new Comparator<>(){
          @Override
          public int compare(Course a, Course b){
              return a.getC_code().compareTo(b.getC_code());
          }  
        };
        
        Course c=new Course();
        c.setC_code(code);
        int index=Collections.binarySearch(courses,c,cmp);
        if (index<0)
        {
            return null;
        }
        return courses.get(index);
    }
    
    public java.util.Date getRegStart(){
        return regStart;
    }
    
    public java.util.Date getRegEnd(){
        return regEnd;
    }
    
    public java.util.Date getWithdrawDeadline(){
        return withdrawDeadline;
    }
    
}