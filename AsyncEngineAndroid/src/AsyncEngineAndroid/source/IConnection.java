/*
* LumaQQ - Java QQ Client
*
* Copyright (C) 2004 luma <stubma@163.com>
*
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation; either version 2 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package AsyncEngineAndroid.source;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;

import AsyncEngineAndroid.source.INIOHandler;

/**
 * 连接接口，用于隔离具体的网络实现
 *
 * @author luma
 */
public interface IConnection {
	/**
	 * 添加一个包到发送队列
	 * 
	 * @param out
	 * 		OutPacket子类
	 */
	public void add(OutPacket out);
	
	public void clearSendQueue();
	
	public void start();
	
	public String getId();
	
	public void dispose();
	
	public InetSocketAddress getRemoteAddress();
	
	public SelectableChannel channel();
	
	public INIOHandler getNIOHandler();
	
	public boolean isEmpty();

	public void receive() throws IOException;

	public void send() throws IOException;
	
	public void send(ByteBuffer buffer);
    
    public boolean isConnected();
}
