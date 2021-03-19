
public class Marks {
    private float score;
    private Evaluation evaluation;
    private Registeration registeration;
    
    public Marks() {
        this.score = -1;
        this.evaluation = null;
        this.registeration = null;
    }
    
    public Marks(float score, Evaluation evaluation, Registeration registeration) {
        this.score = score;
        this.evaluation = evaluation;
        this.registeration = registeration;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public Registeration getRegisteration() {
        return registeration;
    }
    
    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }
    
    public void setRegistration(Registeration r)
    {
        this.registeration = r;
    }
    public void setEvaluation(Evaluation e)
    {
        this.evaluation = e;
    }

    @Override
    public String toString() {
        return evaluation.getName();
    }
    
    public float getAbsMarks(){
        return (score/evaluation.getTotal_marks())*evaluation.getWeightage();
    }
    
    public void delMarksFromEvaluation(){
        evaluation.marks.remove(this);
    }
    
}