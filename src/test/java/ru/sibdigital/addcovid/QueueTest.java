package ru.sibdigital.addcovid;

import org.junit.Assert;
import org.junit.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.junit.runner.RunWith;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import ru.sibdigital.addcovid.config.ApplicationConstants;
import ru.yoomoney.tech.dbqueue.api.EnqueueParams;
import ru.yoomoney.tech.dbqueue.api.Task;
import ru.yoomoney.tech.dbqueue.api.TaskExecutionResult;
import ru.yoomoney.tech.dbqueue.config.*;
import ru.yoomoney.tech.dbqueue.config.impl.NoopTaskLifecycleListener;
import ru.yoomoney.tech.dbqueue.config.impl.NoopThreadLifecycleListener;
import ru.yoomoney.tech.dbqueue.settings.QueueConfig;
import ru.yoomoney.tech.dbqueue.settings.QueueId;
import ru.yoomoney.tech.dbqueue.settings.QueueLocation;
import ru.yoomoney.tech.dbqueue.settings.QueueSettings;
import ru.yoomoney.tech.dbqueue.spring.dao.SpringDatabaseAccessLayer;

import javax.annotation.Nonnull;
import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

@SpringBootTest(classes = AddcovidApplication.class)
public class QueueTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private TransactionTemplate transactionTemplate;


    @Test
    public void example_config() throws InterruptedException {
        AtomicBoolean isTaskConsumed = new AtomicBoolean(false);
        transactionTemplate = new TransactionTemplate(transactionManager);
        final QueueTableSchema.Builder builder = QueueTableSchema.builder();
        final QueueTableSchema build = builder.build();
        SpringDatabaseAccessLayer databaseAccessLayer = new SpringDatabaseAccessLayer(
                DatabaseDialect.POSTGRESQL, build,
                jdbcTemplate,
                transactionTemplate);
        QueueShard<SpringDatabaseAccessLayer> shard = new QueueShard<>(new QueueShardId("verify_shard"), databaseAccessLayer);

        QueueConfig config = new QueueConfig(QueueLocation.builder().withTableName("reg_queue_tasks")
                .withQueueId(new QueueId("verify_shard")).build(),
                QueueSettings.builder()
                        .withBetweenTaskTimeout(Duration.ofMillis(100))
                        .withNoTaskTimeout(Duration.ofMillis(100))
                        .build());

        StringQueueProducer producer = new StringQueueProducer(config, shard);
        StringQueueConsumer consumer = new StringQueueConsumer(config) {
            @Override
            public TaskExecutionResult execute(@Nonnull Task<String> task) {
//                log.info("payload={}", task.getPayloadOrThrow());
                isTaskConsumed.set(true);
                return TaskExecutionResult.finish();
            }
        };

        QueueService queueService = new QueueService(Collections.singletonList(shard),
                NoopThreadLifecycleListener.getInstance(), NoopTaskLifecycleListener.getInstance());
        queueService.registerQueue(consumer);
        queueService.start();
        producer.enqueue(EnqueueParams.create("example task"));
        Thread.sleep(1000);
        queueService.shutdown();
        queueService.awaitTermination(Duration.ofSeconds(10));
        Assert.assertTrue(isTaskConsumed.get());
    }
}
