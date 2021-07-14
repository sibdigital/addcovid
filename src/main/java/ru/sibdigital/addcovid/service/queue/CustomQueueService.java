package ru.sibdigital.addcovid.service.queue;

import ru.sibdigital.addcovid.cms.VerifiedData;

import java.util.List;

public interface CustomQueueService {

    List<Long> enqueueAll(List<VerifiedData> verifiedData);

    Long enqueue(VerifiedData verifiedData);
}
