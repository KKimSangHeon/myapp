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
        Properties props = MessageHubContext.getServerProperties();

        String topic = props.getProperty("topic");
        KafkaProducer producer = null;


        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        JedisPool pool = new JedisPool(jedisPoolConfig, "redis-ibm-redis-ha-dev-master-svc.kafka.svc.cluster.local");
//        Jedis jedis = new Jedis("redis-ibm-redis-ha-dev-master-svc.kafka.svc.cluster.local:6379");

        Jedis jedis = pool.getResource();
        jedis.set("100", msg);

        String cachedStr = jedis.get("100");

        logger.info("Cached message is " + cachedStr);



        try {
            producer = ProducerManager.getProducer(MessageHubContext.getProducerProperties(), topic);
            ProducerManager.sendMsgs(topic ,msg);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
