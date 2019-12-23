//package test.com.jd.blockchain.peer.web;
//
//import org.springframework.boot.test.mocker.mockito.MockBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import com.jd.blockchain.peer.PeerSettings;
//import com.jd.blockchain.peer.service.MessageBroadcaster;
//import com.jd.blockchain.peer.service.PeerKeyStorageService;
//
//@Configuration
//public class ControllerTestConfiguration {
//
//    @Bean
//    public PeerKeyStorageService peerKeyStorageService() {
//        return new PeerKeyStorageServiceImpl();
//    }
//
////    @MockBean
////    private LedgerService ledgerService;
//
//    @MockBean
//    private MessageBroadcaster msgBroadcaster; // 用于向客户端进行消息通知；
//    
//
//    @Bean
//    public PeerSettings peerSettring() {
//        PeerSettings setting = new PeerSettings();
//        PeerSettings.ConsensusSetting consensusSetting = new PeerSettings.ConsensusSetting();
//        consensusSetting.setIp("127.0.0.1");
//        consensusSetting.setPort(9000);
//        setting.setConsensus(consensusSetting);
//
//        return setting;
//    }
//
//}
