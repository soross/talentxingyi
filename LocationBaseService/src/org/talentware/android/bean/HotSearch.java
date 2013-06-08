package org.talentware.android.bean;

/**
 * Created with IntelliJ IDEA.
 * User: yixing
 * Date: 13-6-8
 * Time: 下午6:28
 * To change this template use File | Settings | File Templates.
 */
public class HotSearch {

    private int HotSearchId = -1;

    private String HotSearchKey = "";

    private String HotSearchTags = "";

    private String HotSearchDisplayName = "";

    public int getHotSearchId() {
        return HotSearchId;
    }

    public void setHotSearchId(int hotSearchId) {
        HotSearchId = hotSearchId;
    }

    public String getHotSearchKey() {
        return HotSearchKey;
    }

    public void setHotSearchKey(String hotSearchKey) {
        HotSearchKey = hotSearchKey;
    }

    public String getHotSearchTags() {
        return HotSearchTags;
    }

    public void setHotSearchTags(String hotSearchTags) {
        HotSearchTags = hotSearchTags;
    }

    public String getHotSearchDisplayName() {
        return HotSearchDisplayName;
    }

    public void setHotSearchDisplayName(String hotSearchDisplayName) {
        HotSearchDisplayName = hotSearchDisplayName;
    }

    @Override
    public String toString() {
        return "HotSearch{" +
                "HotSearchId=" + HotSearchId +
                ", HotSearchKey='" + HotSearchKey + '\'' +
                ", HotSearchTags='" + HotSearchTags + '\'' +
                ", HotSearchDisplayName='" + HotSearchDisplayName + '\'' +
                '}';
    }

    public HotSearch(int hotSearchId, String hotSearchKey, String hotSearchTags, String hotSearchDisplayName) {
        HotSearchId = hotSearchId;
        HotSearchKey = hotSearchKey;
        HotSearchTags = hotSearchTags;
        HotSearchDisplayName = hotSearchDisplayName;
    }
}
