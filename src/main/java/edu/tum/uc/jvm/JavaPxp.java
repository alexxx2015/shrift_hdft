/**
 * Autogenerated by Thrift Compiler (0.9.1)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package edu.tum.uc.jvm;
import java.util.BitSet;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;
import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.server.AbstractNonblockingServer.AsyncFrameBuffer;

public class JavaPxp {

  public interface Iface {

    public void delmr(String s, short time) throws org.apache.thrift.TException;

  }

  public interface AsyncIface {

    public void delmr(String s, short time, org.apache.thrift.async.AsyncMethodCallback resultHandler) throws org.apache.thrift.TException;

  }

  public static class Client extends org.apache.thrift.TServiceClient implements Iface {
    public static class Factory implements org.apache.thrift.TServiceClientFactory<Client> {
      public Factory() {}
      public Client getClient(org.apache.thrift.protocol.TProtocol prot) {
        return new Client(prot);
      }
      public Client getClient(org.apache.thrift.protocol.TProtocol iprot, org.apache.thrift.protocol.TProtocol oprot) {
        return new Client(iprot, oprot);
      }
    }

    public Client(org.apache.thrift.protocol.TProtocol prot)
    {
      super(prot, prot);
    }

    public Client(org.apache.thrift.protocol.TProtocol iprot, org.apache.thrift.protocol.TProtocol oprot) {
      super(iprot, oprot);
    }

    public void delmr(String s, short time) throws org.apache.thrift.TException
    {
      send_delmr(s, time);
    }

    public void send_delmr(String s, short time) throws org.apache.thrift.TException
    {
      delmr_args args = new delmr_args();
      args.setS(s);
      args.setTime(time);
      sendBase("delmr", args);
    }

  }
  public static class AsyncClient extends org.apache.thrift.async.TAsyncClient implements AsyncIface {
    public static class Factory implements org.apache.thrift.async.TAsyncClientFactory<AsyncClient> {
      private org.apache.thrift.async.TAsyncClientManager clientManager;
      private org.apache.thrift.protocol.TProtocolFactory protocolFactory;
      public Factory(org.apache.thrift.async.TAsyncClientManager clientManager, org.apache.thrift.protocol.TProtocolFactory protocolFactory) {
        this.clientManager = clientManager;
        this.protocolFactory = protocolFactory;
      }
      public AsyncClient getAsyncClient(org.apache.thrift.transport.TNonblockingTransport transport) {
        return new AsyncClient(protocolFactory, clientManager, transport);
      }
    }

    public AsyncClient(org.apache.thrift.protocol.TProtocolFactory protocolFactory, org.apache.thrift.async.TAsyncClientManager clientManager, org.apache.thrift.transport.TNonblockingTransport transport) {
      super(protocolFactory, clientManager, transport);
    }

    public void delmr(String s, short time, org.apache.thrift.async.AsyncMethodCallback resultHandler) throws org.apache.thrift.TException {
      checkReady();
      delmr_call method_call = new delmr_call(s, time, resultHandler, this, ___protocolFactory, ___transport);
      this.___currentMethod = method_call;
      ___manager.call(method_call);
    }

    public static class delmr_call extends org.apache.thrift.async.TAsyncMethodCall {
      private String s;
      private short time;
      public delmr_call(String s, short time, org.apache.thrift.async.AsyncMethodCallback resultHandler, org.apache.thrift.async.TAsyncClient client, org.apache.thrift.protocol.TProtocolFactory protocolFactory, org.apache.thrift.transport.TNonblockingTransport transport) throws org.apache.thrift.TException {
        super(client, protocolFactory, transport, resultHandler, true);
        this.s = s;
        this.time = time;
      }

      public void write_args(org.apache.thrift.protocol.TProtocol prot) throws org.apache.thrift.TException {
        prot.writeMessageBegin(new org.apache.thrift.protocol.TMessage("delmr", org.apache.thrift.protocol.TMessageType.CALL, 0));
        delmr_args args = new delmr_args();
        args.setS(s);
        args.setTime(time);
        args.write(prot);
        prot.writeMessageEnd();
      }

      public void getResult() throws org.apache.thrift.TException {
        if (getState() != org.apache.thrift.async.TAsyncMethodCall.State.RESPONSE_READ) {
          throw new IllegalStateException("Method call not finished!");
        }
        org.apache.thrift.transport.TMemoryInputTransport memoryTransport = new org.apache.thrift.transport.TMemoryInputTransport(getFrameBuffer().array());
        org.apache.thrift.protocol.TProtocol prot = client.getProtocolFactory().getProtocol(memoryTransport);
      }
    }

  }

  public static class Processor<I extends Iface> extends org.apache.thrift.TBaseProcessor<I> implements org.apache.thrift.TProcessor {
//    private static final Logger LOGGER = LoggerFactory.getLogger(Processor.class.getName());
    public Processor(I iface) {
      super(iface, getProcessMap(new HashMap<String, org.apache.thrift.ProcessFunction<I, ? extends org.apache.thrift.TBase>>()));
    }

    protected Processor(I iface, Map<String,  org.apache.thrift.ProcessFunction<I, ? extends  org.apache.thrift.TBase>> processMap) {
      super(iface, getProcessMap(processMap));
    }

    private static <I extends Iface> Map<String,  org.apache.thrift.ProcessFunction<I, ? extends  org.apache.thrift.TBase>> getProcessMap(Map<String,  org.apache.thrift.ProcessFunction<I, ? extends  org.apache.thrift.TBase>> processMap) {
      processMap.put("delmr", new delmr());
      return processMap;
    }

    public static class delmr<I extends Iface> extends org.apache.thrift.ProcessFunction<I, delmr_args> {
      public delmr() {
        super("delmr");
      }

      public delmr_args getEmptyArgsInstance() {
        return new delmr_args();
      }

      protected boolean isOneway() {
        return true;
      }

      public org.apache.thrift.TBase getResult(I iface, delmr_args args) throws org.apache.thrift.TException {
        iface.delmr(args.s, args.time);
        return null;
      }
    }

  }

  public static class AsyncProcessor<I extends AsyncIface> extends org.apache.thrift.TBaseAsyncProcessor<I> {
//    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncProcessor.class.getName());
    public AsyncProcessor(I iface) {
      super(iface, getProcessMap(new HashMap<String, org.apache.thrift.AsyncProcessFunction<I, ? extends org.apache.thrift.TBase, ?>>()));
    }

    protected AsyncProcessor(I iface, Map<String,  org.apache.thrift.AsyncProcessFunction<I, ? extends  org.apache.thrift.TBase, ?>> processMap) {
      super(iface, getProcessMap(processMap));
    }

    private static <I extends AsyncIface> Map<String,  org.apache.thrift.AsyncProcessFunction<I, ? extends  org.apache.thrift.TBase,?>> getProcessMap(Map<String,  org.apache.thrift.AsyncProcessFunction<I, ? extends  org.apache.thrift.TBase, ?>> processMap) {
      processMap.put("delmr", new delmr());
      return processMap;
    }

    public static class delmr<I extends AsyncIface> extends org.apache.thrift.AsyncProcessFunction<I, delmr_args, Void> {
      public delmr() {
        super("delmr");
      }

      public delmr_args getEmptyArgsInstance() {
        return new delmr_args();
      }

      public AsyncMethodCallback<Void> getResultHandler(final AsyncFrameBuffer fb, final int seqid) {
        final org.apache.thrift.AsyncProcessFunction fcall = this;
        return new AsyncMethodCallback<Void>() { 
          public void onComplete(Void o) {
          }
          public void onError(Exception e) {
          }
        };
      }

      protected boolean isOneway() {
        return true;
      }

      public void start(I iface, delmr_args args, org.apache.thrift.async.AsyncMethodCallback<Void> resultHandler) throws TException {
        iface.delmr(args.s, args.time,resultHandler);
      }
    }

  }

  public static class delmr_args implements org.apache.thrift.TBase<delmr_args, delmr_args._Fields>, java.io.Serializable, Cloneable, Comparable<delmr_args>   {
    private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("delmr_args");

    private static final org.apache.thrift.protocol.TField S_FIELD_DESC = new org.apache.thrift.protocol.TField("s", org.apache.thrift.protocol.TType.STRING, (short)1);
    private static final org.apache.thrift.protocol.TField TIME_FIELD_DESC = new org.apache.thrift.protocol.TField("time", org.apache.thrift.protocol.TType.I16, (short)2);

    private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
    static {
      schemes.put(StandardScheme.class, new delmr_argsStandardSchemeFactory());
      schemes.put(TupleScheme.class, new delmr_argsTupleSchemeFactory());
    }

    public String s; // required
    public short time; // required

    /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
    public enum _Fields implements org.apache.thrift.TFieldIdEnum {
      S((short)1, "s"),
      TIME((short)2, "time");

      private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

      static {
        for (_Fields field : EnumSet.allOf(_Fields.class)) {
          byName.put(field.getFieldName(), field);
        }
      }

      /**
       * Find the _Fields constant that matches fieldId, or null if its not found.
       */
      public static _Fields findByThriftId(int fieldId) {
        switch(fieldId) {
          case 1: // S
            return S;
          case 2: // TIME
            return TIME;
          default:
            return null;
        }
      }

      /**
       * Find the _Fields constant that matches fieldId, throwing an exception
       * if it is not found.
       */
      public static _Fields findByThriftIdOrThrow(int fieldId) {
        _Fields fields = findByThriftId(fieldId);
        if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
        return fields;
      }

      /**
       * Find the _Fields constant that matches name, or null if its not found.
       */
      public static _Fields findByName(String name) {
        return byName.get(name);
      }

      private final short _thriftId;
      private final String _fieldName;

      _Fields(short thriftId, String fieldName) {
        _thriftId = thriftId;
        _fieldName = fieldName;
      }

      public short getThriftFieldId() {
        return _thriftId;
      }

      public String getFieldName() {
        return _fieldName;
      }
    }

    // isset id assignments
    private static final int __TIME_ISSET_ID = 0;
    private byte __isset_bitfield = 0;
    public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
    static {
      Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
      tmpMap.put(_Fields.S, new org.apache.thrift.meta_data.FieldMetaData("s", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
      tmpMap.put(_Fields.TIME, new org.apache.thrift.meta_data.FieldMetaData("time", org.apache.thrift.TFieldRequirementType.DEFAULT, 
          new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I16)));
      metaDataMap = Collections.unmodifiableMap(tmpMap);
      org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(delmr_args.class, metaDataMap);
    }

    public delmr_args() {
    }

    public delmr_args(
      String s,
      short time)
    {
      this();
      this.s = s;
      this.time = time;
      setTimeIsSet(true);
    }

    /**
     * Performs a deep copy on <i>other</i>.
     */
    public delmr_args(delmr_args other) {
      __isset_bitfield = other.__isset_bitfield;
      if (other.isSetS()) {
        this.s = other.s;
      }
      this.time = other.time;
    }

    public delmr_args deepCopy() {
      return new delmr_args(this);
    }

    @Override
    public void clear() {
      this.s = null;
      setTimeIsSet(false);
      this.time = 0;
    }

    public String getS() {
      return this.s;
    }

    public delmr_args setS(String s) {
      this.s = s;
      return this;
    }

    public void unsetS() {
      this.s = null;
    }

    /** Returns true if field s is set (has been assigned a value) and false otherwise */
    public boolean isSetS() {
      return this.s != null;
    }

    public void setSIsSet(boolean value) {
      if (!value) {
        this.s = null;
      }
    }

    public short getTime() {
      return this.time;
    }

    public delmr_args setTime(short time) {
      this.time = time;
      setTimeIsSet(true);
      return this;
    }

    public void unsetTime() {
      __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __TIME_ISSET_ID);
    }

    /** Returns true if field time is set (has been assigned a value) and false otherwise */
    public boolean isSetTime() {
      return EncodingUtils.testBit(__isset_bitfield, __TIME_ISSET_ID);
    }

    public void setTimeIsSet(boolean value) {
      __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __TIME_ISSET_ID, value);
    }

    public void setFieldValue(_Fields field, Object value) {
      switch (field) {
      case S:
        if (value == null) {
          unsetS();
        } else {
          setS((String)value);
        }
        break;

      case TIME:
        if (value == null) {
          unsetTime();
        } else {
          setTime((Short)value);
        }
        break;

      }
    }

    public Object getFieldValue(_Fields field) {
      switch (field) {
      case S:
        return getS();

      case TIME:
        return Short.valueOf(getTime());

      }
      throw new IllegalStateException();
    }

    /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
    public boolean isSet(_Fields field) {
      if (field == null) {
        throw new IllegalArgumentException();
      }

      switch (field) {
      case S:
        return isSetS();
      case TIME:
        return isSetTime();
      }
      throw new IllegalStateException();
    }

    @Override
    public boolean equals(Object that) {
      if (that == null)
        return false;
      if (that instanceof delmr_args)
        return this.equals((delmr_args)that);
      return false;
    }

    public boolean equals(delmr_args that) {
      if (that == null)
        return false;

      boolean this_present_s = true && this.isSetS();
      boolean that_present_s = true && that.isSetS();
      if (this_present_s || that_present_s) {
        if (!(this_present_s && that_present_s))
          return false;
        if (!this.s.equals(that.s))
          return false;
      }

      boolean this_present_time = true;
      boolean that_present_time = true;
      if (this_present_time || that_present_time) {
        if (!(this_present_time && that_present_time))
          return false;
        if (this.time != that.time)
          return false;
      }

      return true;
    }

    @Override
    public int hashCode() {
      return 0;
    }

    @Override
    public int compareTo(delmr_args other) {
      if (!getClass().equals(other.getClass())) {
        return getClass().getName().compareTo(other.getClass().getName());
      }

      int lastComparison = 0;

      lastComparison = Boolean.valueOf(isSetS()).compareTo(other.isSetS());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetS()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.s, other.s);
        if (lastComparison != 0) {
          return lastComparison;
        }
      }
      lastComparison = Boolean.valueOf(isSetTime()).compareTo(other.isSetTime());
      if (lastComparison != 0) {
        return lastComparison;
      }
      if (isSetTime()) {
        lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.time, other.time);
        if (lastComparison != 0) {
          return lastComparison;
        }
      }
      return 0;
    }

    public _Fields fieldForId(int fieldId) {
      return _Fields.findByThriftId(fieldId);
    }

    public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
      schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
      schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
    }

    @Override
    public String toString() {
      StringBuilder sb = new StringBuilder("delmr_args(");
      boolean first = true;

      sb.append("s:");
      if (this.s == null) {
        sb.append("null");
      } else {
        sb.append(this.s);
      }
      first = false;
      if (!first) sb.append(", ");
      sb.append("time:");
      sb.append(this.time);
      first = false;
      sb.append(")");
      return sb.toString();
    }

    public void validate() throws org.apache.thrift.TException {
      // check for required fields
      // check for sub-struct validity
    }

    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
      try {
        write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
      } catch (org.apache.thrift.TException te) {
        throw new java.io.IOException(te);
      }
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
      try {
        // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
        __isset_bitfield = 0;
        read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
      } catch (org.apache.thrift.TException te) {
        throw new java.io.IOException(te);
      }
    }

    private static class delmr_argsStandardSchemeFactory implements SchemeFactory {
      public delmr_argsStandardScheme getScheme() {
        return new delmr_argsStandardScheme();
      }
    }

    private static class delmr_argsStandardScheme extends StandardScheme<delmr_args> {

      public void read(org.apache.thrift.protocol.TProtocol iprot, delmr_args struct) throws org.apache.thrift.TException {
        org.apache.thrift.protocol.TField schemeField;
        iprot.readStructBegin();
        while (true)
        {
          schemeField = iprot.readFieldBegin();
          if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
            break;
          }
          switch (schemeField.id) {
            case 1: // S
              if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
                struct.s = iprot.readString();
                struct.setSIsSet(true);
              } else { 
                org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
              }
              break;
            case 2: // TIME
              if (schemeField.type == org.apache.thrift.protocol.TType.I16) {
                struct.time = iprot.readI16();
                struct.setTimeIsSet(true);
              } else { 
                org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
              }
              break;
            default:
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
          }
          iprot.readFieldEnd();
        }
        iprot.readStructEnd();

        // check for required fields of primitive type, which can't be checked in the validate method
        struct.validate();
      }

      public void write(org.apache.thrift.protocol.TProtocol oprot, delmr_args struct) throws org.apache.thrift.TException {
        struct.validate();

        oprot.writeStructBegin(STRUCT_DESC);
        if (struct.s != null) {
          oprot.writeFieldBegin(S_FIELD_DESC);
          oprot.writeString(struct.s);
          oprot.writeFieldEnd();
        }
        oprot.writeFieldBegin(TIME_FIELD_DESC);
        oprot.writeI16(struct.time);
        oprot.writeFieldEnd();
        oprot.writeFieldStop();
        oprot.writeStructEnd();
      }

    }

    private static class delmr_argsTupleSchemeFactory implements SchemeFactory {
      public delmr_argsTupleScheme getScheme() {
        return new delmr_argsTupleScheme();
      }
    }

    private static class delmr_argsTupleScheme extends TupleScheme<delmr_args> {

      @Override
      public void write(org.apache.thrift.protocol.TProtocol prot, delmr_args struct) throws org.apache.thrift.TException {
        TTupleProtocol oprot = (TTupleProtocol) prot;
        BitSet optionals = new BitSet();
        if (struct.isSetS()) {
          optionals.set(0);
        }
        if (struct.isSetTime()) {
          optionals.set(1);
        }
        oprot.writeBitSet(optionals, 2);
        if (struct.isSetS()) {
          oprot.writeString(struct.s);
        }
        if (struct.isSetTime()) {
          oprot.writeI16(struct.time);
        }
      }

      @Override
      public void read(org.apache.thrift.protocol.TProtocol prot, delmr_args struct) throws org.apache.thrift.TException {
        TTupleProtocol iprot = (TTupleProtocol) prot;
        BitSet incoming = iprot.readBitSet(2);
        if (incoming.get(0)) {
          struct.s = iprot.readString();
          struct.setSIsSet(true);
        }
        if (incoming.get(1)) {
          struct.time = iprot.readI16();
          struct.setTimeIsSet(true);
        }
      }
    }

  }

}
