package aforo255.ms.test.pay.service;

import aforo255.ms.test.pay.domain.Operation;

public interface IOperationService {

    public Iterable<Operation> findAll();

    public Operation findById(Integer id);

    public Operation save(Operation operation);
}