//package com.jd.blockchain.binaryproto.impl;
//
//import my.utils.ValueType;
//import my.utils.io.*;
//import my.utils.net.NetworkAddress;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Constructor;
//import java.lang.reflect.Field;
//import java.lang.reflect.InvocationTargetException;
//import java.lang.reflect.Method;
//import java.util.*;
//
//import com.jd.blockchain.binaryproto.BinaryEncoder;
//import com.jd.blockchain.binaryproto.ContractTypeResolver;
//import com.jd.blockchain.binaryproto.DConstructor;
//import com.jd.blockchain.binaryproto.DataContractException;
//import com.jd.blockchain.binaryproto.DataContractRegistry;
//import com.jd.blockchain.binaryproto.DataSpecification;
//import com.jd.blockchain.binaryproto.EnumSpecification;
//import com.jd.blockchain.binaryproto.FieldSetter;
//import com.jd.blockchain.binaryproto.FieldSpec;
//
///**
// * Created by zhangshuang3 on 2018/6/21.
// */
//public class BinaryEncoderImpl implements BinaryEncoder {
//	private DataSpecification spec;
//	private Class<?> contractType;
//
//	public BinaryEncoderImpl(DataSpecification spec, Class<?> contractType) {
//		this.spec = spec;
//		this.contractType = contractType;
//	}
//
//	@Override
//	public DataSpecification getSepcification() {
//		return spec;
//	}
//
//	@Override
//	public Class<?> getContractType() {
//		return contractType;
//	}
//
//	public void write(FieldSpec spec, OutputStream out, Object value) {
//		ValueType primitive = spec.getPrimitiveType();
//		EnumSpecification refEnum = spec.getRefEnum();
//		DataSpecification refContract = spec.getRefContract();
//		boolean list = spec.isRepeatable();
//		boolean refPubKey = spec.isRefPubKey();
//		boolean refPrivKey = spec.isRefPrivKey();
//		boolean refHashDigest = spec.isRefHashDigest();
//		boolean refSignatureDigest = spec.isRefSignatureDigest();
//		boolean refIdentity = spec.isRefIdentity();
//		boolean refNetworkAddr = spec.isRefNetworkAddr();
//		Class<?> contractTypeResolverClass = spec.getContractTypeResolver();
//
//		try {
//			// basic data type
//			if ((primitive != ValueType.NIL) && (list == false)) {
//				writePrimitive(value, primitive, out);
//			}
//			// enum type
//			else if (refEnum != null) {
//				writeEnum(value, refEnum, out);
//			}
//			// ref contract
//			else if (refContract != null && list == false) {
//				writeContract(value, refContract, contractTypeResolverClass, out);
//			}
//			// array type
//			else if ((primitive != ValueType.NIL) && (list == true)) {
//				writePrimitiveList(value, primitive, out);
//			}
//			// refcontract array
//			else if (refContract != null && list == true) {
//				writeContractList(value, refContract, contractTypeResolverClass, out);
//			}
//			// HashDigest type | PubKey type | PrivKey type
//			else if (refPubKey == true | refPrivKey == true | refHashDigest == true | refSignatureDigest == true) {
//				writeBytesSerializeObject(value, out);
//			}
//			// BlockChainIdentity type
//			// else if (refIdentity == true) {
//			// if (obj == null) {
//			// BytesEncoding.writeInNormal(null, out);
//			// }
//			// else {
//			// BytesEncoding.writeInNormal(obj.getClass().getName().getBytes(), out);
//			// BytesWriter writer = (BytesWriter)obj;
//			// writer.writeTo(out);
//			// }
//			// }
//			// NetworkAddress type
//			else if (refNetworkAddr == true) {
//				wirteNetworkAddressValue(value, out);
//			} else {
//				throw new IllegalStateException("Unexpected contract field and value!");
//			}
//		} catch (InstantiationException | IllegalAccessException e) {
//			throw new DataContractException(e.getMessage(), e);
//		}
//
//	}
//
//	private void wirteNetworkAddressValue(Object value, OutputStream out) {
//		if (value == null) {
//			BytesEncoding.writeInNormal(null, out);
//		} else {
//			// write host ,port and secure flag
//			NetworkAddress address = (NetworkAddress) value;
//			BytesEncoding.writeInNormal(address.getHost().getBytes(), out);
//			BytesUtils.writeInt(address.getPort(), out);
//			BytesUtils.writeByte((byte) (address.isSecure() ? 1 : 0), out);
//		}
//	}
//
//	private void writeBytesSerializeObject(Object value, OutputStream out) {
//		if (value == null) {
//			BytesEncoding.writeInNormal(null, out);
//		} else {
//			BytesEncoding.writeInNormal(value.getClass().getName().getBytes(), out);
//			BytesSerializable serializable = (BytesSerializable) value;
//			// refPubKey:1bytes algorithm code , 1byte key type, and others are raw bytes
//			// refPrivKey:1bytes algorithm code , 1byte key type, and others are raw bytes
//			// refHashDigest:1bytes algorithm code , and others are raw bytes
//			BytesEncoding.writeInNormal(serializable.toBytes(), out);
//		}
//	}
//
//	private void writeContractList(Object value, DataSpecification contractSpeci, Class<?> contractTypeResolverClass,
//			OutputStream out) throws InstantiationException, IllegalAccessException {
//		BinaryEncoder encoder1 = null;
//		Object[] refContractArray = (Object[]) value;
//		if (refContractArray == null) {
//			BytesUtils.writeInt(0, out);
//		} else {
//			BytesUtils.writeInt(refContractArray.length, out);
//			if (contractTypeResolverClass.isInterface() == true) {
//				encoder1 = DataContractRegistry.getEncoder(contractSpeci.getCode(), contractSpeci.getVersion());
//				if (encoder1 == null) {
//					throw new DataContractException("write: get encoder null error!");
//				}
//				for (Object ref : refContractArray) {
//					BytesUtils.writeInt(encoder1.getSepcification().getCode(), out);
//					BytesUtils.writeLong(encoder1.getSepcification().getVersion(), out);
//					// record class name
//					BytesEncoding.writeInNormal(ref.getClass().getName().getBytes(), out);
//					encoder1.encode(ref, out);
//				}
//			} else {
//				// TODO: 不必每次都实例化，应该对此实例建立单例缓存；
//				ContractTypeResolver resolver = (ContractTypeResolver) (contractTypeResolverClass.newInstance());
//				for (Object ref : refContractArray) {
//					Class<?> subContractType = resolver.getContractType(ref, null);
//					encoder1 = DataContractRegistry.register(subContractType);
//					if (encoder1 == null) {
//						throw new DataContractException("write: regist sub contract type failed error!");
//					}
//					BytesUtils.writeInt(encoder1.getSepcification().getCode(), out);
//					BytesUtils.writeLong(encoder1.getSepcification().getVersion(), out);
//					// record class name
//					BytesEncoding.writeInNormal(ref.getClass().getName().getBytes(), out);
//					encoder1.encode(ref, out);
//					encoder1 = null;
//				}
//			}
//		}
//	}
//
//	private void writePrimitiveList(Object value, ValueType primitive, OutputStream out) {
//		switch (primitive) {
//		case BOOLEAN:
//			if (value == null) {
//				BytesUtils.writeInt(0, out);
//				break;
//			}
//
//			boolean[] boolArray = (boolean[]) value;
//			BytesUtils.writeInt(boolArray.length, out);
//			for (boolean i : boolArray) {
//				BytesUtils.writeByte((byte) (i ? 1 : 0), out);
//			}
//			break;
//		case INT8:
//			if (value == null) {
//				BytesUtils.writeInt(0, out);
//				break;
//			}
//			byte[] byteArray = (byte[]) value;
//			BytesUtils.writeInt(byteArray.length, out);
//			for (byte i : byteArray) {
//				BytesUtils.writeByte(i, out);
//			}
//			break;
//		case INT16:
//			if (value == null) {
//				BytesUtils.writeInt(0, out);
//				break;
//			}
//			short[] shortArray = (short[]) value;
//			BytesUtils.writeInt(shortArray.length, out);
//			for (short i : shortArray) {
//				byte[] bytes = BytesUtils.toBytes(i);
//				BytesEncoding.writeInShort(bytes, out);
//			}
//			break;
//		case INT32:
//			if (value == null) {
//				BytesUtils.writeInt(0, out);
//				break;
//			}
//			int[] intArray = (int[]) value;
//			BytesUtils.writeInt(intArray.length, out);
//			for (int i : intArray) {
//				BytesUtils.writeInt(i, out);
//			}
//			break;
//		case INT64:
//			if (value == null) {
//				BytesUtils.writeInt(0, out);
//				break;
//			}
//			long[] longArray = (long[]) value;
//			BytesUtils.writeInt(longArray.length, out);
//			for (long i : longArray) {
//				BytesUtils.writeLong(i, out);
//			}
//			break;
//		case DATETIME:
//			if (value == null) {
//				BytesUtils.writeInt(0, out);
//				break;
//			}
//			Date[] dateArray = (Date[]) value;
//			BytesUtils.writeInt(dateArray.length, out);
//			long elemSeconds;
//			for (Date i : dateArray) {
//				elemSeconds = i.getTime();
//				BytesUtils.writeLong(elemSeconds, out);
//			}
//			break;
//		case BYTES:
//			if (value == null) {
//				BytesUtils.writeInt(0, out);
//				break;
//			}
//			ByteArray[] byteArrays = (ByteArray[]) value;
//			BytesUtils.writeInt(byteArrays.length, out);
//			for (ByteArray elem : byteArrays) {
//				BytesEncoding.writeInNormal(elem.bytes(), out);
//			}
//			break;
//		case TEXT:
//		case JSON:
//		case XML:
//		case BIG_INT:
//		case IMG:
//		case VIDEO:
//		case LOCATION:
//			if (value == null) {
//				BytesUtils.writeInt(0, out);
//				break;
//			}
//			Object[] dynamicArray = (Object[]) value;
//			BytesUtils.writeInt(dynamicArray.length, out);
//			for (Object i : dynamicArray) {
//				BytesEncoding.writeInNormal(i.toString().getBytes(), out);
//			}
//			break;
//		default:
//			throw new DataContractException("write: array type error!");
//		}
//	}
//
//	private void writeContract(Object value, DataSpecification contractSpeci, Class<?> contractTypeResolverClass,
//			OutputStream out) throws InstantiationException, IllegalAccessException {
//		BinaryEncoder encoder = null;
//		if (contractTypeResolverClass.isInterface() == true) {
//			encoder = DataContractRegistry.getEncoder(contractSpeci.getCode(), contractSpeci.getVersion());
//			if (encoder == null) {
//				throw new DataContractException("write: get encoder null error!");
//			}
//		} else {
//			// get sub contract type
//			ContractTypeResolver resolver = (ContractTypeResolver) (contractTypeResolverClass.newInstance());
//			if (resolver == null) {
//				throw new DataContractException("write: newInstance null error!");
//			}
//			Class<?> subContractType = resolver.getContractType(value, null);
//			encoder = DataContractRegistry.register(subContractType);
//			if (encoder == null) {
//				throw new DataContractException("write: regist sub contract type failed error!");
//			}
//		}
//		BytesUtils.writeInt(encoder.getSepcification().getCode(), out);
//		BytesUtils.writeLong(encoder.getSepcification().getVersion(), out);
//		if (value == null) {
//			BytesEncoding.writeInNormal(null, out);
//		} else {
//			// record class name
//			BytesEncoding.writeInNormal(value.getClass().getName().getBytes(), out);
//			encoder.encode(value, out);
//		}
//	}
//
//	private void writeEnum(Object value, EnumSpecification enumType, OutputStream out) {
//		int code = 0;
//		EnumSpecificationImpl refEnumImpl = (EnumSpecificationImpl) enumType;
//		Map<Object, Integer> constant = refEnumImpl.getEnumConstants();
//		for (Object enumConstant : constant.keySet()) {
//			if (enumConstant.toString().equals(value.toString()) == true) {
//				code = constant.get(enumConstant);
//				break;
//			}
//		}
//		switch (refEnumImpl.getValueType()) {
//		case INT8:
//			BytesUtils.writeByte((byte) (code & 0xff), out);
//			break;
//		case INT16:
//			byte[] bytes = BytesUtils.toBytes((short) code);
//			BytesEncoding.writeInShort(bytes, out);
//			break;
//		case INT32:
//			BytesUtils.writeInt(code, out);
//			break;
//		default:
//			throw new DataContractException("write: enum type error!");
//		}
//	}
//
//	private void writePrimitive(Object value, ValueType primitive, OutputStream out) {
//		switch (primitive) {
//		case BOOLEAN:
//			BytesUtils.writeByte((byte) ((boolean) value ? 1 : 0), out);
//			break;
//		case INT8:
//			BytesUtils.writeByte((byte) value, out);
//			break;
//		case INT16:
//			// TODO 可修改为short类型
//			byte[] bytes = BytesUtils.toBytes((short) value);
//			BytesEncoding.writeInShort(bytes, out);
//			break;
//		case INT32:
//			BytesUtils.writeInt((int) value, out);
//			break;
//		case INT64:
//			BytesUtils.writeLong((long) value, out);
//			break;
//		case DATETIME:
//			long seconds = ((Date) value).getTime();
//			BytesUtils.writeLong(seconds, out);
//			break;
//		case BYTES:
//			ByteArray byteArray = (ByteArray) value;
//			BytesEncoding.writeInNormal(byteArray.bytes(), out);
//			break;
//		case TEXT:
//		case JSON:
//		case XML:
//		case BIG_INT:
//		case IMG:
//		case VIDEO:
//		case LOCATION:
//			if (value == null) {
//				BytesEncoding.writeInNormal(null, out);
//			} else {
//				BytesEncoding.writeInNormal(value.toString().getBytes(), out);
//			}
//			break;
//		default:
//			throw new DataContractException("write: primitive type error!");
//		}
//	}
//
//	@Override
//	public void encode(Object data, OutputStream out) {
//		DataSpecificationImpl impl = (DataSpecificationImpl) this.getSepcification();
//		List<FieldSpec> fields = impl.getFields();
//
//		try {
//			for (FieldSpec spec : fields) {
//				Method mth = ((FieldSpecImpl) spec).getReadMethod();
//				// mth.setAccessible(true);
//				Object obj = mth.invoke(data);
//				write(spec, out, obj);
//			}
//		} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//			throw new DataContractException(e.getMessage(), e);
//		}
//	}
//
//	public void read(FieldSpec spec, Method mth, InputStream in, Map<String, Object> fieldOutter) {
//		ValueType primitive = spec.getPrimitiveType();
//		EnumSpecification refEnum = spec.getRefEnum();
//		DataSpecification refContract = spec.getRefContract();
//		boolean list = spec.isRepeatable();
//		boolean refPubKey = spec.isRefPubKey();
//		boolean refPrivKey = spec.isRefPrivKey();
//		boolean refHashDigest = spec.isRefHashDigest();
//		boolean refSignatureDigest = spec.isRefSignatureDigest();
//		boolean refIdentity = spec.isRefIdentity();
//		boolean refNetworkAddr = spec.isRefNetworkAddr();
//
//		try {
//			// primitive data type
//			if ((primitive != ValueType.NIL) && (list == false)) {
//				switch (primitive) {
//				case BOOLEAN:
//					boolean boolValue = BytesUtils.readByte(in) == 1 ? true : false;
//					if (fieldOutter != null) {
//						fieldOutter.put(mth.getName(), boolValue);
//					}
//					break;
//				case INT8:
//					byte int8Value = BytesUtils.readByte(in);
//					if (fieldOutter != null) {
//						fieldOutter.put(mth.getName(), int8Value);
//					}
//					break;
//				case INT16:
//					short shortValue = (short) ((BytesUtils.readByte(in) << 8) | (BytesUtils.readByte(in)));
//					if (fieldOutter != null) {
//						fieldOutter.put(mth.getName(), shortValue);
//					}
//					break;
//				case INT32:
//					int intValue = BytesUtils.readInt(in);
//					if (fieldOutter != null) {
//						fieldOutter.put(mth.getName(), intValue);
//					}
//					break;
//				case INT64:
//					long value = BytesUtils.readLong(in);
//					if (fieldOutter != null) {
//						fieldOutter.put(mth.getName(), value);
//					}
//					break;
//				case DATETIME:
//					long seconds = BytesUtils.readLong(in);
//					Date date = new Date(seconds);
//					if (fieldOutter != null) {
//						fieldOutter.put(mth.getName(), date);
//					}
//					break;
//				case BYTES:
//					byte[] bytes = BytesEncoding.read(NumberMask.NORMAL, in);
//					if (fieldOutter != null) {
//						fieldOutter.put(mth.getName(), ByteArray.wrap(bytes));
//					}
//					break;
//				case TEXT:
//				case JSON:
//				case XML:
//				case BIG_INT:
//				case IMG:
//				case VIDEO:
//				case LOCATION:
//					byte[] dynamicResult = BytesEncoding.read(NumberMask.NORMAL, in);
//					if (dynamicResult.length == 0) {
//						if (fieldOutter != null) {
//							fieldOutter.put(mth.getName(), null);
//						}
//					} else {
//						StringBuffer buffer = new StringBuffer();
//						for (byte i : dynamicResult) {
//							buffer.append((char) i);
//						}
//						if (fieldOutter != null) {
//							fieldOutter.put(mth.getName(), buffer.toString());
//						}
//					}
//					break;
//				default:
//					throw new DataContractException("read: primitive type error!");
//				}
//			}
//			// enum type
//			else if (refEnum != null) {
//				int code = 0;
//				switch (refEnum.getValueType()) {
//				case INT8:
//					code = BytesUtils.readByte(in);
//					break;
//				case INT16:
//					code = (BytesUtils.readByte(in) << 8) | (BytesUtils.readByte(in));
//					break;
//				case INT32:
//					code = BytesUtils.readInt(in);
//					break;
//				default:
//					throw new DataContractException("read: enum type error!");
//				}
//				EnumSpecificationImpl refEnumImpl = (EnumSpecificationImpl) refEnum;
//				Map<Object, Integer> constant = refEnumImpl.getEnumConstants();
//				Object enumConstant = null;
//				for (Map.Entry<Object, Integer> vo : constant.entrySet()) {
//					if (vo.getValue() == code) {
//						enumConstant = vo.getKey();
//						break;
//					}
//				}
//				if (fieldOutter != null) {
//					fieldOutter.put(mth.getName(), enumConstant);
//				}
//			}
//			// ref contract type
//			else if (refContract != null && list == false) {
//				BinaryEncoder encoder = null;
//				Object object = null;
//				int code = BytesUtils.readInt(in);
//				long version = BytesUtils.readLong(in);
//				encoder = DataContractRegistry.getEncoder(code, version);
//				if (encoder == null) {
//					throw new DataContractException("read: get encoder null error!");
//				}
//				byte[] className = BytesEncoding.read(NumberMask.NORMAL, in);
//				if (className.length != 0) {
//					StringBuffer buffer = new StringBuffer();
//					for (byte i : className) {
//						buffer.append((char) i);
//					}
//					object = encoder.decode(in, null, Class.forName(buffer.toString()));
//				}
//				if (fieldOutter != null) {
//					fieldOutter.put(mth.getName(), object);
//				}
//			}
//			// array type
//			else if ((primitive != ValueType.NIL) && list == true) {
//				int arrayCount = BytesUtils.readInt(in);
//				int i;
//
//				if (arrayCount == 0) {
//					if (fieldOutter != null) {
//						fieldOutter.put(mth.getName(), null);
//					}
//				} else {
//					switch (primitive) {
//					case BOOLEAN:
//						boolean[] boolArray = new boolean[arrayCount];
//						for (i = 0; i < arrayCount; i++) {
//							boolArray[i] = (BytesUtils.readByte(in) == 1) ? true : false;
//						}
//						if (fieldOutter != null) {
//							fieldOutter.put(mth.getName(), boolArray);
//						}
//						break;
//					case INT8:
//						byte[] byteArray = new byte[arrayCount];
//						for (i = 0; i < arrayCount; i++) {
//							byteArray[i] = (BytesUtils.readByte(in));
//						}
//						if (fieldOutter != null) {
//							fieldOutter.put(mth.getName(), byteArray);
//						}
//						break;
//					case INT16:
//						short[] shortArray = new short[arrayCount];
//						for (i = 0; i < arrayCount; i++) {
//							shortArray[i] = (short) ((BytesUtils.readByte(in) << 8) | (BytesUtils.readByte(in)));
//						}
//						if (fieldOutter != null) {
//							fieldOutter.put(mth.getName(), shortArray);
//						}
//						break;
//					case INT32:
//						int[] intArray = new int[arrayCount];
//						for (i = 0; i < arrayCount; i++) {
//							intArray[i] = BytesUtils.readInt(in);
//						}
//						if (fieldOutter != null) {
//							fieldOutter.put(mth.getName(), intArray);
//						}
//						break;
//					case INT64:
//						long[] longArray = new long[arrayCount];
//						for (i = 0; i < arrayCount; i++) {
//							longArray[i] = BytesUtils.readLong(in);
//						}
//						if (fieldOutter != null) {
//							fieldOutter.put(mth.getName(), longArray);
//						}
//						break;
//					case DATETIME:
//						Date[] dateArray = new Date[arrayCount];
//						for (i = 0; i < arrayCount; i++) {
//							long seconds = BytesUtils.readLong(in);
//							dateArray[i].setTime(seconds);
//						}
//						if (fieldOutter != null) {
//							fieldOutter.put(mth.getName(), dateArray);
//						}
//						break;
//					case TEXT:
//					case JSON:
//					case XML:
//					case BYTES:
//					case BIG_INT:
//					case IMG:
//					case VIDEO:
//					case LOCATION:
//						StringBuffer[] stringBufferArray = new StringBuffer[arrayCount];
//						String[] buffer = new String[arrayCount];
//						for (i = 0; i < arrayCount; i++) {
//							byte[] dynamicResult = BytesEncoding.read(NumberMask.NORMAL, in);
//							stringBufferArray[i] = new StringBuffer();
//							for (byte j : dynamicResult) {
//								stringBufferArray[i].append((char) j);
//							}
//							buffer[i] = stringBufferArray[i].toString();
//						}
//						if (fieldOutter != null) {
//							fieldOutter.put(mth.getName(), buffer);
//						}
//						break;
//					default:
//						throw new DataContractException("read: array type error!");
//					}
//				}
//			}
//			// ref contract type array
//			else if (refContract != null && list == true) {
//				int code;
//				long version;
//				BinaryEncoder encoder = null;
//				Object[] refContractArray = null;
//				int refContractArraySize = BytesUtils.readInt(in);
//				if (refContractArraySize != 0) {
//					refContractArray = new Object[refContractArraySize];
//					for (int i = 0; i < refContractArray.length; i++) {
//						code = BytesUtils.readInt(in);
//						version = BytesUtils.readLong(in);
//						encoder = DataContractRegistry.getEncoder(code, version);
//						if (encoder == null) {
//							throw new DataContractException("read: get encoder null error!");
//						}
//						byte[] className = BytesEncoding.read(NumberMask.NORMAL, in);
//						if (className.length == 0) {
//							refContractArray[i] = null;
//						} else {
//							StringBuffer buffer = new StringBuffer();
//							for (byte var : className) {
//								buffer.append((char) var);
//							}
//							refContractArray[i] = encoder.decode(in, null, Class.forName(buffer.toString()));
//						}
//					}
//				}
//				if (fieldOutter != null) {
//					fieldOutter.put(mth.getName(), refContractArray);
//				}
//			} else if (refPubKey == true | refPrivKey == true | refHashDigest == true | refSignatureDigest == true) {
//				Object object = null;
//				byte[] className = BytesEncoding.read(NumberMask.NORMAL, in);
//
//				if (className.length != 0) {
//					StringBuffer buffer = new StringBuffer();
//					for (byte var : className) {
//						buffer.append((char) var);
//					}
//					byte[] bytes = BytesEncoding.read(NumberMask.NORMAL, in);
//					object = Class.forName(buffer.toString()).getConstructor(byte[].class).newInstance(bytes);
//				}
//				if (fieldOutter != null) {
//					fieldOutter.put(mth.getName(), object);
//				}
//			}
//			// else if (refIdentity == true) {
//			// BytesReader reader = null;
//			// byte[] className = BytesEncoding.read(NumberMask.NORMAL, in);
//			// if (className.length != 0) {
//			// StringBuffer buffer = new StringBuffer();
//			// for (byte var : className) {
//			// buffer.append((char) var);
//			// }
//			// reader = (BytesReader)Class.forName(buffer.toString()).newInstance();
//			// reader.resolvFrom(in);
//			// }
//			// if (fieldOutter != null) {
//			// fieldOutter.put(mth.getName(), reader);
//			// }
//			// }
//			else if (refNetworkAddr == true) {
//				NetworkAddress networkAddress = null;
//				byte[] buffer = BytesEncoding.read(NumberMask.NORMAL, in);
//				if (buffer.length != 0) {
//					StringBuffer host = new StringBuffer();
//					for (byte var : buffer) {
//						host.append((char) var);
//					}
//					int port = BytesUtils.readInt(in);
//					boolean secure = BytesUtils.readByte(in) == 1 ? true : false;
//					networkAddress = new NetworkAddress(host.toString(), port, secure);
//				}
//				if (fieldOutter != null) {
//					fieldOutter.put(mth.getName(), networkAddress);
//				}
//			}
//		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException
//				| InvocationTargetException e) {
//			throw new DataContractException(e.getMessage(), e);
//		}
//	}
//
//	public boolean checkConstructor(Class<?> concretedDataType) {
//		int count = 0;
//		Constructor<?>[] constructors = concretedDataType.getConstructors();
//		for (Constructor constructor : constructors) {
//			if (constructor.getDeclaredAnnotation(DConstructor.class) != null) {
//				count++;
//			}
//		}
//		if (count >= 2) {
//			return false;
//		}
//		return true;
//	}
//
//	// convert the first char to lowercase
//	public String toLowerCaseFirstOne(String s) {
//		if (Character.isLowerCase(s.charAt(0)))
//			return s;
//		else
//			return (new StringBuilder()).append(Character.toLowerCase(s.charAt(0))).append(s.substring(1)).toString();
//	}
//
//	public void executeSet(List<FieldSpec> fields, Map<String, Object> fieldOutter, Object object,
//			Class<?> concretedDataType) {
//		Method mth;
//		try {
//			for (FieldSpec spec : fields) {
//				mth = ((FieldSpecImpl) spec).getReadMethod();
//				if (mth.getName().length() < 4) {
//					throw new DataContractException("executeSet: mth name error!");
//				}
//				// skip "get",get substring
//				String getName = mth.getName().substring(3);
//				// "set" concat with getmthName
//				String setName = "set".concat(getName);
//				Method[] allMths = concretedDataType.getMethods();
//				Method setMth = null;
//				for (Method x : allMths) {
//					if (x.getName().equals(setName)) {
//						setMth = x;
//						break;
//					}
//				}
//				if (setMth != null) {
//					// invoke related set method
//					Object arg = fieldOutter.get(mth.getName());
//					if (arg != null) {
//						setMth.invoke(object, arg);
//					}
//				}
//				// set related member field
//				else {
//					String member = toLowerCaseFirstOne(getName);
//					Field field = concretedDataType.getDeclaredField(member);
//					if (field != null) {
//						field.setAccessible(true);
//						field.set(object, fieldOutter.get(mth.getName()));
//					}
//				}
//			}
//		} catch (IllegalAccessException | InvocationTargetException | NoSuchFieldException e) {
//			throw new DataContractException(e.getMessage(), e);
//		}
//	}
//
//	@Override
//	public Object decode(InputStream in, Map<String, Object> fieldOutter, Class<?> concretedDataType) {
//		// TODO: 未缓存对实现类的解析；
//		DataSpecificationImpl impl = (DataSpecificationImpl) this.getSepcification();
//		List<FieldSpec> fields = impl.getFields();
//		Constructor constructor = null;
//		Object object = null;
//		Method mthType = null;
//
//		if (fieldOutter == null) {
//			fieldOutter = new HashMap<>();
//		}
//
//		try {
//			// first check constructor with annotation, count >=2 throw exception
//			if (checkConstructor(concretedDataType) == false) {
//				throw new DataContractException("decode: constructor with annotation number error!");
//			}
//			// get constructor with annotation
//			for (Constructor construct : concretedDataType.getConstructors()) {
//				if (construct.getDeclaredAnnotation(DConstructor.class) != null) {
//					constructor = construct;
//					break;
//				}
//			}
//			// fill fieldOutter with fieldsetter
//			for (FieldSpec spec : fields) {
//				Method mth = ((FieldSpecImpl) spec).getReadMethod();
//				if (mth == null) {
//					throw new DataContractException("decode: mth null error!");
//				}
//				read(spec, mth, in, fieldOutter);
//			}
//			// save constructor parameters
//			if (constructor != null) {
//				Annotation[][] annotations = constructor.getParameterAnnotations();
//				Object[] obj = new Object[annotations.length];
//				int i = 0;
//				for (Annotation[] annoArray : annotations) {
//					for (Annotation annotation : annoArray) {
//						FieldSetter anno = (FieldSetter) annotation;
//						obj[i] = fieldOutter.get(anno.name());
//						for (FieldSpec spec : fields) {
//							mthType = ((FieldSpecImpl) spec).getReadMethod();
//							// in case :constructor and data contract method name is same ,but return type
//							// is different
//							if (mthType.getName().equals(anno.name())) {
//								String retType = mthType.getReturnType().getSimpleName();
//								String annoType = anno.type();
//								if ((retType.equals(annoType) == false) && (retType.equals("ByteArray"))
//										&& (annoType.equals("byte[]"))) {
//									ByteArray byteArray = (ByteArray) obj[i];
//									obj[i] = byteArray.bytes();
//									break;
//								} else if ((retType.equals(annoType) == false) && (retType.equals("byte[]"))
//										&& (annoType.equals("ByteArray"))) {
//									byte[] bytes = (byte[]) obj[i];
//									obj[i] = ByteArray.wrap(bytes);
//									break;
//								}
//							}
//						}
//						i++;
//					}
//				}
//				// exec constructor with parameters
//				object = constructor.newInstance(obj);
//			}
//			if (object == null) {
//				// use default constructor,
//				constructor = concretedDataType.getDeclaredConstructor();
//				if (!constructor.isAccessible()) {
//					constructor.setAccessible(true);
//				}
//				object = constructor.newInstance();
//			}
//			// exec set method
//			executeSet(fields, fieldOutter, object, concretedDataType);
//			return object;
//		} catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException
//				| SecurityException e) {
//			throw new DataContractException(e.getMessage(), e);
//		}
//	}
//}
