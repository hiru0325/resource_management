package info;

public class Resrc_Select_Info
{
	private int his_id;					//← 履歴管理番号
	private String resource_name;		//← リソース名
	private String resource_dir;		//← リソース格納ディレクトリパス
	private String update_by_name;		//← 更新者名
	private String update_day;			//← 更新日
	private String create_by_name;		//← 作成者名
	private String first_update_day;	//← 初回更新日

	//↓ 初期化
	public Resrc_Select_Info()
	{
		his_id = 0;					//← 履歴管理番号
		resource_name = "";			//← リソース名
		resource_dir = "";			//← リソースディレクトリパス
		update_by_name = "";		//← 更新者名
		update_day = "";			//← 更新日
		create_by_name = "";		//← 作成者名
		first_update_day = "";		//← 初回更新日
	}

	//↓ 履歴管理番号プロパティ
	public void setResrc_His_id(int tmp_his_id)
	{
		his_id = tmp_his_id;
	}
	public int getResrc_His_id()
	{
		return his_id;
	}

	//↓ リソース名プロパティ
	public void setResrc_Name(String tmp_resource_name)
	{
		resource_name = tmp_resource_name;
	}
	public String getResrc_Name()
	{
		return resource_name;
	}

	//↓ リソースディレクトリパスプロパティ
	public void setResrc_Dir(String tmp_resource_dir)
	{
		resource_dir = tmp_resource_dir;
	}
	public String getResrc_Dir()
	{
		return resource_dir;
	}

	//↓ 更新者名プロパティ
	public void setUpdate_By_Name(String tmp_update_by_name)
	{
		update_by_name = tmp_update_by_name;
	}
	public String getUpdate_By_Name()
	{
		return update_by_name;
	}

	//↓ 更新日プロパティ
	public void setUpdate_Day(String tmp_update_day)
	{
		update_day = tmp_update_day;
	}
	public String getUpdate_Day()
	{
		return update_day;
	}

	//↓ 作成者名プロパティ
	public void setCreate_By_Name(String tmp_create_by_name)
	{
		create_by_name = tmp_create_by_name;
	}
	public String getCreate_By_Name()
	{
		return create_by_name;
	}

	//↓ 初回更新日プロパティ
	public void setFirst_Update_Day(String tmp_first_update_day)
	{
		first_update_day = tmp_first_update_day;
	}
	public String getFirst_Update_Day()
	{
		return first_update_day;
	}
}


