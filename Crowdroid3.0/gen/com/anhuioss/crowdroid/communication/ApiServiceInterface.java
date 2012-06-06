/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/oss/workspace0/Crowdroid3.0/src/com/anhuioss/crowdroid/communication/ApiServiceInterface.aidl
 */
package com.anhuioss.crowdroid.communication;
public interface ApiServiceInterface extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.anhuioss.crowdroid.communication.ApiServiceInterface
{
private static final java.lang.String DESCRIPTOR = "com.anhuioss.crowdroid.communication.ApiServiceInterface";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.anhuioss.crowdroid.communication.ApiServiceInterface interface,
 * generating a proxy if needed.
 */
public static com.anhuioss.crowdroid.communication.ApiServiceInterface asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.anhuioss.crowdroid.communication.ApiServiceInterface))) {
return ((com.anhuioss.crowdroid.communication.ApiServiceInterface)iin);
}
return new com.anhuioss.crowdroid.communication.ApiServiceInterface.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_request:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
com.anhuioss.crowdroid.communication.ApiServiceListener _arg2;
_arg2 = com.anhuioss.crowdroid.communication.ApiServiceListener.Stub.asInterface(data.readStrongBinder());
java.util.Map _arg3;
java.lang.ClassLoader cl = (java.lang.ClassLoader)this.getClass().getClassLoader();
_arg3 = data.readHashMap(cl);
this.request(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.anhuioss.crowdroid.communication.ApiServiceInterface
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
//Request

public void request(java.lang.String service, int type, com.anhuioss.crowdroid.communication.ApiServiceListener listener, java.util.Map parameters) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(service);
_data.writeInt(type);
_data.writeStrongBinder((((listener!=null))?(listener.asBinder()):(null)));
_data.writeMap(parameters);
mRemote.transact(Stub.TRANSACTION_request, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_request = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
//Request

public void request(java.lang.String service, int type, com.anhuioss.crowdroid.communication.ApiServiceListener listener, java.util.Map parameters) throws android.os.RemoteException;
}
