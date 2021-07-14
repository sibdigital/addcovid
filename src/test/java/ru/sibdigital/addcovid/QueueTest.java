package ru.sibdigital.addcovid;

import org.junit.Test;
import org.junit.runner.RunWith;
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

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = AddcovidApplication.class)
@ContextConfiguration(
        classes = { ApplicationConstants.class}//,
        //initializers = {ConfigFileApplicationContextInitializer.class}
        )
@TestPropertySource(locations = "../../../constants.yml")
//properties = { "spring.config.location=classpath:constants.yml" }
public class QueueTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PlatformTransactionManager transactionManager;

    private TransactionTemplate transactionTemplate;

    @Test
    public void enqueue() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName("org.postgresql.Driver");
//        dataSource.setUrl("jdbc:postgresql://localhost:5432/cov_prod_copy");
//        dataSource.setUsername("postgres");
//        dataSource.setPassword("postgres");
//
//        jdbcTemplate = new JdbcTemplate();
//        jdbcTemplate.setDataSource(dataSource);
//
//        transactionManager = new DataSourceTransactionManager();

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
                .withQueueId(new QueueId("verify_queue")).build(),
                QueueSettings.builder()
                        .withBetweenTaskTimeout(Duration.ofMillis(100))
                        .withNoTaskTimeout(Duration.ofMillis(100))
                        .build());

        StringQueueProducer producer = new StringQueueProducer(config, shard);
        StringQueueConsumer consumer = new StringQueueConsumer(config) {

            @Override
            public TaskExecutionResult execute(Task<String> task) {
                //log.info("payload={}", task.getPayloadOrThrow());
                isTaskConsumed.set(true);
                return TaskExecutionResult.finish();
            }
        };

        QueueService queueService = new QueueService(Collections.singletonList(shard),
                NoopThreadLifecycleListener.getInstance(), NoopTaskLifecycleListener.getInstance());
        queueService.registerQueue(consumer);
        queueService.start();
        producer.enqueue(EnqueueParams.create("example task"));
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        queueService.shutdown();
        queueService.awaitTermination(Duration.ofSeconds(10));
        //Assert.assertTrue(isTaskConsumed.get());
    }
}
