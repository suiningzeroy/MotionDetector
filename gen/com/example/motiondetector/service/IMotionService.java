/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/thermohaline/zy/MotionDetector/src/com/example/motiondetector/service/IMotionService.aidl
 */
package com.example.motiondetector.service;
public interface IMotionService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.example.motiondetector.service.IMotionService
{
private static final java.lang.String DESCRIPTOR = "com.example.motiondetector.service.IMotionService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.example.motiondetector.service.IMotionService interface,
 * generating a proxy if needed.
 */
public static com.example.motiondetector.service.IMotionService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.example.motiondetector.service.IMotionService))) {
return ((com.example.motiondetector.service.IMotionService)iin);
}
return new com.example.motiondetector.service.IMotionService.Stub.Proxy(obj);
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
case TRANSACTION_getMovingTimeOfADay:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
float _result = this.getMovingTimeOfADay(_arg0);
reply.writeNoException();
reply.writeFloat(_result);
return true;
}
case TRANSACTION_getAllCounts:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getAllCounts();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
case TRANSACTION_getPercentageOfMovingTimeOfADay:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
double _result = this.getPercentageOfMovingTimeOfADay(_arg0);
reply.writeNoException();
reply.writeDouble(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.example.motiondetector.service.IMotionService
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
public float getMovingTimeOfADay(java.lang.String date) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
float _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(date);
mRemote.transact(Stub.TRANSACTION_getMovingTimeOfADay, _data, _reply, 0);
_reply.readException();
_result = _reply.readFloat();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getAllCounts() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getAllCounts, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public double getPercentageOfMovingTimeOfADay(java.lang.String date) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
double _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(date);
mRemote.transact(Stub.TRANSACTION_getPercentageOfMovingTimeOfADay, _data, _reply, 0);
_reply.readException();
_result = _reply.readDouble();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_getMovingTimeOfADay = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getAllCounts = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_getPercentageOfMovingTimeOfADay = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
}
public float getMovingTimeOfADay(java.lang.String date) throws android.os.RemoteException;
public int getAllCounts() throws android.os.RemoteException;
public double getPercentageOfMovingTimeOfADay(java.lang.String date) throws android.os.RemoteException;
}
