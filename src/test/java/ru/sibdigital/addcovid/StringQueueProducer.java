package ru.sibdigital.addcovid;

import ru.yoomoney.tech.dbqueue.api.EnqueueParams;
import ru.yoomoney.tech.dbqueue.api.QueueProducer;
import ru.yoomoney.tech.dbqueue.api.TaskPayloadTransformer;
import ru.yoomoney.tech.dbqueue.api.impl.NoopPayloadTransformer;
import ru.yoomoney.tech.dbqueue.config.QueueShard;
import ru.yoomoney.tech.dbqueue.settings.QueueConfig;
import ru.yoomoney.tech.dbqueue.spring.dao.SpringDatabaseAccessLayer;

import static java.util.Objects.requireNonNull;

/**
 * Queue producer without sharding and payload transformation
 *
 * @author Oleg Kandaurov
 * @since 02.10.2019
 */
public class StringQueueProducer implements QueueProducer<String> {


    private final QueueConfig queueConfig;

    private final QueueShard<SpringDatabaseAccessLayer> queueShard;

    public StringQueueProducer(QueueConfig queueConfig,
                               QueueShard<SpringDatabaseAccessLayer> queueShard) {
        this.queueConfig = requireNonNull(queueConfig, "queueConfig");
        this.queueShard = requireNonNull(queueShard, "queueShard");
    }

    @Override
    public long enqueue(EnqueueParams<String> enqueueParams) {
        requireNonNull(enqueueParams);
        EnqueueParams<String> rawEnqueueParams = new EnqueueParams<String>()
                .withPayload(getPayloadTransformer().fromObject(enqueueParams.getPayload()))
                .withExecutionDelay(enqueueParams.getExecutionDelay())
                .withExtData(enqueueParams.getExtData());
        return requireNonNull(queueShard.getDatabaseAccessLayer().transact(() ->
                queueShard.getDatabaseAccessLayer().getQueueDao().enqueue(queueConfig.getLocation(), rawEnqueueParams)));
    }

    @Override
    public TaskPayloadTransformer<String> getPayloadTransformer() {
        return NoopPayloadTransformer.getInstance();
    }

}