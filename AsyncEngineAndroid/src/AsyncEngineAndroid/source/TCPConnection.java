package AsyncEngineAndroid.source;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;

import android.util.Log;

public class TCPConnection extends ConnectionImp {

	/** ����ͨ�ŵ�channel */
	private final SocketChannel channel;
	/**
	 * true��ʾԶ���Ѿ��ر����������
	 */
	private boolean remoteClosed;

	/**
	 * ����һ�����ӵ�ָ����ַ��TCPPort.
	 * 
	 * @param address
	 *            ���ӵ��ĵ�ַ.
	 * @throws IOException
	 *             �˿ڴ�/�˿�����/���ӵ���ַ����.
	 */
	public TCPConnection(String id, InetSocketAddress address)
			throws IOException {
		super(id);
		channel = SocketChannel.open();
		channel.configureBlocking(false);
		this.remoteAddress = address;
		remoteClosed = false;
	}

	/*
	 * (non-Javadoc)
	 */
	public void start() {
		try {
			channel.connect(remoteAddress);
		} catch (UnknownHostException e) {
			processError(new Exception("Unknown Host"));
		} catch (UnresolvedAddressException e) {
			processError(new Exception("Unable to resolve server address"));
		} catch (IOException e) {
			processError(e);
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
	public void receive() throws IOException {
		if (remoteClosed)
			return;
		// ��������
		int oldPos = receiveBuf.position();
		for (int r = channel.read(receiveBuf); r > 0; r = channel
				.read(receiveBuf))
			;

		byte[] tempBuffer = new byte[1024];
		receiveBuf.get(tempBuffer, 0, receiveBuf.position());
		Log.e("receive", " = " + new String(tempBuffer, "UTF-8"));

		// �õ���ǰλ��
		int pos = receiveBuf.position();
		receiveBuf.flip();
		// ����Ƿ����0�ֽڣ��������һ���ʾԶ���Ѿ��ر����������
		if (oldPos == pos) {
			remoteClosed = true;
			return;
		}
		InPacket packet = new InPacket(receiveBuf);
		inQueue.add(packet);

		adjustBuffer(pos);
	}

	/**
	 * ����buffer
	 * 
	 * @param pos
	 */
	private void adjustBuffer(int pos) {
		// ���0�����ڵ�ǰpos��˵�����ٷ�����һ����
		if (receiveBuf.position() > 0) {
			receiveBuf.compact();
			receiveBuf.limit(receiveBuf.capacity());
		} else {
			receiveBuf.limit(receiveBuf.capacity());
			receiveBuf.position(pos);
		}
	}

	/*
	 * (non-Javadoc)
	 */
	public void send() throws IOException {
		while (!isEmpty()) {
			sendBuf.clear();
			OutPacket packet = remove();
			channel.write(ByteBuffer.wrap(packet.getBody()));
			// ��ӵ��ط�����
			packet.setTimeout(System.currentTimeMillis()
					+ EnginConst.QQ_TIMEOUT_SEND);
			Log.e("debug", "have sended packet - " + packet.toString());
		}
	}

	/*
	 * (non-Javadoc)
	 */
	public void send(OutPacket packet) {
		try {
			sendBuf.clear();
			channel.write(ByteBuffer.wrap(packet.getBody()));
			Log.d("debug", "have sended packet - " + packet.toString());
		} catch (Exception e) {
		}
	}

	/*
	 * (non-Javadoc)
	 */
	public void send(ByteBuffer buffer) {
		try {
			channel.write(buffer);
		} catch (IOException e) {
		}
	}

	/*
	 * (non-Javadoc)
	 */
	public void dispose() {
		try {
			channel.close();
		} catch (IOException e) {
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
		// ���SocketChannel������
		channel.finishConnect();
		while (!channel.isConnected()) {
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
				// û��ʲôҪ����
			}
			channel.finishConnect();
		}
		sk.interestOps(SelectionKey.OP_READ);
		Log.e("debug", "hava connected to server");
	}

	/*
	 * (non-Javadoc)
	 */
	public void processRead(SelectionKey sk) throws IOException {
		receive();
	}

	/*
	 * (non-Javadoc)
	 */
	public void processWrite() throws IOException {
		if (isConnected())
			send();
	}

}
