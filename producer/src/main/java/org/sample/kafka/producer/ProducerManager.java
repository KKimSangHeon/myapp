package org.sample.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.messagehub.core.GroupInfo;
import com.sample.messagehub.core.MessageHubContext;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ProducerManager {

    private static final Logger logger = Logger.getLogger(ProducerManager.class);


    private static KafkaProducer<Long, String> kafkaProducer;
    private static int num_partitions = 0;

    public static KafkaProducer<Long, String> getProducer(Properties producerProps, String topic) throws Exception {

        if(kafkaProducer == null) {

            kafkaProducer = new KafkaProducer<Long, String>(producerProps);

            boolean fromZookeeper = "true".equalsIgnoreCase(MessageHubContext.getServerProperties().getProperty("kafka.brokerlist.fromzookeeper"));
            if(fromZookeeper) {
//                String brokerList = getBrokerList();
//                producerProps.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerList);
            }


            try {
                // Checking for topic existence.
                // If the topic does not exist, the kafkaProducer will retry for about 60 secs
                // before throwing a TimeoutException
                // see configuration parameter 'metadata.fetch.timeout.ms'
                List<PartitionInfo> partitions = kafkaProducer.partitionsFor(topic);
                num_partitions = partitions.size();
                logger.info(partitions.toString());
            } catch (TimeoutException kte) {
                logger.error("Topic '" + topic + "' may not exist - application will terminate");
                kafkaProducer.close();
                throw new IllegalStateException("Topic '" + topic + "' may not exist - application will terminate", kte);
            }
        }

        return kafkaProducer;
    }

    public static void sendMsgs(String topic, String msg) {

        long offset = -1;

        ProducerRecord<Long, String> record = new ProducerRecord<Long, String>(topic, 1, Long.valueOf("1"), msg);
//        Future<RecordMetadata> future = kafkaProducer.send(record);

        try{

            offset = kafkaProducer.send(record).get().offset();
        }catch(Exception e){
            e.printStackTrace();
        }



    }

    public static void sendGroupMsgs(String topic, int groupCnt, int actCnt) throws JsonProcessingException, UnknownHostException {
        List<GroupInfo> msg = createMsgs(groupCnt, actCnt);

        long offset=-1;

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        long startTime = System.currentTimeMillis();

        int msgCnt=0;

        for(GroupInfo lot : msg) {


            try {

                int key_num = lot.getGroupId() % num_partitions;

                lot.setPartitionId(key_num);
                String message = new ObjectMapper().writeValueAsString(lot);
                ProducerRecord<Long, String> record = new ProducerRecord<Long, String>(topic, key_num, Long.valueOf(lot.getGroupId()), message);

//				Future<RecordMetadata> future = kafkaProducer.send(record);
//				RecordMetadata recordMetadata = future.get(5000, TimeUnit.MILLISECONDS);

                try {
                    offset = kafkaProducer.send(record).get().offset();
//                    DBUtil.insertSendLog(lot);
                    logger.info("[ LOT / ACT ] : " + lot.getGroupId() + " / " + lot.getActId());
                    msgCnt++;

                }catch(Exception ex) {
                    logger.info("Send message faild " + ex.getMessage());
                    if(offset < 0) {
                        logger.info("Send message faild : resend lot data " + message);
                        ProducerRecord<Long, String> record2 = new ProducerRecord<Long, String>(topic, key_num, Long.valueOf(lot.getGroupId()), message);
                    }
                }

//			}catch (final InterruptedException e) {
//				logger.info("Producer closing - caught exception: " + e);
            } catch (final Exception e) {
                logger.info("Sleeping for 5s - Producer has caught : " + e);
                e.printStackTrace();
                try {
                    Thread.sleep(5000); // Longer sleep before retrying
                } catch (InterruptedException e1) {
                    logger.info("Producer closing - caught exception: " + e);
                }
            }
        }

        long endTime = System.currentTimeMillis();

        logger.info("[Summary]");
        logger.info("Start : " + sdf.format(new Date(startTime)));
        logger.info("End : " + sdf.format(new Date(endTime)));
        logger.info("Elapsed : " + (endTime - startTime)/1000);
        logger.info("Total msg : " + msgCnt);

    }

    public static List<GroupInfo> createMsgs(int groupCnt, int actCnt) throws UnknownHostException {
        List<GroupInfo> msgs = new ArrayList<GroupInfo>(groupCnt * actCnt);
        String hostname = InetAddress.getLocalHost().getHostName();
        String[] splittedHostname = hostname.split("-");
        String appInst = splittedHostname[splittedHostname.length -1];
        logger.info(hostname);


        for(int i=0;i<groupCnt;i++) {
            for(int j=0;j<actCnt;j++) {
                GroupInfo lot = new GroupInfo(i+1);
                msgs.add(lot);
            }
        }

        msgs = shuffleLot(msgs);

        List<Integer> lots = new ArrayList<Integer>(groupCnt);
        for(int i=0 ; i< groupCnt; i++) {
            lots.add(i, 1);
        }

        for(GroupInfo GroupInfo : msgs) {
            int lotId = GroupInfo.getGroupId();
            int actId = lots.get(lotId -1);
            GroupInfo.setActId(actId);
            //			lots.add(lotId -1, actId+1);
            lots.set(lotId-1, actId+1);

            GroupInfo.setApp("sender");
            GroupInfo.setApp_inst(hostname.split("-")[2]);

        }

        return msgs;
    }

    public static List<GroupInfo> shuffleLot(List<GroupInfo> msgs) {

        Collections.shuffle(msgs);

        return msgs;
    }

    public static void main(String[] args) throws Exception {

        List<GroupInfo> msgs = ProducerManager.createMsgs(3, 20);

        for(GroupInfo lot : msgs) {
            logger.info("LOT ID : " + lot.getGroupId() + " , ACT ID : " + lot.getActId());
        }

    }


//    public static String getBrokerList() throws Exception {
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
