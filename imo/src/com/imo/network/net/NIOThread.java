package com.imo.network.net;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.imo.dataengine.DataEngine;
import com.imo.network.Observer.PacketsObserver;
import com.imo.network.packages.OutPacket;
import com.imo.util.LogFactory;

public class NIOThread implements Runnable {
	private static final String TAG = NIOThread.class.getSimpleName();

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
		ports = new Vector<IConnection>();
		newConnections = new Vector<Object>();
		registry = new ConcurrentHashMap<String, IConnection>();
		references = new ConcurrentHashMap<IConnection, Integer>();

		disposeQueue = new ConcurrentLinkedQueue<Object>();

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
	 *        IConnectionʵ��
	 * @throws ClosedChannelException
	 *         ���ע��ʧ��
	 */
	public void register(IConnection port) throws ClosedChannelException {
		SelectableChannel channel = port.channel();
		if (channel instanceof SocketChannel) {
			channel.register(selector, SelectionKey.OP_CONNECT, port.getNIOHandler());
		} else if (channel instanceof DatagramChannel) {
			channel.register(selector, SelectionKey.OP_READ, port.getNIOHandler());
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
	public void register(IConnection port, int ops) throws ClosedChannelException {
		SelectableChannel channel = port.channel();
		if (channel instanceof SocketChannel)
			channel.register(selector, ops, port.getNIOHandler());
		else if (channel instanceof DatagramChannel)
			channel.register(selector, ops, port.getNIOHandler());
		if (!ports.contains(port))
			ports.add(port);
	}

	Object lock_deregister = new Object();

	private void deregister(IConnection port) {
		synchronized (lock_deregister) {
			if (port == null)
				return;

			if (!ports.remove(port))
				return;
			SelectionKey key = port.channel().keyFor(selector);
			if (key != null)
				key.cancel();
			port.dispose();
			System.gc();
		}
	}

	/**
	 * ���ʹ����¼�������IConnection
	 * 
	 * @param e
	 *        ����������Ϣ��Exception
	 */
	private void dispatchErrorToAll(Exception e, short aErrorCode) {
		for (IConnection port : ports)
			port.getNIOHandler().processError(e, aErrorCode);
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
			handler = (ports.get(i)).getNIOHandler();
			handler.processWrite();
		}
	}

	/**
	 * ������תά������ע���IConnection����. ͨ���������ǵļ��������ֱ�������շ��Ͷ���/�����ն���/ά�����еĹ���.
	 * 
	 * @see IConnection#send(ByteBuffer)
	 * @see IConnection#receive(ByteBuffer)
	 * @see IConnection#maintain()
	 */
	Object lock_run = new Object();

	public void run() {
		synchronized (lock_run) {
			int n = 0;
			while (!shutdown) {
				try {
					Thread.sleep(80);
					if (true) {
						// do select
						try {
							if (selector != null)
								n = selector.select(10);
							LogFactory.d(TAG, "selector.select n:" + n);
							// ���Ҫshutdown���ر�selector�˳�
							if (shutdown) {
								selector.close();
								break;
							}
						} catch (IOException e) {
							e.printStackTrace();
						}

						// ���������ͷ�����
						processDisposeQueue();

						// ���select���ش���0�������¼�
						if (n > 0) {
							for (Iterator<SelectionKey> i = selector.selectedKeys().iterator(); i.hasNext();) {
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
										LogFactory.d(TAG, "sk.isConnectable()");
										handler.processConnect(sk);
									} else if (sk.isReadable()) {
										LogFactory.d(TAG, "sk.isReadable()");
										handler.processRead(sk);
									}
								} catch (IOException e) {
									e.printStackTrace();
								} catch (RuntimeException e) {
									e.printStackTrace();
								}
							}

							n = 0;
						}

						checkNewConnection();
						notifySend();
					}
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

			selector = null;
			shutdown = false;
			LogFactory.e("debug", "Porter�Ѿ��˳�");
		}
	}

	/**
	 * ����ͷ�����
	 * 
	 * @param p
	 */
	public void addDisposeRequest(IConnection p) {
		synchronized (disposeQueue) {
			disposeQueue.offer(p);

			LogFactory.e("disposeQueue", "name --->" + "disposeQueue size :" + disposeQueue.size());
		}
	}

	/**
	 * ����Ƿ���������Ҫ����
	 */
	Object lock_checkNewConnection = new Object();

	private void checkNewConnection() {
		synchronized (lock_checkNewConnection) {
			while (!newConnections.isEmpty()) {
				Object handler = newConnections.remove(0);
				if (handler instanceof IConnection) {
					try {
						register((IConnection) handler);
						((IConnection) handler).start();
					} catch (ClosedChannelException e1) {
						e1.printStackTrace();
					}
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

	// IPortӳ������key�����ƣ�value��IPort����
	private Map<String, IConnection> registry;
	// ���ü���
	private Map<IConnection, Integer> references;

	private Object lock_getConnections = new Object();

	public List<IConnection> getConnections() {
		synchronized (lock_getConnections) {
			List<IConnection> ret = new ArrayList<IConnection>();
			ret.addAll(registry.values());
			return ret;
		}
	}

	public boolean hasConnection(String id) {
		return registry.containsKey(id);
	}

	private Object lock_release = new Object();

	public void release(IConnection con, boolean allRelease) {
		synchronized (lock_release) {
			if (con == null)
				return;

			Iterator<Map.Entry<IConnection, Integer>> it = references.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry<IConnection, Integer> entry = (Map.Entry<IConnection, Integer>) it.next();
				IConnection iCon = (IConnection) entry.getKey();

				if (iCon.getId().equals(EngineConst.IMO_CONNECTION_ID) && allRelease == false) {

				} else {
					references.remove(iCon);
					registry.remove(iCon.getId());

					iCon.dispose();
				}
			}
		}
	}

	private Object lock_release_id = new Object();

	public void release(String id, boolean allRelease) {
		synchronized (lock_release_id) {

			DataEngine.getInstance().clearInQueue();
			DataEngine.getInstance().clearSendQueue();
			DataEngine.getInstance().clearTimeoutQueue();

			IConnection con = getConnection(id);
			release(con, allRelease);
			LogFactory.e("NIOThread", "release registry size :" + registry.size());
		}
	}

	public IConnection getConnection(String id) {
		IConnection conn = registry.get(id);
		return conn;
	}

	/**
	 * ����Ƿ����һ����ָ��Զ�̵�ַ������
	 * 
	 * @param address
	 * @return
	 */
	public IConnection getConnection(InetSocketAddress address) {
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
	private Object lock_increaseReference = new Object();

	private void increaseReference(IConnection con) {
		synchronized (lock_increaseReference) {
			Integer i = references.get(con);
			if (i != null) {
				i++;
				LogFactory.d(TAG, "increaseReference:" + i);
				references.put(con, i);
			}
		}
	}

	/**
	 * ʹ��ָ����port����һ����
	 * 
	 * @param name
	 *        port name
	 * @param packet
	 *        OutPacket����
	 * @param keepSent
	 *        true��ʾ���淢���İ���������Ҫ����Ϊ��ЩЭ��ķ��ذ�û��ʲô������Ϣ����Ҫʹ�÷������� �����¼�
	 * @return true��ʾ�����ͳɹ���false��ʾʧ��
	 */
	public void send(String id, OutPacket packet, boolean keepSent) {
		IConnection port = getConnection(id);
		if (port != null) {
			DataEngine.getInstance().getOutQueue().add(packet);
			LogFactory.e(TAG, "id:" + id + ",add packet <" + packet.getCommand() + "> to port,and isStartRelogin = " + EngineConst.isStartRelogin);
		} else {
			LogFactory.e(TAG, "send port is null");
		}
	}

	public synchronized IConnection newUDPConnection(String id, InetSocketAddress server, boolean start) {
		return null;
	}

	private Object lock_newTCPConnection = new Object();

	public IConnection newTCPConnection(String id, InetSocketAddress server, boolean start) {
		synchronized (lock_newTCPConnection) {
			LogFactory.d(TAG, "hasConnection(id:+" + id + "+):" + hasConnection(id));
			if (hasConnection(id)) {
				IConnection con = getConnection(id);
				increaseReference(con);
				return con;
			}

			TCPConnection port;
			try {
				port = new TCPConnection(id, server);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			registry.put(id, port);
			LogFactory.e(TAG, "registry size :" + registry.size());
			references.put(port, 1);
			wakeup(port);
			return port;
		}
	}

	public synchronized IConnection newHTTPConnection(String id, String server, int port, String host, boolean start, PacketsObserver aObserver) {
		// if (hasConnection(id)) {
		// IConnection con = getConnection(id);
		// increaseReference(con);
		// /* Http�Ƕ����ӣ��ݲ�֧�����Ӹ���* */
		// // return con;
		// }
		// InetSocketAddress IMO_SERVER_ADDRESS = new InetSocketAddress(server,
		// port);
		//
		// HTTPConnection httpport;
		// try {
		// httpport = new HTTPConnection(id, IMO_SERVER_ADDRESS, host);
		// } catch (IOException e) {
		// e.printStackTrace();
		// return null;
		// }
		// registry.put(id, httpport);
		// references.put(httpport, 1);
		// wakeup(httpport);
		// if (start)
		// httpport.start();
		// return httpport;
		return null;
	}

	/*
	 * (non-Javadoc)
	 */
	public void dispose() {
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
}
