import java.util.ArrayList;
import java.util.List;

public class Student extends Person implements Observer{
    private String roll_no;
    private int batch;
    private int c_semester;
    private String discipline;
    List<Semester> semesters;

    public Student(){
        this.semesters = new ArrayList<>();
    }
    
    public Student(String roll_no, int batch, int c_semester, String disc,int sem_total)
    {
        this.roll_no = roll_no;
        this.batch = batch;
        this.c_semester = c_semester;
        this.discipline = disc;
        this.semesters = new ArrayList<>(sem_total);
    }
    
    @Override
    public String getRoll_no() {
        return roll_no;
    }

    public void setRoll_no(String roll_no) {
        this.roll_no = roll_no;
    }

    public int getBatch() {
        return batch;
    }

    public void setBatch(int batch) {
        this.batch = batch;
    }

    public int getC_semester() {
        return c_semester;
    }

    public void setC_semester(int c_semester) {
        this.c_semester = c_semester;
    }

    public String getDiscipline() {
        return discipline;
    }

    public void setDiscipline(String discipline) {
        this.discipline = discipline;
    }
    
    public void calculate_CGPA(){
        double cgpa;
        double total_grade_points  = 0.00;
        int total_hours = 0;
        
        for(int i = 0 ; i < semesters.size() ; i++)
        {
            total_grade_points = total_grade_points + semesters.get(i).get_total_grade();
            total_hours = total_hours + semesters.get(i).get_total_crdhrs();
            cgpa = (total_grade_points/(total_hours*4)) * 4.00;
            semesters.get(i).setCGPA(cgpa);
        }
    }
    
    public void AddReg(Registeration r){
        int sem=r.getStd_semester();
        boolean found=false;
        for (Semester s:semesters){
            if(s.getSemester_no()==sem){
                s.registerations.add(r);
                found=true;
                break;
            }
        }
        if(!found){
            Semester s=new Semester();
            s.setSemester_no(sem);
            s.registerations.add(r);
            semesters.add(s);
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(" ");
        sb.append("Student{roll_no=").append(roll_no);
        sb.append(", batch=").append(batch);
        sb.append(", c_semester=").append(c_semester);
        sb.append(", discipline=").append(discipline);
        sb.append('}');
        return sb.toString();
    }
    
    public void printSemesters(){
        for(Semester s:semesters){
            s.printReg();
        }
        System.out.println("\n\n");
    }
    
    public Semester getSemester(int sem_no){
        for (Semester s:semesters){
            if(s.getSemester_no()==sem_no){
                return s;
            }
        }
        return null;
    }
    
    public boolean registerCourse(Section sec) {
        if (!sec.seatsAvailable())
        {
            return false;
        }
        
        Registeration r=new Registeration();
        r.setStd_semester(c_semester);
        r.setStudent(this);
        r.setSection(sec);
        sec.registerations.add(r);
        this.AddReg(r);
        sec.incrNo_of_students();
        
        String sql="insert into registeration values ('"+this.roll_no+"', "+sec.getS_id()+", "+r.getStd_semester()+", '-')";
        Sql dbsql=new Sql();
        dbsql.execUpdate(sql);
        return true;
    }
    
    public void dropCourse(Registeration r){
        Semester s=this.getSemester(r.getStd_semester());
        r.removeAllAttendance();
        r.removeAllMarks();
        s.registerations.remove(r);
        Section sec=r.getSection();
        sec.registerations.remove(r);
        r.setSection(null);
        r.setStudent(null);
        sec.decrNo_of_students();
        
        String sql="delete from registeration where roll_no='"+this.roll_no+"' and sec_id="+sec.getS_id();
        Sql dbsql=new Sql();
        dbsql.execUpdate(sql);
    }
    
    public void withdrawCourse(Registeration r){
        r.setGrade("W");
        
        String Roll_no=r.getStudent().getRoll_no();
        int sec_id=r.getSection().getS_id();
        
        String sql="update registeration set grade='W' where roll_no='"+Roll_no+"' and sec_id="+sec_id;
        Sql dbsql=new Sql();
        dbsql.execUpdate(sql);
    }
    
    @Override
    public void update(String sec_name){
        EmailThread emailThread=new EmailThread(sec_name,email);
        emailThread.start();
    }
    
    @Override
    public void printName(){
        System.out.println(f_name);
    }
}