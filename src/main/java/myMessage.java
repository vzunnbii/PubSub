import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class myMessage implements Serializable
{

    private final String usrName;
    private final String msg;
    private final String date;

    public myMessage(String usrName, Date date, String message)
    {
        this.usrName = usrName;

        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        this.date = formatter.format(date);

        this.msg = message;
    }

    public String GetContent()
    {
        return "["+usrName+"]"+"--"+date+": "+msg;
    }
}
