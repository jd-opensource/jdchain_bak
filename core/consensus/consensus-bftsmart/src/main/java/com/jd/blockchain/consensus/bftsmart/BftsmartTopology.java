package com.jd.blockchain.consensus.bftsmart;

import bftsmart.reconfiguration.views.View;
import com.jd.blockchain.consensus.Topology;
import com.jd.blockchain.utils.serialize.binary.BinarySerializeUtils;

public class BftsmartTopology implements Topology {

    private static final long serialVersionUID = -3042599438265726240L;

    private View view;

    public BftsmartTopology(View view){
        this.view = view;
    }

    @Override
    public int getId() {
        return view.getId();
    }

    @Override
    public Topology copyOf() {
        return BinarySerializeUtils.copyOf(this);
    }

    public View getView() {
        return view;
    }
}
