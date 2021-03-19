
import java.util.LinkedList;
import java.util.List;

public class Teacher extends Person {
    private String rank;
    private String department;
    List<Section> sections; 
    
    public Teacher(){
        sections=new LinkedList<>();
    }
    
    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.toString()).append(" ");
        sb.append("Teacher{rank=").append(rank);
        sb.append(", department=").append(department);
        sb.append('}');
        return sb.toString();
    }
    
    public void printSections(){
        for (Section s: sections){
            System.out.println(s);
        }
    }
}