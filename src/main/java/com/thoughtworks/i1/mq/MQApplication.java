package com.thoughtworks.i1.mq;

import com.google.common.collect.ImmutableList;
import com.google.inject.Module;
import com.thoughtworks.i1.commons.I1Application;
import com.thoughtworks.i1.commons.config.Configuration;
import com.thoughtworks.i1.commons.config.DatabaseConfiguration;
import com.thoughtworks.i1.mq.jms.JmsModule;

import java.util.Collection;

public class MQApplication extends I1Application{
    @Override
    protected Configuration defaultConfiguration() {
        return Configuration.config()
                .app().contextPath("/schedule").end()
                .http().port(8052).end()
                .database().persistUnit("domain").with(DatabaseConfiguration.H2.driver, DatabaseConfiguration.H2.tempFileDB, DatabaseConfiguration.H2.compatible("ORACLE"), DatabaseConfiguration.Hibernate.dialect("Oracle10g"), DatabaseConfiguration.Hibernate.showSql).user("sa").password("").end()
                .build();
    }

    @Override
    protected Collection<? extends Module> getCustomizedModules() {
        Module module = new JmsModule.Builder().usingJNDI().queue("queue/ex").buildModule();
        return ImmutableList.of(module);
    }


    public static void main(String[] args) throws Exception {
        new MQApplication().start(true);
    }
}
