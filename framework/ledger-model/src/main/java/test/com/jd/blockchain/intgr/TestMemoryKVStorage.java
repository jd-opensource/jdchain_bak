package test.com.jd.blockchain.intgr;

import com.jd.blockchain.storage.service.ExPolicyKVStorage;
import com.jd.blockchain.storage.service.VersioningKVStorage;
import com.jd.blockchain.storage.service.utils.MemoryKVStorage;

public class TestMemoryKVStorage extends MemoryKVStorage {

    private MemoryKVStorage memoryKVStorage;

    public TestMemoryKVStorage(MemoryKVStorage memoryKVStorage) {
        this.memoryKVStorage = memoryKVStorage;
    }

    @Override
    public ExPolicyKVStorage getExPolicyKVStorage() {
        return memoryKVStorage;
    }

    @Override
    public VersioningKVStorage getVersioningKVStorage() {
        return memoryKVStorage;
    }
}
