package org.qualipso.factory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class FactoryConfig {

    private static Log logger = LogFactory.getLog(FactoryConfig.class);
    private static FactoryConfig config;
    private Properties props;

    private FactoryConfig(String configFilePath) throws Exception {
        props = new Properties();
        FileInputStream in = new FileInputStream(configFilePath);
        props.load(in);
        in.close();
    }

    private FactoryConfig(URL configFileURL) throws Exception {
        props = new Properties();
        InputStream in = configFileURL.openStream();
        props.load(in);
        in.close();
    }

    public static FactoryConfig getInstance() {

        try {
            if (config == null) {
                String configFilePath = System.getProperty("qualipso.factory.config");
                if (configFilePath != null && configFilePath.length() != 0) {
                    config = new FactoryConfig(configFilePath);
                    logger.info("using custom config file : " + configFilePath);
                } else {
                    URL configFileURL = FactoryConfig.class.getClassLoader().getResource("config.properties");
                    config = new FactoryConfig(configFileURL);
                    logger.info("using default config file : " + configFileURL.getPath());
                }
            }

            return config;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("unable to load configuration", e);
        }
        return null;
    }

    public String getProperty(String name) {
        return props.getProperty(name);
    }

}
