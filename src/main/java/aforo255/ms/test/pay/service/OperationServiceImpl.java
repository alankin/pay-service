package aforo255.ms.test.pay.service;

import aforo255.ms.test.pay.dao.OperationDao;
import aforo255.ms.test.pay.domain.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OperationServiceImpl implements IOperationService {

    @Autowired
    private OperationDao operationDao;

    @Override
    @Transactional(readOnly = true)
    public Operation findById(Integer id) {
        return operationDao.findById(id).orElse(null);
    }

    @Override
    public Operation save(Operation operation) {
        return operationDao.save(operation);
    }

    @Override
    public Iterable<Operation> findAll() {
        return operationDao.findAll();
    }
}
