//package com.jd.blockchain.binaryproto;
//
//import java.io.ByteArrayOutputStream;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import my.utils.io.BytesUtils;
//
///**
// * 二进制数据段的头部；
// * 
// * @author huanghaiquan
// *
// */
//public class BinarySegmentHeader {
//	
//	public static final BinarySliceSpec CODE_SLICE_SPEC = new BinarySliceSpec(4, false, "Code", "Data Contract Code");
//	
//	public static final BinarySliceSpec VERSION_SLICE_SPEC = new BinarySliceSpec(8, false, "Version", "Data Contract Version");
//	
//	public static final List<BinarySliceSpec> HEADER_SLICES = Collections.unmodifiableList(Arrays.asList(CODE_SLICE_SPEC, VERSION_SLICE_SPEC));
//	
//	private int code;
//	
//	private long version;
//	
//	public int getCode() {
//		return code;
//	}
//	
//	public long getVersion() {
//		return version;
//	}
//	
//	public BinarySegmentHeader(int code, long version) {
//		this.code = code;
//		this.version = version;
//	}
//	
//	public static int resolveCode(InputStream in) {
//		return BytesUtils.readInt(in);
//	}
//	
//	
//	public static long resolveVersion(InputStream in) {
//		return BytesUtils.readLong(in);
//	}
//	
//	public static BinarySegmentHeader resolveFrom(InputStream in) {
//		int code = resolveCode(in);
//		long version = resolveVersion(in);
//		return new BinarySegmentHeader(code, version);
//	}
//	
//	public static void writeCode(int code, OutputStream out) {
//		BytesUtils.writeInt(code, out);
//	}
//	
//	public static void writeVersion(long version, OutputStream out) {
//		BytesUtils.writeLong(version, out);
//	}
//
//	public void writeTo(OutputStream out) {
//		writeCode(code, out);
//		writeVersion(version, out);
//	}
//
//
//	public byte[] toBytes() {
//		ByteArrayOutputStream out =new ByteArrayOutputStream();
//		writeTo(out);
//		return out.toByteArray();
//	}
//	
//	
//	
//}
