import java.util.Comparator;
import java.util.List;
import java.util.LinkedList;

public class Semester {
    private int semester_no;
    private double SGPA;
    private double CGPA;
    List<Registeration> registerations;
    public Semester(){
         this.registerations = new LinkedList<>();
    }
    
    public Semester(int semester_no, double cgpa, double sgpa, int total_reg){
         this.semester_no = semester_no;
         this.CGPA = cgpa;
         this.SGPA = sgpa;
         this.registerations = new LinkedList<>();
    }
    
    public int getSemester_no() {
        return semester_no;
    }

    public void setSemester_no(int semester_no) {
        this.semester_no = semester_no;
    }

    public double getSGPA() {
        return SGPA;
    }

    public void setSGPA(double SGPA) {
        this.SGPA = Math.round(SGPA*100.0)/100.0;
    }

    public double getCGPA() {
        return CGPA;
    }

    public void setCGPA(double CGPA) {
        this.CGPA = Math.round(CGPA*100.0)/100.0;
    }
    
    public int getCrdt_hours(int index) {
        Section sec = registerations.get(index).getSection();
        Course c = sec.getCourse();
        int crdhrs = c.getCrhr();
        return crdhrs;
    }
    public static double grade_to_double(String grade)
    {
        double grade_eqv = 0.00;
        switch(grade)
        {
            case "A+": case "a+":
                grade_eqv = 4.00;
                break;
            case "A": case "a":
                grade_eqv = 4.00;
                break;
            case "A-": case "a-":
                grade_eqv = 3.67;
                break;
            case "B+": case "b+":
                grade_eqv = 3.33;
                break;
            case "B": case "b":
                grade_eqv = 3.00;
                break;
            case "B-": case "b-":
                grade_eqv = 2.67;
                break;
            case "C+": case "c+":
                grade_eqv = 2.33;
                break;
            case "C": case "c":
                grade_eqv = 2.00;
                break;
            case "C-": case "c-":
                grade_eqv = 1.67;
                break;
            case "D+": case "d+":
                grade_eqv = 1.33;
                break;
            case "D": case "d":
                grade_eqv = 1.00;
                break;
            case "F": case "f":
                grade_eqv = 0.00;
                break;
            case "FA": case "fa":
                grade_eqv = 0.00;
                break;
            default:
                grade_eqv=0.00;
        }
        return grade_eqv;
    }
    public int get_total_crdhrs()
    {
        int total_hrs = 0;
        for( int i = 0; i < registerations.size() ;i++)
        {
            String grade = registerations.get(i).getGrade();
            if(grade.compareTo("-")!=0 && grade.compareTo("W")!=0 && grade.compareTo("w")!=0)
            {
                total_hrs = total_hrs + getCrdt_hours(i);
            }
        }
        return total_hrs;
    }
    
    public float get_crdeits_attempted(){
        int total_hrs = 0;
        for( int i = 0; i < registerations.size() ;i++)
        {
            String grade = registerations.get(i).getGrade();
            if(grade.compareTo("W")!=0 && grade.compareTo("w")!=0)
            {
                total_hrs = total_hrs + getCrdt_hours(i);
            }
        }
        return total_hrs;
    }
    
    public float get_credits_earned(){
        float total=0;
        for (Registeration r:registerations){
            if(grade_to_double(r.getGrade())>0){
                total+=r.getSection().getCourse().getCrhr();
            }
        }
        return total;
    }
    
    public double get_total_grade()
    {
        double total = 0;
        
        for( int i = 0; i < registerations.size() ;i++)
        {
            String grade = registerations.get(i).getGrade();
            if(grade != "-" && grade != "W" && grade != "w")
            {
                double g= grade_to_double(grade);
                total = total + (g * getCrdt_hours(i));
            }
        }
        return total;
    }
    public void cal_sgpa() {
        double sgpa = 0;
        double total = 0;
        int total_hrs = 0;
        
        for( int i = 0; i < registerations.size() ;i++)
        {
            String grade = registerations.get(i).getGrade();
            if(grade != "-" && grade != "W" && grade != "w")
            {
                double g= grade_to_double(grade);
                total = total + (g * getCrdt_hours(i));
                total_hrs = total_hrs + getCrdt_hours(i);
            }
        }
        sgpa = (total/(total_hrs*4)) * 4.00;
        setSGPA(sgpa);
    }
    
    public void printReg(){
        for(Registeration r: registerations){
            System.out.println(r);
        }
    }

    @Override
    public String toString() {
        return "Semester "+semester_no;
    }
    
    public static Comparator getComparator(){
        Comparator<Semester> cmp=new Comparator<>(){
            @Override
            public int compare(Semester a, Semester b){
                if(a.semester_no>b.semester_no){
                    return 1;
                }
                else if(a.semester_no<b.semester_no){
                    return -1;
                }
                return 0;
            }
        };
        return cmp;
    }
    
    public Registeration findReg(String sectionName){
        for(Registeration r:registerations){
            if(r.getSection().getS_name().compareTo(sectionName)==0){
                return r;
            }
        }
        return null;
    }
    
    public boolean allWithdrawn(){
        for(Registeration r : registerations){
            if (r.getGrade().compareTo("W")!=0 && r.getGrade().compareTo("w")!=0){
                return false;
            }
        }
        return true;
    }
}