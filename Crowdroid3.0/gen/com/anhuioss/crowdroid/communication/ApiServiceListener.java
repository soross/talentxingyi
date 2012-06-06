/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/oss/workspace0/Crowdroid3.0/src/com/anhuioss/crowdroid/communication/ApiServiceListener.aidl
 */
package com.anhuioss.crowdroid.communication;
public interface ApiServiceListener extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.anhuioss.crowdroid.communication.ApiServiceListener
{
private static final java.lang.String DESCRIPTOR = "com.anhuioss.crowdroid.communication.ApiServiceListener";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.anhuioss.crowdroid.communication.ApiServiceListener interface,
 * generating a proxy if needed.
 */
public static com.anhuioss.crowdroid.communication.ApiServiceListener asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.anhuioss.crowdroid.communication.ApiServiceListener))) {
return ((com.anhuioss.crowdroid.communication.ApiServiceListener)iin);
}
return new com.anhuioss.crowdroid.communication.ApiServiceListener.Stub.Proxy(obj);
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
case TRANSACTION_requestCompleted:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
int _arg1;
_arg1 = data.readInt();
java.lang.String _arg2;
_arg2 = data.readString();
java.lang.String _arg3;
_arg3 = data.readString();
this.requestCompleted(_arg0, _arg1, _arg2, _arg3);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.anhuioss.crowdroid.communication.ApiServiceListener
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
//Request Completed

public void requestCompleted(java.lang.String service, int type, java.lang.String statusCode, java.lang.String message) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(service);
_data.writeInt(type);
_data.writeString(statusCode);
_data.writeString(message);
mRemote.transact(Stub.TRANSACTION_requestCompleted, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_requestCompleted = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
//Request Completed

public void requestCompleted(java.lang.String service, int type, java.lang.String statusCode, java.lang.String message) throws android.os.RemoteException;
}
