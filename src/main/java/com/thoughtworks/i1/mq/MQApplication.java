package com.thoughtworks.i1.mq;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import com.thoughtworks.i1.commons.I1Application;
import com.thoughtworks.i1.commons.config.Configuration;
import com.thoughtworks.i1.commons.config.DatabaseConfiguration;
import com.thoughtworks.i1.commons.server.Embedded;
import com.thoughtworks.i1.mq.jms.QueueReceiver;
import com.thoughtworks.i1.mq.service.DeviceService;
import com.thoughtworks.i1.mq.service.DispatchService;
import com.thoughtworks.i1.mq.service.MessageService;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.ConnectionFactory;
import javax.jms.MessageListener;
import java.util.Collection;

public class MQApplication extends I1Application {
    @Override
    protected Configuration defaultConfiguration() {
        return Configuration.config()
                .app().jerseyServletModule("/api/*", "com.thoughtworks.i1.mq.api").contextPath("/mq").end()
                .http().port(8052).end()
                .database().persistUnit("domain").with(DatabaseConfiguration.H2.driver, DatabaseConfiguration.H2.tempFileDB, DatabaseConfiguration.H2.compatible("ORACLE"), DatabaseConfiguration.Hibernate.dialect("Oracle10g"), DatabaseConfiguration.Hibernate.showSql).user("sa").password("").end()
                .build();
    }

    @Override
    protected Collection<? extends Module> getCustomizedModules() {
        return ImmutableList.of(new MQModule());
    }

    public static void main(String[] args) throws Exception {
        new MQApplication().start(true);
    }

    @Override
    public Embedded start(boolean standalone) {
        getInjector().getInstance(QueueReceiver.class);
        return super.start(standalone);
    }

    public static class MQModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(MessageService.class).in(Scopes.SINGLETON);
            bind(DispatchService.class).in(Scopes.SINGLETON);
            bind(DeviceService.class).in(Scopes.SINGLETON);

            String brokerUrl = "tcp://localhost:61618";

            ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
            bind(ConnectionFactory.class).toInstance(connectionFactory);
            bind(MessageListener.class).to(MessageService.class);

            bind(String.class).annotatedWith(Names.named("queueName")).toInstance("TEST.FOO");
            bind(String.class).annotatedWith(Names.named("androidSendEndPoint")).toInstance("http://localhost:7070/notification.do?action=send");
        }
    }
}
