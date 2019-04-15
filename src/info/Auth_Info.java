package info;

//↓ 認証情報格納
public class Auth_Info
{
	private String Login_User;				//← 認証に成功したユーザ名
	private String Result_Content;			//← 認証結果内容
	private int Error_Code;				//← エラーコード

	public Auth_Info()
	{
		Login_User = "";
		Result_Content = "false";
		Error_Code = 0;
	}

	//↓ Login_Userのプロパティ
	public void setLogin_User(String tmp_Login_User)
	{
		Login_User = tmp_Login_User;
	}
	public String getLogin_User()
	{
		return Login_User;
	}

	//↓ Result_Contentのプロパティ
	public void setResult_Content(String tmp_Result_Content)
	{
		Result_Content = tmp_Result_Content;
	}
	public String getResult_Content()
	{
		return Result_Content;
	}

	//↓ Error_Codeのプロパティ
	public void setError_Code(int tmp_Error_Code)
	{
		Error_Code = tmp_Error_Code;
	}
	public int getError_Code()
	{
		return Error_Code;
	}
}
