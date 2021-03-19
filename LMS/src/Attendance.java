
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Attendance {
    private Date date;
    private char presence;
    private float duration;

    public Attendance() {
        date=null;
        presence='-';
        duration=(float) 1.5;
    }

    public Attendance(Date date, char presence, float duration) {
        this.date = date;
        this.presence = presence;
        this.duration = duration;
    }
    
    public Attendance(String date, char presence, float duration){
        setDate(date);
        this.presence = presence;
        this.duration = duration;
    }
    
    public Date getDate() {
        return date;
    }

    public final void setDate(String dob) {
        try {
            this.date=new SimpleDateFormat("yyyy-MM-dd").parse(dob);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }
    
    public String getDateAsString(){
        StringBuilder sb=new StringBuilder();
        sb.append(date.getYear()+1900).append("/").append(date.getMonth()+1).append("/").append(date. getDate());
        return sb.toString();
    }

    public char getPresence() {
        return presence;
    }

    public void setPresence(char presence) {
        this.presence = presence;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return getDateAsString();
    }
    
}