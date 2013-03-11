package com.imo.module.dialogue;

import android.content.Context;

import com.imo.R;

public class Emotion {
	public Context context;

	public Emotion(Context context) {
		this.context = context;
	}

	public final int[] emotion_ids = {
			R.drawable.emotion1, R.drawable.emotion2, R.drawable.emotion3, R.drawable.emotion5, R.drawable.emotion4, R.drawable.emotion6, R.drawable.emotion7, R.drawable.emotion8, R.drawable.emotion9, R.drawable.emotion10, R.drawable.emotion11, R.drawable.emotion12, R.drawable.emotion13,
			R.drawable.emotion14, R.drawable.emotion15, R.drawable.emotion16, R.drawable.emotion17, R.drawable.emotion18, R.drawable.emotion19, R.drawable.emotion20, R.drawable.emotion21, R.drawable.emotion22, R.drawable.emotion23, R.drawable.emotion24, R.drawable.emotion25, R.drawable.emotion26,
			R.drawable.emotion27, R.drawable.emotion28, R.drawable.emotion29, R.drawable.emotion30, R.drawable.emotion31, R.drawable.emotion32, R.drawable.emotion33, R.drawable.emotion34, R.drawable.emotion35, R.drawable.emotion36, R.drawable.emotion37, R.drawable.emotion38, R.drawable.emotion39,
			R.drawable.emotion40, R.drawable.emotion41, R.drawable.emotion42, R.drawable.emotion43, R.drawable.emotion44, R.drawable.emotion45, R.drawable.emotion46, R.drawable.emotion47, R.drawable.emotion48, R.drawable.emotion49, R.drawable.emotion50, R.drawable.emotion51, R.drawable.emotion52,
			R.drawable.emotion53, R.drawable.emotion54, R.drawable.emotion55, R.drawable.emotion56, R.drawable.emotion57, R.drawable.emotion58, R.drawable.emotion59, R.drawable.emotion60, R.drawable.emotion61, R.drawable.emotion62, R.drawable.emotion63, R.drawable.emotion64, R.drawable.emotion65,
			R.drawable.emotion66, R.drawable.emotion67, R.drawable.emotion68, R.drawable.emotion69, R.drawable.emotion70, R.drawable.emotion71, R.drawable.emotion72, R.drawable.emotion73, R.drawable.emotion74, R.drawable.emotion75, R.drawable.emotion76, R.drawable.emotion77, R.drawable.emotion78,
			R.drawable.emotion79, R.drawable.emotion80, R.drawable.emotion81, R.drawable.emotion82, R.drawable.emotion83, R.drawable.emotion84, R.drawable.emotion85, R.drawable.emotion86, R.drawable.emotion87, R.drawable.emotion88
	};
	public final String[] emotion_indexs = {
			"{[101]}", "{[102]}", "{[103]}", "{[104]}", "{[105]}", "{[201]}", "{[202]}", "{[107]}", "{[113]}", "{[203]}", "{[204]}", "{[109]}", "{[106]}", "{[205]}", "{[206]}", "{[207]}", "{[110]}", "{[208]}", "{[209]}", "{[210]}", "{[211]}", "{[212]}", "{[213]}", "{[108]}", "{[111]}", "{[114]}",
			"{[214]}", "{[115]}", "{[215]}", "{[116]}", "{[216]}", "{[112]}", "{[217]}", "{[218]}", "{[219]}", "{[220]}", "{[221]}", "{[222]}", "{[223]}", "{[224]}", "{[225]}", "{[226]}", "{[117]}", "{[118]}", "{[119]}", "{[120]}", "{[121]}", "{[122]}", "{[123]}", "{[127]}", "{[124]}", "{[254]}",
			"{[255]}", "{[125]}", "{[126]}", "{[128]}", "{[129]}", "{[130]}", "{[131]}", "{[132]}", "{[133]}", "{[227]}", "{[228]}", "{[229]}", "{[230]}", "{[231]}", "{[232]}", "{[233]}", "{[234]}", "{[235]}", "{[236]}", "{[237]}", "{[238]}", "{[239]}", "{[240]}", "{[241]}", "{[242]}", "{[243]}",
			"{[244]}", "{[245]}", "{[246]}", "{[247]}", "{[249]}", "{[250]}", "{[251]}", "{[252]}", "{[253]}", "{[248]}"
	};

	public final String[] emotion_texts = {
			"微笑", "偷笑", "大笑1", "调皮", "憨笑", "大笑2", "鼓掌1", "害羞", "摆酷", "得意", "抽烟", "擦汗", "难过", "难受", "不开心", "郁闷", "哭泣", "发脾气", "发怒", "抓狂", "哀", "可怜", "惊讶", "困倦", "睡觉", "疑问", "闭嘴", "晕", "不理你", "再见", "财迷", "奋斗", "囧", "冷", "吐", "鄙视", "查找", "色", "抠鼻子", "思考", "贼贼的", "认真", "握手", "OK", "鼓掌2", "真棒", "米饭",
			"蛋糕1", "美酒", "咖啡", "篮球", "足球", "羽毛球", "礼物", "邮件", "台灯", "电话", "手机", "音乐", "灯泡", "时钟", "大便", "太阳", "月亮", "下雨", "闪电", "彩虹", "雪花", "雨伞", "汽车", "苹果", "西瓜", "香蕉", "草莓", "葡萄", "蛋糕2", "饮料1", "糖", "雪糕", "饮料2", "女", "男", "神马", "Hold住", "浮云", "给力", "灰机", "有木有"
	};
}
