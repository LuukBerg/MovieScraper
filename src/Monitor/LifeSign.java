package Monitor;

import sun.plugin2.message.Serializer;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LifeSign implements Serializable {
    private String name;
    private Date date;
    public LifeSign() {
    }

    public LifeSign(String name) {
        this.name = name;
        this.date = new Date();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return "name= " + name + " date= " + formatter.format(date);
    }
}
