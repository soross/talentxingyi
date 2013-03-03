package com.imo.network.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;

import android.util.Log;

import com.imo.dataengine.DataEngine;
import com.imo.network.Observer.PacketsObserver;
import com.imo.network.packages.IMOCommand;
import com.imo.network.packages.OutPacket;

public class HTTPConnection extends ConnectionImp{

	/** 回复码 - 成功 */
	public static final String SUCCESS = "200";
	/** 回复码 - 需要验证 */
	public static final String NEED_AUTH = "407";
	
    /** 状态 - 无动作 */
    public static final int STATUS_NONE = 0;
    /** 状态 - 最初请求 */
    public static final int STATUS_INIT = 1;
    /** 状态 - 验证 */
    public static final int STATUS_AUTH = 2;
    /** 状态 - Ready */
    public static final int STATUS_READY = 3;
    
    /** CRLF */
    private static final byte[] CRLF = "\r\n".getBytes();
    /** CONNECT 的前面部分 */
    private static final byte[] CONNECT_BEGIN = "GET ".getBytes();
    /** CONNECT 的后面部分 */
    private static final byte[] CONNECT_END = " HTTP/1.1\r\n".getBytes();
    /** Accept Header */
    private static final byte[] ACCEPT = "Accept: */*\r\n".getBytes();
    /** Content-Type Header */
    private static final byte[] CONTENT_TYPE = "Content-Type: text/html\r\n".getBytes();
    /** Host */
    private static final byte[] HOST = "Host: ".getBytes();
	/** 当前状态 */
	protected int status;
	
	/** 用于通信的channel */
	private final SocketChannel channel;
	/**
	 * true表示远程已经关闭了这个连接
	 */
	private boolean remoteClosed;
	
	private PacketsObserver dataObserver;
	
	private String host;

	/**
	 * 构造一个连接到指定地址的TCPPort.
	 * 
	 * @param address
	 *            连接到的地址.
	 * @throws IOException
	 *             端口打开/端口配置/连接到地址出错.
	 */
	public HTTPConnection(String id, InetSocketAddress address, String host)throws IOException {
		super(id);
		channel = SocketChannel.open();
		channel.configureBlocking(false);
		this.remoteAddress = address;
		remoteClosed = false;
	    status = STATUS_NONE;
	    this.host = host;
	}

    public void init() {
        // 构造请求连接包
        String hostname = remoteAddress.getHostName();
        
        sendBuf.clear();       
        sendBuf.put(CONNECT_BEGIN)
	           .put(host.getBytes())
	           .put(CONNECT_END)
	           .put(HOST)
	           .put(hostname.getBytes())
	           .put(CRLF)
	           .put(ACCEPT)
	           .put(CONTENT_TYPE)
	           .put(CRLF);
        // 发送
        sendBuf.flip();
        send();
        status = STATUS_INIT;     
    }
    
	/*
	 * (non-Javadoc)
	 */
    public void start() {
		try {
            channel.connect(remoteAddress);
            Log.e("HttpConnection","connect");
		} catch(UnknownHostException e) {
			Log.e("error","未知的服务器地址");
			processError(new Exception("Unknown Host"),IMOCommand.ERROR_NETWORK);
		} catch(UnresolvedAddressException e) {
			Log.e("error","无法解析服务器地址");
			processError(new Exception("Unable to resolve server address"),IMOCommand.ERROR_NETWORK);
        } catch (IOException e) {
            Log.e("error","连接失败");
            processError(e,IMOCommand.ERROR_NETWORK);
        } 
    }

	/*
	 * (non-Javadoc)
	 */
	public SelectableChannel channel() {
		return channel;
	}

	/*
	 * (non-Javadoc)
	 */
	public void send()  {
		try {
			if (isConnected()) {
				if (channel != null)
					channel.write(sendBuf);
			}
		} catch (IOException e) {
			Log.e("error",e.getMessage());
			processError(e,IMOCommand.ERROR_NETWORK);
		}
	}

	/*
	 * (non-Javadoc)
	 */
	public void dispose() {
		try {
			channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 */
	public boolean isConnected() {
		return channel != null && channel.isConnected();
	}

	/*
	 * (non-Javadoc)
	 */
	public void processConnect(SelectionKey sk) throws IOException {
		// 完成SocketChannel的连接
		channel.finishConnect();
		while (!channel.isConnected()) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// 没有什么要做的
				e.printStackTrace();
			}
			channel.finishConnect();
		}
		sk.interestOps(SelectionKey.OP_READ);
		Log.e("HttpConnection", "have connected to server");
	}
	
	/*
	 * (non-Javadoc)
	 */
	public void processRead(SelectionKey sk) throws IOException {
		try {
			receiveBuf.clear();
			
			int contentLength = -1;
			int headerLength = -1;
			int contentLengthIndex = -1;
			int contentLengthIndexEnd = -1;
			
			if (channel != null)
				for (int len = channel.read(receiveBuf); len > 0; len = channel.read(receiveBuf))
				{   
					if ( -1 == headerLength )
					{
					    ByteBuffer tempBuffer = receiveBuf.duplicate();
					    tempBuffer.flip();
					    byte[] a = new byte[tempBuffer.limit()];
					    tempBuffer.get(a);
					    String tempResp = new String(a);
					    
					    headerLength = tempResp.indexOf("\r\n\r\n");
					    
					    if ( -1 != headerLength )
					    {
						    contentLengthIndex = tempResp.indexOf("Content-length: ");
					    	contentLengthIndexEnd = tempResp.indexOf("\r\n", contentLengthIndex);
					    	String contentLengthStr = tempResp.substring(contentLengthIndex+"Content-length: ".length(), contentLengthIndexEnd);
					    	contentLength = Integer.parseInt(contentLengthStr);
					    	
					    	
					    	tempBuffer.clear();
					    	tempBuffer = null;
					    	a = null;
					    	tempResp = null;
					    }
					    else
					    {
					    	tempBuffer.clear();
					    	tempBuffer = null;
					    	a = null;
					    	tempResp = null;
					    	
					    	continue;
					    }
					}
					else
					{
					    if( receiveBuf.position() < contentLength + headerLength + 4)
					    	continue;	
					}
					
				}
			receiveBuf.flip();
		} catch (IOException e) {
			Log.e("error",e.getMessage());
			processError(e,IMOCommand.ERROR_NETWORK);
		}
		
		// 检查头部
        byte[] b = new byte[receiveBuf.limit()];
        receiveBuf.get(b);
        String response = new String(b);
        Log.d("debug",response);
        if(!response.startsWith("HTTP/1."))
            return;
        
        // 判断状态
        String replyCode = response.substring(9, 12);
        switch(status) {
            case STATUS_INIT:
                if(SUCCESS.equals(replyCode)) {
                    /* 成功 */
                    Log.d("debug","连接成功");
                    status = STATUS_READY;
                    int bodyStart = response.indexOf("\r\n\r\n");
                    byte[] bodyTemp = new byte[b.length-bodyStart-4];
                    System.arraycopy(b, bodyStart+4, bodyTemp, 0, b.length-bodyStart-4);
                    DataEngine.getInstance().observerNotifyHttpPacketArrived( this.name, ByteBuffer.wrap(bodyTemp) );
                    
                    bodyTemp = null;
                    b = null;
                    response = null;
                    dispose();
                } else if(NEED_AUTH.equals(replyCode)) {
                    /* 需要验证 */
                    Log.d("debug","需要验证，但是未提供用户名密码");
                    dispose();
                } else {
                    Log.d("debug","未知的回复码");
                    dispose();
                }
                break;
            default:
                break;
        }
	}

	/*
	 * (non-Javadoc)
	 */
	public void processWrite() {
		if (isConnected())
		{
	        init();
		}
		else
		{
		}
	}
	
	public void processError(Exception e,short aErrorCode) {
		Log.d("debug","网络出错，关闭连接, id: " + getId());
		dataObserver.NotifyPacketFailed(this.name, IMOCommand.ERROR_NETWORK);
	}

	public void receive() throws IOException {
		// TODO Auto-generated method stub
		
	}

	public void send(ByteBuffer buffer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void send(OutPacket packet) {
		// TODO Auto-generated method stub
		
	}

}
