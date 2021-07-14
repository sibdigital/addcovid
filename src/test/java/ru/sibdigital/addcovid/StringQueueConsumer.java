package ru.sibdigital.addcovid;

import ru.yoomoney.tech.dbqueue.api.QueueConsumer;
import ru.yoomoney.tech.dbqueue.api.TaskPayloadTransformer;
import ru.yoomoney.tech.dbqueue.api.impl.NoopPayloadTransformer;
import ru.yoomoney.tech.dbqueue.settings.QueueConfig;


import static java.util.Objects.requireNonNull;

/**
 * Queue consumer without payload transformation
 *
 * @author Oleg Kandaurov
 * @since 02.10.2019
 */
public abstract class StringQueueConsumer implements QueueConsumer<String> {

    private final QueueConfig queueConfig;

    public StringQueueConsumer(QueueConfig queueConfig) {
        this.queueConfig = requireNonNull(queueConfig);
    }


    @Override
    public QueueConfig getQueueConfig() {
        return queueConfig;
    }


    @Override
    public TaskPayloadTransformer<String> getPayloadTransformer() {
        return NoopPayloadTransformer.getInstance();
    }

}