import java.util.ArrayList;
import java.util.List;

public class Evaluation {
    private String name;
    private String type;
    private float weightage;
    private int total_marks;
    private int e_id;
    private float min;
    private float max;
    private float avg;
    List<Marks> marks;

    public Evaluation(String name, String type, float weightage, int total_marks) {
        this.name = name;
        this.type = type;
        this.weightage = weightage;
        this.total_marks = total_marks;
        marks=new ArrayList();
    }
    
    public Evaluation(){
        name=null;
        type=null;
        weightage=0;
        total_marks=0;
        e_id=-1;
        marks=new ArrayList();
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public float getWeightage() {
        return weightage;
    }

    public void setWeightage(float wightage) {
        this.weightage = wightage;
    }

    public int getTotal_marks() {
        return total_marks;
    }

    public void setTotal_marks(int total_marks) {
        this.total_marks = total_marks;
    }

    public int getE_id() {
        return e_id;
    }

    public void setE_id(int e_id) {
        this.e_id = e_id;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Evaluation{name=").append(name);
        sb.append(", type=").append(type);
        sb.append(", weightage=").append(weightage);
        sb.append(", total_marks=").append(total_marks);
        sb.append(", e_id=").append(e_id);
        sb.append('}');
        return sb.toString();
    }
    
    public void calculateStats(){
        float total=0;
        
        if(marks.size()==0){
            return;
        }
        
        max=marks.get(0).getScore();
        min=max;
        
        for(Marks m:marks){
            if(m.getScore()<min){
                min=m.getScore();
            }
            else if(m.getScore()>max){
                max=m.getScore();
            }
            total+=m.getScore();
        }
        
        avg=total/marks.size();
    }
    
    public float getMax(){
        return max;
    }
    
    public float getMin(){
        return min;
    }
    
    public float getAvg(){
        return avg;
    }
    
    public void updateEvaluation(float w,int m){
        if(weightage==w && total_marks==m){
            return;
        }
        
        weightage=w;
        total_marks=m;
        
        Sql dbsql=new Sql();
        
        String s="update evaluation set weightage="+weightage+", total_marks="+total_marks+" where e_id="+e_id;
        dbsql.execUpdate(s);
    }
}