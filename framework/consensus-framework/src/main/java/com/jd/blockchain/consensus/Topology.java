package com.jd.blockchain.consensus;

import java.io.Serializable;

public interface Topology extends Serializable {
    int getId();

    Topology copyOf();
}
