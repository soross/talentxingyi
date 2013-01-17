package org.talentware.android.comm.network.encrypt;

public class RegexItem {
	private int start;
	private int end;
	private String content;
	private int itemType;
	
	public int getStart() {
		return start;
	}
	
	public void setStart(int start) {
		this.start = start;
	}
	
	public int getEnd() {
		return end;
	}
	
	public void setEnd(int end) {
		this.end = end;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public int getItemType() {
		return itemType;
	}
	
	public void setItemType(short itemType) {
		this.itemType = itemType;
	}
	
	public RegexItem(int aStart,int aEnd,String aContent,int aItemType)
	{
		this.start = aStart;
		this.end = aEnd;
		this.content = aContent;
		this.itemType = aItemType;
	}

}
