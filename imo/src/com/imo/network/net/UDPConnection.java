package com.imo.network.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.UnresolvedAddressException;

import com.imo.dataengine.DataEngine;
import com.imo.network.packages.IMOCommand;
import com.imo.network.packages.OutPacket;

import android.util.Log;


public class UDPConnection extends ConnectionImp{

	/** ���ݱ�channel */
	protected final DatagramChannel channel;
		
	/**
	 * ���캯��
	 * 
	 * @param policy
	 * 		�˿ڲ���
	 * @param address
	 * 		��������ַ
	 * @throws IOException
	 * 		�������˿�ʧ��
	 */
	public UDPConnection(String id, InetSocketAddress address) throws IOException {
	    super(id);
		channel = DatagramChannel.open();
		channel.configureBlocking(false);
        this.remoteAddress = address;
	}

	/* (non-Javadoc)
	 */
	public SelectableChannel channel() {
		return channel;
	}
	
    /* (non-Javadoc)
     */
    public void start() {
		try {
            channel.connect(remoteAddress);
        } catch(UnknownHostException e) {
			Log.e("error","δ֪�ķ�������ַ");
			processError(new Exception("Unknown Host"),IMOCommand.ERROR_NETWORK);
		} catch(UnresolvedAddressException e) {
			Log.e("error","�޷�������������ַ");
			processError(new Exception("Unable to resolve server address"),IMOCommand.ERROR_NETWORK);
        } catch (IOException e) {
            Log.e("error","����ʧ��");
            processError(e,IMOCommand.ERROR_NETWORK);
        } 
    }

	/* (non-Javadoc)
	 */
	public void receive() throws IOException {
	    receiveBuf.clear();
		for (int len = channel.read(receiveBuf); len > 0; len = channel.read(receiveBuf)) {
		    receiveBuf.flip();
		    receiveBuf.clear();
		}
	}

	/* (non-Javadoc)
	 */
	public void send() {
		while (!DataEngine.getInstance().isEmpty()) {
			sendBuf.clear();
			OutPacket packet = DataEngine.getInstance().remove();
			try {
				channel.write(ByteBuffer.wrap(packet.getBody()));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.e("error",e.getMessage());
				e.printStackTrace();
			}
			// ��ӵ��ط�����
			packet.setTimeout(System.currentTimeMillis() + EngineConst.IMO_TIMEOUT_SEND_WIFI);
			Log.e("debug","have sended packet - " + packet.toString());	
			}
		}
	
    /* (non-Javadoc)
     */
    public void send(OutPacket packet) {
		try {
			sendBuf.clear();
			channel.write(ByteBuffer.wrap(packet.getBody()));
            Log.d("debug","have sended packet - " + packet.toString());			    
        } catch (Exception e) {
        }
    }
    
    /* (non-Javadoc)
     */
    public void send(ByteBuffer buffer) {
        try {
            channel.write(buffer);
        } catch (IOException e) {
        }
    }
    
    /* (non-Javadoc)
     */
    public void dispose() {
        try {
			channel.close();
		} catch(IOException e) {
		}
    }
    
    /* (non-Javadoc)
     */
    public boolean isConnected() {
        return true;
    }
    
    /* (non-Javadoc)
     */
    public void processConnect(SelectionKey sk) throws IOException {
        // û��ʲôҪ����
    }

    /* (non-Javadoc)
     */
    public void processRead(SelectionKey sk) throws IOException {
        receive();
    }

    /* (non-Javadoc)
     */
    public void processWrite() {
        send();
    }

	public void processError(Exception e,short aErrorCode) {
		// TODO Auto-generated method stub
		
	}

}
