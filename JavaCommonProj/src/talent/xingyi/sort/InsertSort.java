package talent.xingyi.sort;

public class InsertSort {

	public static void main(String[] args) {
		int[] sortdata = new int[] { -100, 34, 2, 50 };
		FuncInsertSort(sortdata);

		for (int i = 0; i < sortdata.length; i++) {
			System.out.println(sortdata[i]);
		}
	}

	// 与前边排序好的子序列比较，向前依次比较相邻元素，将元素推到正确位置
	private static void FuncInsertSort(int[] data) {
		if (data == null || data.length == 1) {
			return;
		}

		for (int i = 0, len = data.length; i < len; i++) {
			for (int j = i; j > 0 && data[j] < data[j - 1]; j--) {
				int tmp = data[j];
				data[j] = data[j - 1];
				data[j - 1] = tmp;
			}
		}
	}

}
