package org.talentware.android.comm.packet;

import java.nio.ByteBuffer;
import java.util.Random;

import org.talentware.android.comm.net.EngineConst;

public abstract class Packet {

	protected ByteBuffer header;

	protected ByteBuffer body;

	protected short command;

	protected int cid;

	protected int uid;

	protected short dataLen;

	protected int header_seq;

	public static int START_GLOBAL_UNIQUE_SEQ = 1;
	protected static int global_unique_seq = START_GLOBAL_UNIQUE_SEQ;
	protected int seq = (new Random()).nextInt();

	public short getDataLen() {
		return dataLen;
	}

	public void setDataLen(short dataLen) {
		this.dataLen = dataLen;
	}

	public int relocate(ByteBuffer buf) {
		return 0;
	}

	/** 解密密钥 */
	protected byte[] decryptKey;

	/** 加密密钥 */
	protected byte[] encryptKey;

	public Packet() {

	}

	public Packet(ByteBuffer aHeader, ByteBuffer aBody) {
		this.header = aHeader;
		this.body = aBody;
	}

	public Packet(ByteBuffer aBody, short aCommand, int aCid, int aUid) {

	}

	// ***********************************新加测试方法*******************************************//
	public Packet(ByteBuffer aBody, short aCommand) {
		this.body = aBody;
		this.command = aCommand;
		this.dataLen = (short) aBody.capacity();
	}

	// ***********************************新加测试方法*******************************************//

	public Packet(ByteBuffer aData, int aBodyLen) {
		// 解析头部
		parseHeader(aData);
	}

	protected void parseHeader(ByteBuffer buf) {
		dataLen = buf.getShort();
		command = buf.getShort();
		header_seq = buf.getInt();
		EngineConst.version = buf.get();
		cid = buf.getInt();
		uid = buf.getInt();
	}

	public int get_header_seq() {
		return header_seq;
	}

	// [Make Request Header]
	public void MakeHeader() {
		ByteBuffer headerBuffer = ByteBuffer.allocate(32);
		headerBuffer.putShort(dataLen);
		headerBuffer.putShort(command);
		header_seq = gen_new_unique_seq();
		headerBuffer.putInt(header_seq);
		headerBuffer.put(EngineConst.version);
		headerBuffer.putInt(cid);
		headerBuffer.putInt(uid);

		headerBuffer.flip();

		byte[] headerData = new byte[headerBuffer.limit() - headerBuffer.position()];
		headerBuffer.get(headerData);

		headerBuffer = null;

		header = ByteBuffer.wrap(headerData);
	}

	// [End]

	/******************************************************************************
	 * Packet.gen_new_unique_seq - Not support multi_thread access DESCRIPTION:
	 * - N/A Input: Output: Returns:
	 * 
	 * modification history -------------------- 01a, 24may2012, Davidfan
	 * written --------------------
	 ******************************************************************************/
	protected int gen_new_unique_seq() {
		global_unique_seq++;
		global_unique_seq &= 0x7FFFFFFF;
		if (global_unique_seq == 0)
			global_unique_seq = START_GLOBAL_UNIQUE_SEQ;
		return global_unique_seq;
	}

	protected int getNextSeq() {
		seq++;
		seq &= 0x7FFFFFFF;
		if (seq == 0)
			seq++;
		return seq;
	}

	protected int getHeadLength() {
		return header.position();
	}

	protected int getBodyLength() {
		return body.position();
	}

	protected void putHead(ByteBuffer buf) {
		header = buf;
	}

	protected void putBody(ByteBuffer buf) {
		body = buf;
	}

	public String toString() {
		return getPacketName();
	}

	public String toDebugString() {
		return "toDebugString not implemented!";
	}

	public boolean equals(Object obj) {
		if (obj instanceof Packet) {
			Packet packet = (Packet) obj;
			return command == packet.command && seq == packet.seq;
		} else
			return super.equals(obj);
	}

	public short getPacketLen() {
		return (short) (getHeadLength() + getBodyLength());
	}

	/**
	 * @return Returns the command.
	 */
	public short getCommand() {
		return command;
	}

	public void setCommand(short aCommand) {
		this.command = aCommand;
	}

	/**
	 * @return Returns the sequence.
	 */
	public int getSequence() {
		return seq;
	}

	public String getPacketName() {
		return "Packet Command: " + command;
	}

	// [Set header and body]
	public byte[] getHeader() {
		return header.array();
	}

	public void setHeader(byte[] header) {
		this.header = ByteBuffer.wrap(header);
	}

	public byte[] getBody() {
		return body.array();
	}

	public ByteBuffer getBodyBuffer() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = ByteBuffer.wrap(body);
	}

	public byte[] getDecryptKey(byte[] body) {
		return decryptKey;
	}

	public void setDecryptKey(byte[] decryptKey) {
		this.decryptKey = decryptKey;
	}

	public byte[] getEncryptKey(byte[] body) {
		return encryptKey;
	}

	public void setEncryptKey(byte[] encryptKey) {
		this.encryptKey = encryptKey;
	}
	// [End]

}