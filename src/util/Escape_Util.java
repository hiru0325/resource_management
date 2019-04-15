package util;

public class Escape_Util
{
	//↓ ユーザによる入力値のサニタイジング(XSS(クロスサイトスクリプティング)対策)
	public static String Escape_Html(String InputText)
	{
		//↓ 入力値がnullの場合、空白を返す
		if(InputText == null)
			return "";
		//↓ サニタイジング
		InputText = InputText.replaceAll("&", "& amp;");
		InputText = InputText.replaceAll("<", "& lt;");
		InputText = InputText.replaceAll("<", "& gt;");
		InputText = InputText.replaceAll("\"", "&quot;");
		InputText = InputText.replaceAll("'", "&apos;");

		//↓ サニタイジングした文字列を返す
		return InputText;
	}
}
