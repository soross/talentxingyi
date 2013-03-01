package com.imo.network.Encrypt;

public class XTEAJNI {
	
	public native void encipher_block(int rounds, int[] key, int[] in, int[] out);
	public native void decipher_block(int rounds, int[] key, int[] in, int[] out);
	public native void encipher(char[] key, char[] in, int in_len, char[] out, int out_buf_len,int out_len);
	public native void decipher(char[] key, char[] in, int in_len, char[] out, int out_buf_len,int out_len);
	
	static
    {
        System.loadLibrary("AsyncEngineAndroid_Encrypt_XTEAJNI");
    }
}
