package ru.sibdigital.addcovid.config.queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import ru.sibdigital.addcovid.queue.VerifyQueueConsumer;
import ru.sibdigital.addcovid.queue.VerifyQueueProducer;
import ru.yoomoney.tech.dbqueue.config.*;
import ru.yoomoney.tech.dbqueue.config.impl.NoopTaskLifecycleListener;
import ru.yoomoney.tech.dbqueue.config.impl.NoopThreadLifecycleListener;
import ru.yoomoney.tech.dbqueue.settings.QueueConfig;
import ru.yoomoney.tech.dbqueue.settings.QueueId;
import ru.yoomoney.tech.dbqueue.settings.QueueLocation;
import ru.yoomoney.tech.dbqueue.settings.QueueSettings;
import ru.yoomoney.tech.dbqueue.spring.dao.SpringDatabaseAccessLayer;

import java.time.Duration;
import java.util.Collections;

@Configuration
@Slf4j
public class CustomQueueConfig {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private TransactionTemplate transactionTemplate;

    @Bean
    QueueConfig queueConfig() {
        log.info("queue config create ");
        return new QueueConfig(QueueLocation.builder().withTableName("reg_queue_tasks")
                        .withQueueId(new QueueId("verify_queue")).build(),
                            QueueSettings.builder()
                            .withBetweenTaskTimeout(Duration.ofMillis(100))
                            .withNoTaskTimeout(Duration.ofMillis(100))
                            .build());
    }

    @Bean
    QueueShard queueShard() {
        log.info("queue shard create ");
        transactionTemplate = new TransactionTemplate(transactionManager);
        final QueueTableSchema.Builder builder = QueueTableSchema.builder();
        final QueueTableSchema build = builder.build();
        SpringDatabaseAccessLayer databaseAccessLayer = new SpringDatabaseAccessLayer(
                DatabaseDialect.POSTGRESQL, build,
                jdbcTemplate,
                transactionTemplate);
        QueueShard<SpringDatabaseAccessLayer> shard = new QueueShard<>(new QueueShardId("verify_shard"), databaseAccessLayer);
        return shard;
    }

    @Bean
    VerifyQueueProducer verifyQueueProducer(QueueConfig queueConfig, QueueShard queueShard) {
        log.info("queue producer create ");
        return new VerifyQueueProducer(queueConfig, queueShard);
    }

    @Bean
    VerifyQueueConsumer verifyQueueConsumer(QueueConfig queueConfig) {
        log.info("queue consumer create ");
        return new VerifyQueueConsumer(queueConfig);
    }

    @Bean
    QueueService queueService(){
        log.info("queue service create ");
        QueueService queueService = new ru.yoomoney.tech.dbqueue.config.QueueService(Collections.singletonList(queueShard()),
                NoopThreadLifecycleListener.getInstance(), NoopTaskLifecycleListener.getInstance());
        queueService.registerQueue(verifyQueueConsumer(queueConfig()));
        queueService.start();
        return queueService;
    }

}
