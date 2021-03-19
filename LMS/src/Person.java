
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class Person {
    protected Date dob;
    protected String email;
    protected String password;
    protected String f_name;
    protected String l_name;

    public Date getDob() {
        return dob;
    }

    public void setDob(String dob) {
        try {
            this.dob=new SimpleDateFormat("yyyy-MM-dd").parse(dob);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getF_name() {
        return f_name;
    }

    public void setF_name(String f_name) {
        this.f_name = f_name;
    }

    public String getL_name() {
        return l_name;
    }
    
    public String getFullName(){
        return f_name+" "+l_name;
    }
    
    public void setL_name(String l_name) {
        this.l_name = l_name;
    }
    
    public String getDateS(){
        StringBuilder sb=new StringBuilder();
        sb.append(dob.getDate()).append("-").append(dob.getMonth()+1).append("-").append(dob.getYear()+1900);
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Person{dob=").append(this.getDateS());
        sb.append(", email=").append(email);
        sb.append(", password=").append(password);
        sb.append(", f_name=").append(f_name);
        sb.append(", l_name=").append(l_name);
        sb.append('}');
        return sb.toString();
    }
}