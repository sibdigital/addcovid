package ru.sibdigital.addcovid.service.queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sibdigital.addcovid.cms.VerifiedData;
import ru.sibdigital.addcovid.queue.VerifyQueueConsumer;
import ru.sibdigital.addcovid.queue.VerifyQueueProducer;
import ru.yoomoney.tech.dbqueue.api.EnqueueParams;
import ru.yoomoney.tech.dbqueue.config.QueueService;
import ru.yoomoney.tech.dbqueue.config.QueueShard;
import ru.yoomoney.tech.dbqueue.config.QueueShardId;
import ru.yoomoney.tech.dbqueue.config.impl.NoopTaskLifecycleListener;
import ru.yoomoney.tech.dbqueue.config.impl.NoopThreadLifecycleListener;
import ru.yoomoney.tech.dbqueue.settings.QueueConfig;
import ru.yoomoney.tech.dbqueue.spring.dao.SpringDatabaseAccessLayer;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Service
public class CustomQueueServiceImpl implements CustomQueueService {
    @Autowired
    QueueConfig queueConfig;

    @Autowired
    QueueShard queueShard;

    @Autowired
    VerifyQueueProducer verifyQueueProducer;

    @Autowired
    VerifyQueueConsumer verifyQueueConsumer;

    @Autowired
    QueueService queueService;

    @Override
    public void testMethod(List<VerifiedData> verifiedDataList) {
        verifiedDataList.forEach(verifiedData -> verifyQueueProducer.enqueue(EnqueueParams.create(verifiedData)));
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        queueService.shutdown();
        queueService.awaitTermination(Duration.ofSeconds(10));
    }
}
