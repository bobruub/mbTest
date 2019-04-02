import java.lang.*;
import java.util.Scanner;
import com.rabbitmq.client.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import java.io.*;
import java.util.Date;
import java.text.SimpleDateFormat;

final public class mbTest 
{
  private final static String HOST_NAME = "rabbitmqHostName";
  private final static String QUEUE_NAME = "rabbitmqQueueName";
  private final static String USER_NAME = "rabbitmqUserName";
  private final static String PASS_WORD = "rabbitmqPassword";
  public static void main(String[] args) 
  {
	Date date = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss:SSS");
    String formattedDate = sdf.format(date);
    
	// debug to see if program starts
    BufferedWriter out = null;
    try {
      FileWriter fstream = new FileWriter("out.txt", true); //true tells to append data.
      out = new BufferedWriter(fstream);
      out.write("\nStarting at: " + formattedDate);
    } catch (IOException e) {
      System.err.println("Error: Writing to file: " + e.getMessage());
    } finally {
      try {
        out.close();
      } catch (IOException e) {
      System.err.println("Error: Closing File: " + e.getMessage());
    }
    }
    
    // display command line paramaters
    for (String s: args) {
      System.out.println("INFO: " + s);
    }
    
	// establish factory connection
    Connection connection = null;
    Channel channel = null;
    ConnectionFactory factory = null;
    QueueingConsumer consumer = null;
    try {
      factory = new ConnectionFactory();
      factory.setUsername(USER_NAME);
      factory.setPassword(PASS_WORD);
      factory.setHost(HOST_NAME);
      factory.setPort(5672);
      factory.setVirtualHost("/");
      connection = factory.newConnection();
      channel = connection.createChannel();
    } catch (Exception e) {
      System.out.println("ERROR: HOST_NAME: " + HOST_NAME);
      System.out.println("ERROR: QUEUE_NAME: " + QUEUE_NAME);
      System.out.println("ERROR: Factory Connect Exception: " + e);
    }
    
	// establish queue connection
    try {
      channel.queueDeclare(HOST_NAME, false, false, false, null);
      int prefetchCount = 1;
      channel.basicQos(prefetchCount);
      boolean autoAck = false;
      consumer = new QueueingConsumer(channel);
      channel.basicConsume(QUEUE_NAME, autoAck, consumer);
    } catch (Exception e) {
      System.out.println("ERROR: HOST_NAME: " + HOST_NAME);
      System.out.println("ERROR: QUEUE_NAME: " + QUEUE_NAME);
      System.out.println("ERROR: Queue Connect Exception: " + e); 
    }
    
	// read and display messages to stdout.
    try {
      System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
      QueueingConsumer.Delivery delivery = consumer.nextDelivery();
      String message = new String(delivery.getBody());
      message = message.replaceAll("[\r\n]", "");
      System.out.println("INFO: " + message + "\n");
      channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
    } catch (Exception e) {
      System.out.println("ERROR: HOST_NAME: " + HOST_NAME);
      System.out.println("ERROR: QUEUE_NAME: " + QUEUE_NAME);
      System.out.println("ERROR: Read Exception: " + e); 
    }
    
  }
  
  
}
