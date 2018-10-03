package cj.studio.netos.framework.util;

public class StringUtil {
	public static void main(String...strings){
		String t="我操";
		t=string2Unicode(t);
		System.out.println(unicode2String(t));
	}
	public static String string2Unicode(String string) {
		StringBuffer unicode = new StringBuffer();
		for (int i = 0; i < string.length(); i++) {
			// 取出每一个字符
			char c = string.charAt(i);
			// 转换为unicode
			unicode.append("\\u" + Integer.toHexString(c));
		}
		return unicode.toString();
	}

	public static String unicode2String(String unicode) {
		StringBuffer string = new StringBuffer();
		String[] hex = unicode.split("\\\\u");
		for (int i = 1; i < hex.length; i++) {
			// 转换出每一个代码点
			int data = Integer.parseInt(hex[i], 16);
			// 追加成string
			string.append((char) data);
		}
		return string.toString();
	}

	/**
	 * 包相等，或互为子包视为冲突。
	 * 
	 * <pre>
	 *
	 * </pre>
	 * 
	 * @param pack1
	 * @param pack2
	 * @return
	 */
	public static boolean isPackageConflict(String pack1, String pack2) {
		if (pack1.equals(pack2))
			return true;
		String name1 = pack1.contains(".")
				? pack1.substring(pack1.lastIndexOf("."), pack1.length())
				: pack1;
		String name2 = pack2.contains(".")
				? pack2.substring(pack2.lastIndexOf("."), pack2.length())
				: pack2;
		if (name1.equals(name2)
				&& (pack1.startsWith(pack2) || pack2.startsWith(pack1)))
			return true;
		return false;
	}

	public static boolean isEmpty(String str) {
		return ((null == str) || ("".equals(str)));
	}

	// 求两个字串的最大的公共子串
	public static String getCommonSub(String str1, String str2) {

		String bigStr = null;
		String smallStr = null;
		if (str1.length() - str2.length() > 0) {
			bigStr = str1;
			smallStr = str2;
		} else {
			bigStr = str2;
			smallStr = str1;
		}

		for (int i = smallStr.length(); i > 0; i--) {
			int begain = 0;
			int end = i;
			for (int j = smallStr.length() - i; j >= 0; j--) {
				String result = smallStr.substring(begain, end);
				if (bigStr.contains(result)) {

					return result;

				}

				begain++;
				end++;
			}
		}
		return null;

	}
}
