package com.jd.blockchain.ledger;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.jd.blockchain.crypto.AddressEncoding;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.io.ByteArray;
import com.jd.blockchain.utils.io.BytesEncoding;
import com.jd.blockchain.utils.io.BytesReader;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.io.BytesWriter;
import com.jd.blockchain.utils.io.RuntimeIOException;

/**
 * 区块链身份；
 * 
 * @author huanghaiquan
 *
 */
public class BlockchainIdentityData implements BytesWriter, BytesReader, Externalizable, BlockchainIdentity {

	private Bytes address;

	private PubKey pubKey;

	private BlockchainIdentityData() {
	}

	public BlockchainIdentityData(PubKey pubKey) {
		this.pubKey = pubKey;
		this.address = AddressEncoding.generateAddress(pubKey);
	}

	public BlockchainIdentityData(CryptoAlgorithm algorithm, ByteArray pubKeyBytes) {
		this.pubKey = new PubKey(algorithm, pubKeyBytes.bytes());
		this.address = AddressEncoding.generateAddress(pubKey);
	}

	public BlockchainIdentityData(Bytes address, PubKey pubKey) {
		if (!verifyAddress(address, pubKey)) {
			throw new IllegalArgumentException("Blockchain address is mismatch with the pub-key!");
		}
		this.address = address;
		this.pubKey = pubKey;
	}

	public static boolean verifyAddress(Bytes address, PubKey pubKey) {
		Bytes addr = AddressEncoding.generateAddress(pubKey);
		return addr.equals(address);
	}

	@Override
	public void resolvFrom(InputStream in) throws IOException {
		Bytes addr = AddressEncoding.readAddress(in);
		byte[] value = BytesEncoding.readInShort(in);
		PubKey pk = new PubKey(value);
		this.address = addr;
		this.pubKey = pk;
	}

	@Override
	public void writeTo(OutputStream out) throws IOException {
		AddressEncoding.writeAddress(address, out);
		BytesEncoding.writeInShort(pubKey.toBytes(), out);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jd.blockchain.ledger.BlockchainIdentity#getAddress()
	 */
	@Override
	public Bytes getAddress() {
		return address;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.jd.blockchain.ledger.BlockchainIdentity#getPubKey()
	 */
	@Override
	public PubKey getPubKey() {
		return pubKey;
	}

	public static BlockchainIdentity resolveFrom(ByteArray bytes) {
		try {
			BlockchainIdentityData id = new BlockchainIdentityData();
			id.resolvFrom(bytes.asInputStream());
			return id;
		} catch (IOException e) {
			throw new RuntimeIOException(e.getMessage(), e);
		}
	}

	public static ByteArray toBytes(List<BlockchainIdentityData> identities) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			BytesUtils.writeInt(identities.size(), out);
			for (BlockchainIdentityData identity : identities) {
				identity.writeTo(out);
			}

			return ByteArray.wrap(out.toByteArray());
		} catch (IOException e) {
			throw new RuntimeIOException(e.getMessage(), e);
		}
	}

	public static List<BlockchainIdentityData> resolveIdentitiesFrom(ByteArray bytes) {
		try {
			InputStream in = bytes.asInputStream();
			int identitiesLen = BytesUtils.readInt(in);
			List<BlockchainIdentityData> identities = new ArrayList<>();
			for (int i = 0; i < identitiesLen; i++) {
				BlockchainIdentityData id = new BlockchainIdentityData();
				id.resolvFrom(in);

				identities.add(id);
			}

			return identities;
		} catch (IOException e) {
			throw new RuntimeIOException(e.getMessage(), e);
		}
	}

	public ByteArray toBytes() {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			writeTo(out);
			return ByteArray.wrap(out.toByteArray());
		} catch (IOException e) {
			throw new RuntimeIOException(e.getMessage(), e);
		}
	}

	@Override
	public int hashCode() {
		return address.hashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof BlockchainIdentityData)) {
			return false;
		}

		BlockchainIdentity identity = (BlockchainIdentity) other;

		if (!getAddress().equals(identity.getAddress())) {
			return false;
		}
		return pubKey.equals(identity.getPubKey());
	}

	/**
	 * The object implements the writeExternal method to save its contents by
	 * calling the methods of DataOutput for its primitive values or calling the
	 * writeObject method of ObjectOutput for objects, strings, and arrays.
	 *
	 * @param out
	 *            the stream to write the object to
	 * @throws IOException
	 *             Includes any I/O exceptions that may occur
	 * @serialData Overriding methods should use this tag to describe the data
	 *             layout of this Externalizable object. List the sequence of
	 *             element types and, if possible, relate the element to a
	 *             public/protected field and/or method of this Externalizable
	 *             class.
	 */
	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		writeTo(os);
		byte[] bts = os.toByteArray();
		out.writeInt(bts.length);
		out.write(bts);
	}

	/**
	 * The object implements the readExternal method to restore its contents by
	 * calling the methods of DataInput for primitive types and readObject for
	 * objects, strings and arrays. The readExternal method must read the values in
	 * the same sequence and with the same types as were written by writeExternal.
	 *
	 * @param in
	 *            the stream to read data from in order to restore the object
	 * @throws IOException
	 *             if I/O errors occur
	 * @throws ClassNotFoundException
	 *             If the class for an object being restored cannot be found.
	 */
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		int len = in.readInt();
		byte[] bts = new byte[len];
		in.readFully(bts);
		this.resolvFrom(new ByteArrayInputStream(bts));
	}
}
