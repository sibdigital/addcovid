package ru.sibdigital.addcovid.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sibdigital.addcovid.cms.VerifiedData;
import ru.yoomoney.tech.dbqueue.api.*;
import ru.yoomoney.tech.dbqueue.settings.QueueConfig;

public class VerifyQueueConsumer implements QueueConsumer<VerifiedData> {

    private final static Logger verificationLog = LoggerFactory.getLogger("VerificationLogger");

    private final QueueConfig queueConfig;

    public VerifyQueueConsumer(QueueConfig queueConfig) {
        this.queueConfig = queueConfig;
    }

    @Override
    public TaskExecutionResult execute(Task<VerifiedData> task) {
        verificationLog.info("payload={}", task.getPayloadOrThrow());
        return TaskExecutionResult.finish();
    }

    @Override
    public QueueConfig getQueueConfig() {
        return queueConfig;
    }

    @Override
    public TaskPayloadTransformer<VerifiedData> getPayloadTransformer() {
        return null;
    }
}
