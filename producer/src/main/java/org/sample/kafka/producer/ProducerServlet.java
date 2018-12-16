package org.sample.kafka.producer;

import com.sample.messagehub.core.MessageHubContext;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Properties;

@WebServlet("/sendmsg")
public class ProducerServlet extends HttpServlet {

    private final Logger logger = Logger.getLogger(ProducerServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String msg = req.getParameter("msg");
        String groupCnt = req.getParameter("groupCnt");
        String actCnt = req.getParameter("actCnt");
        Properties props = MessageHubContext.getServerProperties();

        String topic = props.getProperty("topic");
        KafkaProducer producer = null;

        if(isNull(groupCnt) && isNull(actCnt)) {
            ProducerManager.sendGroupMsgs(topic, 3, 20);
        }else {
            ProducerManager.sendGroupMsgs(topic, Integer.valueOf(groupCnt), Integer.valueOf(actCnt));
        }

        try {
            producer = ProducerManager.getProducer(MessageHubContext.getProducerProperties(), topic);
            ProducerManager.sendMsgs(topic ,msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private boolean isNull(String data) {

        boolean isNull = false;

        if("".equalsIgnoreCase(data) || data == null) {
            isNull = true;
        }

        return isNull;
    }
}
