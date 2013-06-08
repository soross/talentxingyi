package com.snda.inote.fileselector;

import com.snda.inote.R;


/* 文件选择器：Model
 * @Author KevinComo@gmail.com
 * 2010-6-30
 */

public class FileData {
	
	private static final int[] iconValue = {
		R.drawable.ai,
		R.drawable.asp,
		R.drawable.avi,
		R.drawable.bmp,
		R.drawable.css,
		R.drawable.doc,
		R.drawable.exe,
		R.drawable.fla,
		R.drawable.folder,
		R.drawable.generic,
		R.drawable.gif,
		R.drawable.html,
		R.drawable.jpg,
		R.drawable.js,
		R.drawable.jsp,
		R.drawable.mid,
		R.drawable.mov,
		R.drawable.mp3,
		R.drawable.mp4,
		R.drawable.mpeg,
		R.drawable.mpg,
		R.drawable.pdf,
		R.drawable.php,
		R.drawable.png,
		R.drawable.ppt,
		R.drawable.psd,
		R.drawable.ram,
		R.drawable.rar,
		R.drawable.swf,
		R.drawable.tiff,
		R.drawable.txt,
		R.drawable.up,
		R.drawable.vsd,
		R.drawable.wav,
		R.drawable.wma,
		R.drawable.xls,
		R.drawable.zip,
        R.drawable.zip
	};
	private static final String[] iconKey = {
		"ai",
		"asp",
		"avi",
		"bmp",
		"css",
		"doc",
		"exe",
		"fla",
		"folder",
		"generic",
		"gif",
		"html",
		"jpg",
		"js",
		"jsp",
		"mid",
		"mov",
		"mp3",
		"mp4",
		"mpeg",
		"mpg",
		"pdf",
		"php",
		"png",
		"ppt",
		"psd",
		"ram",
		"rar",
		"swf",
		"tiff",
		"txt",
		"up",
		"vsd",
		"wav",
		"wma",
		"xls",
		"zip",
        "apk"
	};
	
	private static int getIndexInKey(String key){
		for(int i = 0; i < iconKey.length; i++){
			if(key.equalsIgnoreCase(iconKey[i])) return i;
		}
		return -1;
	}
	
	private String name;
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
	
	private FileType type;
	public FileType getType(){
		return type;
	}
	public void setType(String type){
		this.setType(this.getTypeByString(type));
	}
	public void setType(FileType type){
		this.type = type;
	}
	
	private Integer size;
	public Integer getSize(){
		return size;
	}
	public void setSize(Integer size){
		this.size = size;
	}
	
	public int getIcon(){
		return iconValue[getIndexInKey(this.type.name())];
	}
	
	private FileType getTypeByString(String v){
		v = v.toLowerCase();
		int index = getIndexInKey(v);
		if(index > -1){
			return FileType.valueOf(v.toUpperCase());
		} else{
			if(v.equals("aspx")) return FileType.ASP;
			if(v.equals("docx")) return FileType.DOC;
			if(v.equals("htm") || v.equals("shtml")) return FileType.HTML;
			if(v.equals("jpeg")) return FileType.JPG;
			if(v.equals("midi")) return FileType.MID;
			if(v.equals("pptx")) return FileType.PPT;
			if(v.equals("rm") || v.equals("rmvb")) return FileType.RAM;
			if(v.equals("xlsx")) return FileType.XLS;
            if(v.equals("apk")) return FileType.ZIP;
		}
		return FileType.GENERIC;
	}
}
