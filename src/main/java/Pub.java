import org.redisson.Redisson;
import org.redisson.api.RList;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.redisson.config.Config;

import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Pub
{
    private static RList<myMessage> myMessageRList;
    public static void main(String[] args)
    {
        //redis address
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://127.0.0.1:6379");
        //create redis client
        RedissonClient client = Redisson.create(config);

        //enter roomName
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter room name: ");
        String roomName = scanner.nextLine();

        //get old message
        getMessage(client, roomName);

        //open thread for listen from chanel
        subscribe(client, roomName);

        //publish to chanel
        mypublish(client, roomName);

    }

    private static void mypublish(RedissonClient client, String chatRoomName)
    {
        RTopic publishTopic = client.getTopic(chatRoomName);
        String usrName = "alice";

        //create object scanner
        Scanner scanner = new Scanner(System.in);

        while (true)
        {
            String msg = scanner.nextLine();
            Date date = new Date();
            myMessage mes = new myMessage(usrName, date, msg);

            //add data to mem and set expire = 1 day
            myMessageRList.add(mes);
            myMessageRList.expire(1, TimeUnit.DAYS);

            //publish mess
            publishTopic.publish(mes);
        }
    }
    private static void subscribe(RedissonClient client, String chatRoomName)
    {
        RTopic subscribeTopic = client.getTopic(chatRoomName);

        //addListener open a thread to listen from chanel, so no need a loop for listenning
        subscribeTopic.addListener(myMessage.class, new MessageListener<myMessage>()
        {
            public void onMessage(CharSequence charSequence, myMessage mes)
            {
                System.out.println(mes.GetContent());
            }
        });
    }

    private static void getMessage(RedissonClient client, String chatRoomName)
    {
        myMessageRList = client.getList(chatRoomName);
        for (myMessage msg: myMessageRList)
        {
            System.out.println(msg.GetContent());
        }
    }
}
