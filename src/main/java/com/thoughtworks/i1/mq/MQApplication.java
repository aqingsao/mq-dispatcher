package com.thoughtworks.i1.mq;

import com.google.common.collect.ImmutableList;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import com.thoughtworks.i1.commons.I1Application;
import com.thoughtworks.i1.commons.config.Configuration;
import com.thoughtworks.i1.commons.config.DatabaseConfiguration;
import com.thoughtworks.i1.mq.jms.QueueReceiver;
import com.thoughtworks.i1.mq.service.DeviceService;
import com.thoughtworks.i1.mq.service.DispatchService;
import com.thoughtworks.i1.mq.service.MessageService;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.util.Collection;

public class MQApplication extends I1Application {
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
        return ImmutableList.of(new MQModule());
    }

    public static void main(String[] args) throws Exception {
        new MQApplication().start(true);
    }

    public static class MQModule extends AbstractModule {
        @Override
        protected void configure() {
            bind(MessageService.class).in(Scopes.SINGLETON);
            bind(DispatchService.class).in(Scopes.SINGLETON);
            bind(DeviceService.class).in(Scopes.SINGLETON);

            String brokerUrl = "tcp://localhost:61618";
            String queue = "TEST.FOO";

            QueueReceiver queueReceiver = new QueueReceiver.QueueBuilder(new ActiveMQConnectionFactory(brokerUrl), queue).transacted().build();
            bind(QueueReceiver.class).annotatedWith(Names.named(queue)).toInstance(queueReceiver);

        }
    }
}
