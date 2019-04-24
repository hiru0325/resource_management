package login;

public class Login_auth_property
{
	private String DB_Driver;																//← 接続用ドライバー
	private String DB_Url;																	//← 接続先URL
	private String DB_User;																	//← 接続用ユーザ名
	private String DB_Password;																//← 接続用パスワード

	public Login_auth_property()
	{
		DB_Driver = "com.mysql.cj.jdbc.Driver";												//← 接続用ドライバー
		DB_Url = "jdbc:mysql://localhost:3306/RESOURCE_MANAGEMENT?serverTimezone=JST";		//← 接続先URL
		DB_User = "matsumotohru";															//← 接続用ユーザ名
		DB_Password = "matsumotohru";
	}

	//↓ 接続用ドライバー(ゲッター)
	public String get_DB_Driver()
	{
		return DB_Driver;
	}

	//↓ 接続先URL(セッター)
	void set_DB_Url(String DB_Url)
	{
		this.DB_Url = DB_Url;
	}

	//↓ 接続URL(ゲッター)
	public String get_DB_Url()
	{
		return DB_Url;
	}

	//↓　接続用ユーザ名(セッター)
	void set_DB_User(String DB_User)
	{
		this.DB_User = DB_User;
	}

	//↓　接続用ユーザ名(ゲッター)
	public String get_DB_User()
	{
		return DB_User;
	}

	//↓　接続用パスワード(セッター)
	void set_DB_Password(String DB_Password)
	{
		this.DB_Password = DB_Password;
	}

	//↓　接続用パスワード(ゲッター)
	public String get_DB_Password()
	{
		return DB_Password;
	}
}
