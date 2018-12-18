package org.sample.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.messagehub.core.GroupInfo;
import com.sample.messagehub.core.MessageHubContext;
import com.sample.messagehub.util.DBUtil;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.util.*;

public class ConsumerRunnable implements Runnable {

    private final Logger logger = Logger.getLogger(ConsumerRunnable.class);

    private KafkaConsumer<Long, String> kafkaConsumer;
    private ArrayList<String> topicList;
    private boolean closing;
    private ArrayList<String> consumedMessages;
    private String currentConsumedMessage;

    public ConsumerRunnable(String broker, String topic) throws Exception {
        consumedMessages = new ArrayList<String>();
        closing = false;
        topicList = new ArrayList<String>();

        Properties props = MessageHubContext.getConsumerProperties();

//        String brokerList = getBrokerList();
//        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);

        kafkaConsumer = new KafkaConsumer<Long, String>(props,
                (Deserializer)MessageHubContext.loadClass(props.getProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG)).newInstance(),
                (Deserializer)MessageHubContext.loadClass(props.getProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG)).newInstance());


        topicList.add(topic);
        kafkaConsumer.subscribe(topicList, new ConsumerRebalanceListener() {

            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                logger.info("onpartitionrevoked");
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
                try {
                    logger.info("Partitions assigned, consumer seeking to end.");

                    for (TopicPartition partition : partitions) {
                        long position = kafkaConsumer.position(partition);
                        logger.info("current Position: " + position);

                        logger.info("Seeking to end...");
                        kafkaConsumer.seekToEnd(Arrays.asList(partition));
                        logger.log(Level.WARN, "Seek from the current position: " + kafkaConsumer.position(partition));

                        kafkaConsumer.seek(partition, position);
                    }
                    logger.info("Producer can now begin producing messages.");
                } catch (final Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }




    @Override
    public void run() {

        logger.info("Consumer is starting.");

        while (!closing) {
            try {
                currentConsumedMessage = "consumer is waiting for messages to be consumed ...";
                ConsumerRecords<Long, String> records = kafkaConsumer.poll(Integer.MAX_VALUE);
                for(TopicPartition partition :  records.partitions()) {

                    List<ConsumerRecord<Long,String>> partitionRecords = records.records(partition);
                    for(ConsumerRecord<Long, String> record : partitionRecords) {

                        long offset = record.offset();

                        GroupInfo group = new ObjectMapper().readValue(record.value(), GroupInfo.class);

                        group.setPartitionId(record.partition());
                        group.setOffset(offset);
                        group.setRetry_count(0);
                        group.setApp("msgapp");

                        String hostname = InetAddress.getLocalHost().getHostName();
                        String[] splittedHostname = hostname.split("-");
                        group.setApp_inst(splittedHostname[splittedHostname.length -1]);

                        logger.info("Consumer Record: [" + record.topic() + ", " + record.partition() + "] : " + record.value() + ", " + offset);
                        DBUtil.insertGroupActivity(group);


                        kafkaConsumer.commitSync(Collections.singletonMap(partition, new OffsetAndMetadata(offset + 1)));
                    }
                }

//				Thread.sleep(1000);
            } catch (final Exception e) {
                logger.error("Consumer has failed with exception: " + e);
                shutdown();
            }
        }

        logger.info("Consumer is shutting down.");
        kafkaConsumer.close();

    }

    public void shutdown() {
        closing = true;
    }

//    public String getBrokerList() throws Exception {
//
//        String blist = "";
//
//        String zookeeperHost = MessageHubContext.getServerProperties().getProperty("zookeeper.host");
//        String zookeeperPort = MessageHubContext.getServerProperties().getProperty("zookeeper.port");
//        String zookeeperTimeOut = MessageHubContext.getServerProperties().getProperty("zookeeper.connect.timeout");
//
//        ZooKeeper zk = new ZooKeeper(zookeeperHost + ":"+  zookeeperPort, Integer.valueOf(zookeeperTimeOut), null);
//        List<String> ids = zk.getChildren("/brokers/ids", false);
//        List<Map> brokerList = new ArrayList<>();
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        for (String id : ids) {
//            Map map = objectMapper.readValue(zk.getData("/brokers/ids/" + id, false, null), Map.class);
//            blist += map.get("host") + ":" + map.get("port") + ",";
//            brokerList.add(map);
//        }
//
//        if(blist.endsWith(",")) {
//            blist = blist.substring(0, blist.length() -1);
//        }
//
//        return blist;
//    }
}
