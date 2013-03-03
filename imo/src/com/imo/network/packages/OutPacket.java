package com.imo.network.packages;

import java.nio.ByteBuffer;

import com.imo.network.net.EngineConst;



public class OutPacket extends Packet {

	/** ��ʱ��ֹʱ�䣬��λms */
	protected long timeout;
	
	protected int sendCount;
	
	/** �ط������� */
	protected int resendCountDown = 1;
   
    public int getResendCountDown() {
		return resendCountDown;
	}

	public void setResendCountDown(int resendCountDown) {
		this.resendCountDown = resendCountDown;
	}

	public OutPacket(ByteBuffer aHeader, ByteBuffer aBody) {
		super(aHeader, aBody);
		this.sendCount = 1;
    }    	
    
	public OutPacket(ByteBuffer aBody,short aCommand, int aCid,int aUid)
	{
		super(aBody,aCommand,aCid,aUid);
		
		this.body = aBody;
		this.dataLen = (short)(aBody.limit()+EngineConst.IMO__PACKET_HEADER_SIZE);
		this.command = aCommand;
		this.cid = aCid;
		this.uid = aUid;
		
		MakeHeader();
	}
	
	public OutPacket()
	{
		
	}
	
    /**
     * @return Returns the timeout.
     */
    public final long getTimeout() {
        return timeout;
    }
    
    /**
     * @param timeout The timeout to set.
     */
    public final void setTimeout(long timeout) {
        this.timeout = timeout;
    }
    
    /**
     * @param sendCount The sendCount to set.
     */
    public final void setSendCount(int sendCount) {
        this.sendCount = sendCount;
    }
    
    /**
     * @return Returns the sendCount.
     */
    public final int getSendCount() {
        return sendCount;
    }
    
	/**
	 * �Ƿ���Ҫ�ط�.
	 * 
	 * @return ��Ҫ�ط�����true, ���򷵻�false.
	 */
	public final boolean needResend() {
		return (resendCountDown--) > 0;
	}
}


