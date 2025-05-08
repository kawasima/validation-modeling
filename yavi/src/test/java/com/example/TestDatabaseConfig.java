package com.example;

import org.flywaydb.core.Flyway;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DataSourceConnectionProvider;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.impl.ThreadLocalTransactionProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class TestDatabaseConfig {
    @Bean
    public DataSource embeddedDatabase() {
        return new EmbeddedDatabaseBuilder()
                .setName("test;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE")
                .setType(EmbeddedDatabaseType.H2)
                .build();
    }
    @Bean
    public DataSourceConnectionProvider connectionProvider(DataSource ds) {
        return new DataSourceConnectionProvider(
                new TransactionAwareDataSourceProxy(ds));
    }
    @Bean
    public DefaultConfiguration jooqConfiguration(DataSourceConnectionProvider connectionProvider) {
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(connectionProvider.dataSource());
        DefaultConfiguration config = new DefaultConfiguration();
        config.set(connectionProvider);
        config.set(SQLDialect.H2);
        config.set(new ThreadLocalTransactionProvider(connectionProvider));
        return config;
    }
    @Bean
    public DSLContext dslContext(DefaultConfiguration jooqConfiguration) {
        return DSL.using(jooqConfiguration);
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .baselineOnMigrate(true)
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .load();
    }



}
