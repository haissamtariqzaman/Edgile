import java.util.LinkedList;
import java.util.List;

public abstract class Subject {
    protected List<Observer> stds;
    
    public Subject(){
        stds=new LinkedList<>();
    }
    
    public abstract void notifyObserver();
    
    public void PrintStdNames(){
        for (Observer o: stds){
            o.printName();
        }
    }
    
    public boolean alreadyObserving(Student s){
        for (Observer o: stds){
            if((Student)o==s)
            {
                return true;
            }
        }
        return false;
    }
}