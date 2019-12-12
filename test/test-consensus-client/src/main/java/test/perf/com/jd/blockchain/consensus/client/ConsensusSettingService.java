package test.perf.com.jd.blockchain.consensus.client;

import com.jd.blockchain.utils.http.HttpAction;
import com.jd.blockchain.utils.http.HttpMethod;
import com.jd.blockchain.utils.http.HttpService;

/**
 * Created by zhangshuang3 on 2018/9/11.
 */
@HttpService
public interface ConsensusSettingService {

    @HttpAction(path = "/node/settings", method = HttpMethod.GET)
    public String getConsensusSettingsHex();

    @HttpAction(path = "/node/topology", method = HttpMethod.GET)
    public String getConsensusTopologyHex();

}

