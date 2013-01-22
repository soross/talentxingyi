package talent.xingyi.sort;

public class BubbleSort {

	public static void main(String[] args) {
		int[] sortdata = new int[] { -100, 34, 2, 50 };
		FuncBubbleSort(sortdata);

		for (int i = 0; i < sortdata.length; i++) {
			System.out.println(sortdata[i]);
		}
	}

	// 从头开始向后依次比较相邻元素，将最大值推到数组尾部
	private static void FuncBubbleSort(int[] data) {
		if (data == null || data.length == 1) {
			return;
		}

		for (int i = 0, len = data.length; i < len; i++) {
			for (int j = i; j < len - 1 && data[j] > data[j + 1]; j++) {
				int tmp = data[j];
				data[j] = data[j + 1];
				data[j + 1] = tmp;
			}
		}
	}

}
