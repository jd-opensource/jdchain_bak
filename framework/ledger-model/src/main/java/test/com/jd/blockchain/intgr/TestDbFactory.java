package test.com.jd.blockchain.intgr;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.util.concurrent.atomic.AtomicBoolean;

import com.jd.blockchain.storage.service.*;
import com.jd.blockchain.storage.service.utils.MemoryKVStorage;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class TestDbFactory implements DbConnectionFactory{
	
	private DbConnectionFactory innerDbFactory;
	
	private AtomicBoolean errorSetTurnOn = new AtomicBoolean(false);
	
	public TestDbFactory(DbConnectionFactory innerDbFactory) {
		this.innerDbFactory = innerDbFactory;
	}


	Answer<ExPolicyKVStorage> exKVStorageMockedAnswer = new Answer<ExPolicyKVStorage>() {

		@Override
		public ExPolicyKVStorage answer(InvocationOnMock invocation) throws Throwable {

			ExPolicyKVStorage reallyExKVStorage = (ExPolicyKVStorage) invocation.callRealMethod();

			ExPolicyKVStorage mockExKVStorage = Mockito.spy(reallyExKVStorage);

			//按条件开关触发异常；
			doAnswer(new Answer<Boolean>() {

				@Override
				public Boolean answer(InvocationOnMock invocation) throws Throwable {
					if (isErrorSetTurnOn()) {
						return false;
					}
					return (Boolean) invocation.callRealMethod();
				}
			}).when(mockExKVStorage).set(any(), any(), any());

			return mockExKVStorage;
		}
	};

	Answer<VersioningKVStorage> verKVStorageMockedAnswer = new Answer<VersioningKVStorage>() {

		@Override
		public VersioningKVStorage answer(InvocationOnMock invocation) throws Throwable {

			VersioningKVStorage reallyVerKVStorage = (VersioningKVStorage) invocation.callRealMethod();

			VersioningKVStorage mockVerKVStorage = Mockito.spy(reallyVerKVStorage);

			//按条件开关触发异常；
			doAnswer(new Answer<Long>() {

				@Override
				public Long answer(InvocationOnMock invocation) throws Throwable {
					if (isErrorSetTurnOn()) {
						return (long)(-1);
					}
					return (Long) invocation.callRealMethod();
				}
			}).when(mockVerKVStorage).set(any(), any(), anyLong());

			return mockVerKVStorage;
		}
	};

	Answer<KVStorageService> storageMockedAnswer = new Answer<KVStorageService>() {

		@Override
		public KVStorageService answer(InvocationOnMock invocation) throws Throwable {

			KVStorageService reallyStorage = (KVStorageService) invocation.callRealMethod();

			TestMemoryKVStorage testMemoryKVStorage = new TestMemoryKVStorage((MemoryKVStorage)reallyStorage);

			KVStorageService mockedStorage = Mockito.spy(testMemoryKVStorage);

			doAnswer(exKVStorageMockedAnswer).when(mockedStorage).getExPolicyKVStorage();

			doAnswer(verKVStorageMockedAnswer).when(mockedStorage).getVersioningKVStorage();

			return mockedStorage;
		}

	};


	@Override
	public String dbPrefix() {
		return innerDbFactory.dbPrefix();
	}

	@Override
	public boolean support(String scheme) {
		return innerDbFactory.support(scheme);
	}

	@Override
	public DbConnection connect(String dbConnectionString) {
		
		DbConnection reallyDbConn = innerDbFactory.connect(dbConnectionString);
		
		DbConnection mockDbConn = Mockito.spy(reallyDbConn);

		when(mockDbConn.getStorageService()).then(storageMockedAnswer);
		return mockDbConn;
	}

	@Override
	public DbConnection connect(String dbConnectionString, String password) {
		return connect(dbConnectionString);
	}

	@Override
	public void close() {
		innerDbFactory.close();
		
	}

	public boolean isErrorSetTurnOn() {
		return errorSetTurnOn.get();
	}

	public void setErrorSetTurnOn(boolean errorSetTurnOn) {
		this.errorSetTurnOn.set(errorSetTurnOn);;
	}
	
	

}
