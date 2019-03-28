package test.com.jd.blockchain.binaryproto.contract.impl;

import test.com.jd.blockchain.binaryproto.contract.*;

/**
 * Created by zhangshuang3 on 2018/7/11.
 */
public class ArrayImpl implements Array {

    private byte[] familyMemberAges;
    private int[] scores;
    private String[] features;
    private long[] familyMemberIds;

    @Override
    public int[] getScores() {
        // TODO Auto-generated method stub
        return this.scores;
    }
    public void setScores(int[] scores) {
        this.scores = scores;
    }

    @Override
    public String[] getFeatures() {
        return this.features;
    }
    public void setFeatures(String[] features) {
        this.features = features;
    }

    @Override
    public byte[] getFamilyMemberAges() {
        return this.familyMemberAges;
    }
    public void setFamilyMemberAges(byte[] familyMemberAge) {
        this.familyMemberAges = familyMemberAge;
    }

    @Override
    public long[] getFamilyMemberIds() {
        return this.familyMemberIds;
    }
    public void setFamilyMemberIds(long[] familyMemberId) {
        this.familyMemberIds = familyMemberId;
    }
}
