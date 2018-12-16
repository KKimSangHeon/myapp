package com.sample.messagehub.core;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MessageHubContext {


    private static final Logger logger = Logger.getLogger(MessageHubContext.class);

    static Properties consumerProps = null;
    static Properties producerProps = null;
    static Properties serverProps = null;

    public static Properties getConsumerProperties() throws IOException {

        if(consumerProps == null) {

            consumerProps = getProperties("consumer");
        }

        return consumerProps;

    }

    public static Properties getProducerProperties() throws IOException {

        if(producerProps == null) {
            producerProps = getProperties("producer");
        }

        return producerProps;
    }

    public static Properties getServerProperties() throws IOException {

        if(serverProps == null) {
            serverProps = getProperties("server");
        }

        return serverProps;
    }

    public static Properties getProperties(String type) throws IOException {

        String runEnv = System.getenv("MESSAGEHUB_RUN_ENV");
        logger.info("Messagehub environment value is : " + runEnv);
        Properties properties = null;
        String propertyFileName = null;
        InputStream input = null;

        if("k8s".equalsIgnoreCase(runEnv)) {
            propertyFileName = "/etc/kafka/k8s.messagehub." + type + ".properties";
            input = new FileInputStream(new File(propertyFileName));
        }else {
            propertyFileName = "messagehub." + type + ".properties";
            input = getClassLoader().getResourceAsStream(propertyFileName);
        }

        logger.info("Kafka property file name is : " + propertyFileName);

        properties = new Properties();
        properties.load(input);

        return properties;
    }

    public static ClassLoader getClassLoader() {
        ClassLoader loader = null;

        if(loader ==null){
            loader =  Thread.currentThread().getContextClassLoader();
        }

        return loader;
    }

    public static Class loadClass(String className) throws ClassNotFoundException {

        Class type = null;
        type = getClassLoader().loadClass(className);

        return  type;
    }
}
