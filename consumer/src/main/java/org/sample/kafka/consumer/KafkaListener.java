package org.sample.kafka.consumer;

import com.sample.messagehub.core.MessageHubContext;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.log4j.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.IOException;

@WebListener
public class KafkaListener implements ServletContextListener {

    private final Logger logger = Logger.getLogger(KafkaListener.class);

    private ConsumerRunnable consumerRunnable;
    private Thread consumerThread = null;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Start of KafkaListener");

        ServletContext ctx = sce.getServletContext();

        try {
            consumerRunnable = new ConsumerRunnable(MessageHubContext.getConsumerProperties().getProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG),
                    MessageHubContext.getServerProperties().getProperty("topic"));
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        consumerThread = new Thread(consumerRunnable);
        consumerThread.start();



        ctx.setAttribute("consumer", consumerRunnable);

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("End of KafkaListener");

        ServletContext ctx = sce.getServletContext();
        ConsumerRunnable consumer = (ConsumerRunnable) ctx.getAttribute("consumer");

        consumer.shutdown();

    }
}
