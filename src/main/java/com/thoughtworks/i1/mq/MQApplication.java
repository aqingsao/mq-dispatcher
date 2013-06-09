package com.thoughtworks.i1.mq;

import com.google.common.collect.ImmutableList;
import com.google.inject.Module;
import com.thoughtworks.i1.commons.I1Application;
import com.thoughtworks.i1.commons.config.Configuration;
import com.thoughtworks.i1.commons.config.DatabaseConfiguration;
import com.thoughtworks.i1.mq.jms.JmsModule;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.ConnectionFactory;
import java.util.Collection;

public class MQApplication extends I1Application{
    @Override
    protected Configuration defaultConfiguration() {
        return Configuration.config()
                .app().contextPath("/mq").end()
                .http().port(8052).end()
                .database().persistUnit("domain").with(DatabaseConfiguration.H2.driver, DatabaseConfiguration.H2.tempFileDB, DatabaseConfiguration.H2.compatible("ORACLE"), DatabaseConfiguration.Hibernate.dialect("Oracle10g"), DatabaseConfiguration.Hibernate.showSql).user("sa").password("").end()
                .build();
    }

    @Override
    protected Collection<? extends Module> getCustomizedModules() {
        String brokerUrl = "tcp://localhost:61618/";
        String queue = "TEST.FOO";

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        return ImmutableList.of(new JmsModule.Builder(connectionFactory, queue).transacted().buildModule());
    }


    public static void main(String[] args) throws Exception {
        new MQApplication().start(true);
    }
}
