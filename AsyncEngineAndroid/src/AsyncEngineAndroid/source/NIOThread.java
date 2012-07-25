package AsyncEngineAndroid.source;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Vector;

public class NIOThread implements Runnable {
	/** �߳��Ƿ�����ı�־ */
	protected boolean shutdown = false;
	/** �˿�ѡ���� */
	protected Selector selector;

	// Connection�б�
	private List<IConnection> ports;

	// �����ͷ�����
	private Queue<Object> disposeQueue;

	// ����������
	private List<Object> newConnections;

	/**
	 * ����һ��Porter.
	 */
	public NIOThread() {
		ports = new ArrayList<IConnection>();
		newConnections = new Vector<Object>();
		disposeQueue = new LinkedList<Object>();

		registry = new HashMap<String, IConnection>();
		references = new HashMap<IConnection, Integer>();

		// �����µ�Selector
		try {
			selector = Selector.open();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * ע��һ��IConnection��NIOThread��
	 * 
	 * @param port
	 *            IConnectionʵ��
	 * @throws ClosedChannelException
	 *             ���ע��ʧ��
	 */
	public void register(IConnection port) throws ClosedChannelException {
		SelectableChannel channel = port.channel();
		if (channel instanceof SocketChannel) {
			channel.register(selector, SelectionKey.OP_CONNECT,
					port.getNIOHandler());
		} else if (channel instanceof DatagramChannel) {
			channel.register(selector, SelectionKey.OP_READ,
					port.getNIOHandler());
		}
		if (!ports.contains(port))
			ports.add(port);
	}

	/**
	 * ��ָ���Ĳ���ע��channel
	 * 
	 * @param port
	 * @param ops
	 * @throws ClosedChannelException
	 */
	public void register(IConnection port, int ops)
			throws ClosedChannelException {
		SelectableChannel channel = port.channel();
		if (channel instanceof SocketChannel)
			channel.register(selector, ops, port.getNIOHandler());
		else if (channel instanceof DatagramChannel)
			channel.register(selector, ops, port.getNIOHandler());
		if (!ports.contains(port))
			ports.add(port);
	}

	private void deregister(IConnection port) {
		if (port == null)
			return;

		if (!ports.remove(port))
			return;
		SelectionKey key = port.channel().keyFor(selector);
		if (key != null)
			key.cancel();
		port.dispose();
	}

	/**
	 * ���ʹ����¼�������IConnection
	 * 
	 * @param e
	 *            ����������Ϣ��Exception
	 */
	private void dispatchErrorToAll(Exception e) {
		for (IConnection port : ports)
			port.getNIOHandler().processError(e);
	}

	/**
	 * ֪ͨ����IConnection���Ͱ�
	 * 
	 * @throws IOException
	 */
	private void notifySend() {
		int size = ports.size();
		for (int i = 0; i < size; i++) {
			INIOHandler handler = null;
			try {
				handler = (ports.get(i)).getNIOHandler();
				handler.processWrite();
			} catch (IOException e) {
				// handler.processError(e);
			} catch (IndexOutOfBoundsException e) {
			}
		}
	}

	/**
	 * ������תά������ע���IConnection����. ͨ���������ǵļ��������ֱ�������շ��Ͷ���/�����ն���/ά�����еĹ���.
	 * 
	 * @see IConnection#send(ByteBuffer)
	 * @see IConnection#receive(ByteBuffer)
	 * @see IConnection#maintain()
	 */
	public void run() {
		int n = 0;
		while (!shutdown) {
			// do select
			try {
				if (selector != null)
					n = selector.select(3000);
				// ���Ҫshutdown���ر�selector�˳�
				if (shutdown) {
					selector.close();
					break;
				}
			} catch (IOException e) {
				dispatchErrorToAll(e);
			}

			// ���������ͷ�����
			processDisposeQueue();

			// ���select���ش���0�������¼�
			if (n > 0) {
				for (Iterator<SelectionKey> i = selector.selectedKeys()
						.iterator(); i.hasNext();) {
					// �õ���һ��Key
					SelectionKey sk = i.next();
					i.remove();
					// ������Ƿ���Ч
					if (!sk.isValid())
						continue;

					// ����
					INIOHandler handler = (INIOHandler) sk.attachment();
					try {
						if (sk.isConnectable()) {
							handler.processConnect(sk);
						} else if (sk.isReadable()) {
							handler.processRead(sk);
						}
					} catch (IOException e) {
						handler.processError(e);
					} catch (RuntimeException e) {
					}
				}

				n = 0;
			}

			checkNewConnection();
			notifySend();
		}

		selector = null;
		shutdown = false;
	}

	/**
	 * ����ͷ�����
	 * 
	 * @param p
	 */
	public void addDisposeRequest(IConnection p) {
		synchronized (disposeQueue) {
			disposeQueue.offer(p);
		}
	}

	/**
	 * ����Ƿ���������Ҫ����
	 */
	private void checkNewConnection() {
		while (!newConnections.isEmpty()) {
			Object handler = newConnections.remove(0);
			if (handler instanceof IConnection) {
				try {
					register((IConnection) handler);
				} catch (ClosedChannelException e1) {
				}
			}
		}
	}

	public int getConnectionNum() {
		return newConnections.size();
	}

	/**
	 * ���������ͷ�����
	 */
	private void processDisposeQueue() {
		synchronized (disposeQueue) {
			while (!disposeQueue.isEmpty()) {
				Object obj = disposeQueue.poll();
				if (obj instanceof IConnection)
					deregister((IConnection) obj);
			}
		}
	}

	/**
	 * �ر�IConnection
	 */
	public void shutdown() {
		if (selector != null) {
			shutdown = true;
			selector.wakeup();
		}
	}

	/**
	 * ����selector
	 */
	public void wakeup() {
		selector.wakeup();
	}

	/**
	 * ����selectorȻ��ע�����proxy
	 * 
	 * @param proxy
	 */
	public void wakeup(Object handler) {
		newConnections.add(handler);
		selector.wakeup();
	}

	/************************************************************************/

	// IPortӳ������key�����ƣ�value��IPort����
	private Map<String, IConnection> registry;
	// ���ü���
	private Map<IConnection, Integer> references;

	public void start() {
		this.start();
	}

	public synchronized List<IConnection> getConnections() {
		List<IConnection> ret = new ArrayList<IConnection>();
		ret.addAll(registry.values());
		return ret;
	}

	public synchronized boolean hasConnection(String id) {
		return registry.containsKey(id);
	}

	public synchronized void release(IConnection con) {
		if (con == null)
			return;

		Integer reference = references.get(con);
		if (reference == null)
			return;
		reference--;
		if (reference <= 0) {
			references.remove(con);
			registry.remove(con.getId());
			this.addDisposeRequest((IConnection) con);
		} else
			references.put(con, reference);
	}

	public synchronized void release(String id) {
		IConnection con = getConnection(id);
		release(con);
	}

	public synchronized IConnection getConnection(String id) {
		return registry.get(id);
	}

	/**
	 * ����Ƿ����һ����ָ��Զ�̵�ַ������
	 * 
	 * @param address
	 * @return
	 */
	public synchronized IConnection getConnection(InetSocketAddress address) {
		for (IConnection port : registry.values()) {
			if (port.getRemoteAddress().equals(address))
				return port;
		}
		return null;
	}

	/**
	 * �������ӵ����ü���
	 * 
	 * @param id
	 */
	private synchronized void increaseReference(IConnection con) {
		Integer i = references.get(con);
		if (i != null) {
			i++;
			references.put(con, i);
		}
	}

	/**
	 * ʹ��ָ����port����һ����
	 * 
	 * @param name
	 *            port name
	 * @param packet
	 *            OutPacket����
	 * @param keepSent
	 *            true��ʾ���淢���İ���������Ҫ����Ϊ��ЩЭ��ķ��ذ�û��ʲô������Ϣ����Ҫʹ�÷������� �����¼�
	 * @return true��ʾ�����ͳɹ���false��ʾʧ��
	 */
	public void send(String id, OutPacket packet, boolean keepSent) {
		IConnection port = getConnection(id);
		if (port != null) {
			port.add(packet);
		}
	}

	public synchronized IConnection newUDPConnection(String id,
			InetSocketAddress server, boolean start) {
		if (hasConnection(id)) {
			IConnection con = getConnection(id);
			increaseReference(con);
		}

		UDPConnection port;
		try {
			port = new UDPConnection(id, server);
		} catch (IOException e) {
			return null;
		}

		registry.put(id, port);
		references.put(port, 1);
		wakeup(port);
		if (start)
			port.start();
		return port;
	}

	public synchronized IConnection newTCPConnection(String id,
			InetSocketAddress server, boolean start) {
		if (hasConnection(id)) {
			IConnection con = getConnection(id);
			increaseReference(con);
		}

		TCPConnection port;
		try {
			port = new TCPConnection(id, server);
		} catch (IOException e) {
			return null;
		}
		registry.put(id, port);
		references.put(port, 1);
		wakeup(port);
		if (start)
			port.start();
		return port;
	}

	/*
	 * (non-Javadoc)
	 */
	public synchronized void dispose() {
		new Thread() {
			@Override
			public void run() {
				shutdown();
				registry.clear();
				references.clear();
				registry = null;
				references = null;
			}
		}.start();
	}

	public void flush() {
		wakeup();
	}
	/***********************************************************************************/
}
