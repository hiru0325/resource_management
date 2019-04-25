package util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Cipher_util
{
	/*
	 * 暗号化アルゴリズム ： 「AES」
	 *暗号化利用モード ： 「CBC」
	 *パディング方式 ： 「PKCS5Padding」
	 */

	//↓ 暗号化キー指定
	private static final byte[] key = "abcdefghijklmnop".getBytes();

	//↓ 外部公開用の暗号化メソッド
	public static final String encode(String plainText)
	{
		SecureRandom random;	//← 暗号化乱数
		byte iv[] = null;		//← 初期化ベクトル

		try
		{
			//↓ 暗号化方式、暗号利用モード、パディング方式を指定する。
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			//↓ 乱数方式の設定
			random = SecureRandom.getInstance("SHA1PRNG");

			//↓ 設定した暗号鍵長の配列を設定
			iv = new byte[cipher.getBlockSize()];
			//↓ 暗号化乱数を付与
			random.nextBytes(iv);
		}
		catch(NoSuchPaddingException e)
		{
			e.printStackTrace();
		}
		catch(NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		//↓ 実際の暗号化処理
		return encode(plainText, iv);
	}
	//↓ 実際の暗号化処理
	private static final String encode(String plainText, byte[] iv)
	{
		String encryptText = null; //← 暗号化後の文字列

		try
		{
			//↓ 初期化ベクトル(IV)をセットする。
			AlgorithmParameterSpec ivSpec = new IvParameterSpec(iv);

			//↓ 暗号化方式、暗号利用モード、パディング方式を指定する。
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			//↓ 暗号化方式などをセットして暗号化キーを生成
			cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, "AES"), ivSpec);

			//↓ 暗号化対象の文字列を暗号化する。
			byte[] cryptogram = cipher.doFinal(plainText.getBytes("UTF-8"));

			//↓ 暗号化の結果を格納する変数を生成
			byte[] byteResult = new byte[iv.length + cryptogram.length];

			//↓ byteResult変数に対して、初期ベクトル + 暗号データを格納する。
			System.arraycopy(iv, 0, byteResult, 0, iv.length);
			System.arraycopy(cryptogram, 0, byteResult, iv.length, cryptogram.length);

			//↓ 暗号化されたデータに対してbase64エンコードを行い、文字列化する
			encryptText = Base64.getEncoder().encodeToString(byteResult);
		}
		catch(NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		catch(NoSuchPaddingException e)
		{
			e.printStackTrace();
		}
		catch(InvalidKeyException e)
		{
			e.printStackTrace();
		}
		catch(InvalidAlgorithmParameterException e)
		{
			e.printStackTrace();
		}
		catch(UnsupportedEncodingException e)
		{
			e.printStackTrace();
		}
		catch(BadPaddingException e)
		{
			e.printStackTrace();
		}
		catch(IllegalBlockSizeException e)
		{
			e.printStackTrace();
		}

		return encryptText;
	}

	//↓ 初期ベクトルを取得するメソッド(暗号化した文字列を比較する際、同じ初期ベクトルを付与しないと合致しない)
	private static final byte[] getIV(byte[] BaseDecode)
	{
		Cipher cipher = null;	//← 暗号化クラス
		byte[] iv = null;		//← 初期化ベクトル

		try
		{
			//↓ 暗号化方式、暗号利用モード、パディング方式を指定する。
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		}
		catch(NoSuchAlgorithmException e)
		{
			e.printStackTrace();
		}
		catch(NoSuchPaddingException e)
		{
			e.printStackTrace();
		}
		//↓ 初期化ベクトル配列初期化
		iv = new byte[cipher.getBlockSize()];

		//↓ 暗号化文字列の初期化ベクトルをコピー
		System.arraycopy(BaseDecode, 0, iv, 0, iv.length);
		return iv;
	}

	//↓ 暗号化文字列と比較対象文字列を比較する
	public static final boolean passcheck(String plainText, String encodedText)
	{
		byte[] BaseDecode = null;	//← Base64デコードした暗号化文字列

		//↓ Base64で暗号化文字列をバイトへデコード
		BaseDecode = Base64.getDecoder().decode(encodedText);
		//↓ 比較対象文字列を暗号化文字列の初期化ベクトルを基に暗号化
		String encodedFromPlain = encode(plainText, getIV(BaseDecode));

		//↓ 比較結果をBooleanで返す
		return encodedText.equals(encodedFromPlain);
	}

	/*
	//↓ 文字列暗号化用メイン関数
	public static void main(String[] args)
	{
		//↓ 暗号化した文字列を代入
		String plainText = "sample_2";
		String encryptText = null;

		encryptText = encode(plainText);
		//↓ 暗号化前 ： 「暗号化後」
		System.out.println(plainText + ":" + "「" + encryptText + "」");
	}
	*/
}
