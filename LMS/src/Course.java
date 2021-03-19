
import java.util.LinkedList;
import java.util.List;

public class Course {
    private String c_name;
    private String c_code;
    private int crhr;
    List<Section> sections;
    
    Course(){
        sections=new LinkedList<>();
    }
    
    public String getC_name() {
        return c_name;
    }

    public void setC_name(String c_name) {
        this.c_name = c_name;
    }

    public String getC_code() {
        return c_code;
    }

    public void setC_code(String c_code) {
        this.c_code = c_code;
    }

    public int getCrhr() {
        return crhr;
    }

    public void setCrhr(int crhr) {
        this.crhr = crhr;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Course{c_name=").append(c_name);
        sb.append(", c_code=").append(c_code);
        sb.append(", crhr=").append(crhr);
        sb.append('}');
        return sb.toString();
    }
    
    public void printSections(){
        for (Section s: sections){
            System.out.println(s);
        }
    }
    
    public boolean getOffered(){
        for(Section s:sections){
            if(s.isOffered()){
                return true;
            }
        }
        return false;
    }
}