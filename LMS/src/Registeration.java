
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Registeration {
    private String grade;
    private Student student;
    private Section section;
    private int std_semester;
    List<Attendance> attendance;
    List<Marks> marks;
    
    public Registeration(){
        grade="-";
        student=null;
        section=null;
        std_semester=0;
        attendance=new ArrayList<>();
        marks=new ArrayList<>();
    }
    
    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    public int getStd_semester() {
        return std_semester;
    }

    public void setStd_semester(int std_semester) {
        this.std_semester = std_semester;
    }

    @Override
    public String toString() {
        return student.getRoll_no();
    }
    
    public void printAttendance(){
        for(Attendance a:attendance){
            System.out.println(a);
        }
    }
    
    public void addAttendance(Attendance a){
        attendance.add(a);
        System.out.println(a.getDateAsString());
        String sql="insert into attendance values('"+a.getDateAsString()+"', '"+student.getRoll_no()+"', "+
                section.getS_id()+", '"+a.getPresence()+"' ,"+a.getDuration()+")";
        Sql dbsql=new Sql();
        dbsql.execUpdate(sql);
    }
    
    public void updateAttendance(Attendance a, char presence, float duration){
        if(a.getPresence()==presence && a.getDuration()==duration){
            return;
        }
        a.setPresence(presence);
        String sql="update attendance set presence='"+presence+"', duration="+duration+" where roll_no='"+this.getStudent().getRoll_no()+
                "' and date='"+a.getDateAsString()+"' and sec_id="+this.getSection().getS_id();
        Sql dbsql=new Sql();
        dbsql.execUpdate(sql);
    }
    
    public void addMarks(Evaluation e){
        Marks m=new Marks(0,e,this);
        this.marks.add(m);
        e.marks.add(m);
        String sql2="insert into marks values('"+this.getStudent().getRoll_no()+"', "+e.getE_id()+", 0)";
        Sql dbsql=new Sql();
        dbsql.execUpdate(sql2);
    }
    
    public void upDateMarks(Marks m,float marks){
        if(m.getScore()==marks){
            return;
        }
        m.setScore(marks);
        String sql="update marks set score="+marks+" where roll_no='"+this.getStudent().getRoll_no()+
                "' and e_id="+m.getEvaluation().getE_id();
        Sql dbsql=new Sql();
        dbsql.execUpdate(sql);
    }
    
    public void assignGrade(String grade){
        this.grade=grade;
        String sql="update registeration set grade='"+grade+"' where roll_no='"+student.getRoll_no()+"' and sec_id="+section.getS_id();
        Sql dbsql=new Sql();
        dbsql.execUpdate(sql);
    }
    
    public Attendance getAttendance(java.util.Date d){
        for(Attendance a:attendance){
            if(a.getDate().compareTo(d)==0){
                return a;
            }
        }
        return null;
    }
    
    public float getTotalMarks(){
        float total=0;
        for(Marks m:marks){
            total+=m.getAbsMarks();
        }
        return total;
    }
    
    public float attendancePercentage(){
        float totalDuration=0;
        float totalAttendance=0;
        boolean late=false;
        
        for(Attendance a: attendance){
            totalDuration+=a.getDuration();
            if(a.getPresence()=='P'){
                totalAttendance+=a.getDuration();
            }
            else if(a.getPresence()=='L'){
                if(late){
                    late=false;
                    totalAttendance-=a.getDuration();
                }
                else{
                    late=true;
                    totalAttendance+=a.getDuration();
                }
            }
        }
        
        return (totalAttendance/totalDuration)*100;
    }
    
    public void removeAllAttendance(){
        for(Attendance a:attendance){
            String s="delete from attendance where date='"+a.getDateAsString()+"' and roll_no='"+student.getRoll_no()+
                    "' and sec_id="+section.getS_id();
            Sql mssql=new Sql();
            mssql.execUpdate(s);
        }
        attendance.clear();
    }
    
    public void removeAllMarks(){
        for (Marks m:marks){
            m.delMarksFromEvaluation();
        }
        marks.clear();
    }
}