package com.jd.blockchain.consensus.bftsmart.client;

import com.jd.blockchain.consensus.client.ClientSettings;


public interface BftsmartClientSettings extends ClientSettings {

    byte[] getTopology();

    byte[] getTomConfig();

}
