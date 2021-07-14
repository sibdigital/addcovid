package ru.sibdigital.addcovid.queue;

import ru.sibdigital.addcovid.cms.VerifiedData;
import ru.yoomoney.tech.dbqueue.api.EnqueueParams;
import ru.yoomoney.tech.dbqueue.api.QueueProducer;
import ru.yoomoney.tech.dbqueue.api.TaskPayloadTransformer;
import ru.yoomoney.tech.dbqueue.config.QueueShard;
import ru.yoomoney.tech.dbqueue.settings.QueueConfig;
import ru.yoomoney.tech.dbqueue.spring.dao.SpringDatabaseAccessLayer;

public class VerifyQueueProducer implements QueueProducer<VerifiedData> {

    private final QueueConfig queueConfig;

    private final QueueShard<SpringDatabaseAccessLayer> queueShard;

    public VerifyQueueProducer( QueueConfig queueConfig,  QueueShard<SpringDatabaseAccessLayer> queueShard) {
        this.queueConfig = queueConfig;
        this.queueShard = queueShard;
    }

    @Override
    public long enqueue(EnqueueParams<VerifiedData> enqueueParams) {
        EnqueueParams<String> rawEnqueueParams = new EnqueueParams<String>()
                .withPayload(getPayloadTransformer().fromObject(enqueueParams.getPayload()))
                .withExecutionDelay(enqueueParams.getExecutionDelay())
                .withExtData(enqueueParams.getExtData());
        return queueShard.getDatabaseAccessLayer().transact(() ->
                queueShard.getDatabaseAccessLayer().getQueueDao().enqueue(queueConfig.getLocation(), rawEnqueueParams));
    }

    @Override
    public TaskPayloadTransformer<VerifiedData> getPayloadTransformer() {
        return new VerifyTaskPayloadTransformer();
    }
}
