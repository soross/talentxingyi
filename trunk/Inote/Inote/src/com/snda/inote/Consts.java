package com.snda.inote;

import java.util.HashMap;
import java.util.Map;

public class Consts {

    //URLs
    public static final String URL_BASE = "http://api.note.sdo.com/";
//    public static final String URL_BASE = "http://api.noteint.sdo.com/";
    public static final String FILE_URL_BASE = "http://files.note.sdo.com/";
    public static final String URL_CATEGORY_LIST = URL_BASE + "CategoryService.asmx/List";
    public static final String URL_CATEGORY_ADD = URL_BASE + "CategoryService.asmx/Create";
    public static final String URL_CATEGORY_DEL = URL_BASE + "CategoryService.asmx/Delete";
    public static final String URL_CATEGORY_GET = URL_BASE + "CategoryService.asmx/Get";
    public static final String URL_CATEGORY_UPDATE = URL_BASE + "CategoryService.asmx/Update";
    public static final String URL_NOTE_LIST = URL_BASE + "NoteService.asmx/List";
    public static final String URL_NOTE_DETAIL = URL_BASE + "NoteService.asmx/Get";
    public static final String URL_NOTE_ADD = URL_BASE + "NoteService.asmx/Create";
    public static final String URL_NOTE_EDIT = URL_BASE + "NoteService.asmx/Update";
    public static final String URL_NOTE_LISTATTACHMENTS = URL_BASE + "NoteService.asmx/ListAttachments";
    public static final String URL_NOTE_DEL = URL_BASE + "NoteService.asmx/Delete";
    public static final String URL_USER_GET = URL_BASE + "UserService.asmx/GetProfile";
    public static final String URL_FILE_UPLOAD = URL_BASE + "SaveAttachment.ashx";
    public static final String URL_USER_LOGIN = URL_BASE + "AccountService.asmx/Login";
    public static final String URL_SYNC_CHANGE = URL_BASE + "SyncService.asmx/GetChanges";

    public static final String URL_IFRAME_LOGIN = URL_BASE + "clientlogin.aspx?client=android";
    public static final String URL_LOGIN = URL_BASE +"AccountService.asmx/Login";
    public static final String URL_APP_VERSION_CHECK = URL_BASE +"clientupdate.ashx";
    public static final String URL_ACTIVATE = URL_BASE +"AccountService.asmx/Activate";
    public static final String PATH_BASE = "maiku/";
    public static final String PATH_CACHE = PATH_BASE + "cache/";
    public static final String PATH_TEMP = "maiku/temp/";
    public static final String PATH_IMAGE_CACHE = PATH_CACHE + ".image/";
    public static final String PATH_FILE_CACHE = PATH_CACHE + ".file/";
    public static final String PATH_FILE_LOCALCACHE = PATH_CACHE + ".localfile/";
    public static final String PATH_CACHE_CATEGORY = PATH_CACHE + "category/";
    public static final String PATH_CACHE_LIST = PATH_CACHE + "list/";
    public static final String PATH_CACHE_NOTE = PATH_CACHE + "note/";

    //UserInfo
    public static final String USER_TOKEN = "token";
    public static final String USER_TOKEN_DEFAULT = "testinotetoken";
    public static final String USER_SNDAID = "sndaid";

    //Setting
    public static final String FILE_SETTING = "settings";


    public static final int RESULT_STATUS_ADD_NOTE = 5;

    public static final String[] FILE_UPLOAD_ALLOW = new String[]{
            "jpg", "jpeg", "gif", "png", "doc", "docx", "ppt", "pptx", "pdf", "xls", "xlsx", "exe",
            "rar", "zip", "swf", "mp3", "wma", "txt", "tiff", "bmp", "html", "mp4", "amr", "wav","apk"
    };

    public static final Map<String, String> nameAndMine = new HashMap<String, String>();
    
    static {
        nameAndMine.put("apk","application/vnd.android.package-archive");
        nameAndMine.put("jpg", "image/jpeg");
        nameAndMine.put("jpeg", "image/jpeg");
        nameAndMine.put("gif", "image/gif");
        nameAndMine.put("png", "image/png");
        nameAndMine.put("doc", "application/msword");
        nameAndMine.put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        nameAndMine.put("ppt", "application/mspowerpoint");
        nameAndMine.put("pptx", "application/mspowerpoint");
        nameAndMine.put("pdf", "application/pdf");
        nameAndMine.put("xls", "application/msexcel");
        nameAndMine.put("xlsx", "application/msexcel");
        nameAndMine.put("swf", "application/x-shockwave-flash");
        nameAndMine.put("mp3", "audio/mp3");
        nameAndMine.put("wma", "audio/x-ms-wma");
        nameAndMine.put("wav", "audio/x-wav");
        nameAndMine.put("amr", "audio/amr");
        nameAndMine.put("txt", "text/plain");
        nameAndMine.put("tiff", "image/tiff");
        nameAndMine.put("bmp", "image/bmp");
        nameAndMine.put("html", "text/html");
        nameAndMine.put("htm", "text/html");
        nameAndMine.put("mp4", "video/mp4");
        nameAndMine.put("java", "text/html");
    }

    /**
     * 添加视图中的文字数组
     */
    public static final int[] ADD_TEXTS = {
            R.string.add_simple_note,
            R.string.add_full_note,
            R.string.add_camera_note,
            R.string.add_voice_file_note,
            R.string.add_file_note
    };


    /**
     * 添加视图中的ICON
     */
    public static final int[] ADD_ICONS = {
            R.drawable.add_icon1,
            R.drawable.add_icon2,
            R.drawable.add_icon4,
            R.drawable.add_icon5,
            R.drawable.add_icon3
    };

    /**
     * 附加的文字数组
     */
    public static final int[] APPEND_TEXTS = {
            R.string.add_camera_note,
            R.string.add_voice_file_note,
            R.string.add_file_note
    };

    /**
     * 附加视图中的ICON
     */
    public static final int[] APPEND_ICONS = {
            R.drawable.add_icon4,
            R.drawable.add_icon5,
            R.drawable.add_icon3
    };


    public static final String SERVICE_MSG_CATEGORY = "s_category";
    public static final String SERVICE_MSG_NOTELIST = "s_notelist";
    public static final String SERVICE_MSG_ALL = "s_all";
    public static final String SERVICE_MSG_CHANGE = "s_change";
    public static final String SERVICE_MSG_ERROR = "s_error";
    public static final String SERVICE_MSG_IOERROR = "s_io_error";
    public static final String SERVICE_MSG_REQUEST_ERROR = "http_api_error";
    public static final String SERVICE_NOT_CONNECTION_ERROR = "s_not_connection_error";
    public static final String SERVICE_BEGIN = "servie_begin";
    public static final String SERVICE_END = "servie_end";

    public static final int CHANGE_CREATE = 1;
    public static final int CHANGE_UPDATE = 2;
    public static final int CHANGE_DELETE = 3;
    public static final int CHANGE_TYPE_LOCAL = 0;
    public static final int CHANGE_TYPE_REMOTE = 1;

    public static final int CHANGE_CATEGORY_NOTE=1;
    public static final int CHANGE_CATEGORY_CATEGORY=2;
    public static final int CHANGE_CATEGORY_ATTACH=3;
}
