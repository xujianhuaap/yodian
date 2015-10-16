package maimeng.yodian.app.client.android.view.deal.pay;import android.text.TextUtils;import maimeng.yodian.app.client.android.utils.LogUtil;public class PayResult {	private String resultStatus;	private String result;	private String memo;	public PayResult(String rawResult) {		if (TextUtils.isEmpty(rawResult))			return;		String[] resultParams = rawResult.split(";");		for (String resultParam : resultParams) {			if (resultParam.startsWith("resultStatus=")) {				resultStatus = gatValue(resultParam, "resultStatus=");			}			if (resultParam.startsWith("result=")) {				result = gatValue(resultParam, "result=");			}			if (resultParam.startsWith("memo=")) {				memo = gatValue(resultParam, "memo=");			}		}	}	@Override	public String toString() {		return "resultStatus={" + resultStatus + "};memo={" + memo				+ "};result={" + result + "}";	}	private String gatValue(String content, String key) {		LogUtil.d(PayResult.class.getName(),""+content);		int endIndex=content.lastIndexOf("}");		if(!content.endsWith("}")){			endIndex=content.length();		}		String prefix = key + "{";		 String s=content.substring(content.indexOf(prefix) + prefix.length(),				endIndex);		return s;	}	/**	 * @return the resultStatus	 */	public String getResultStatus() {		return resultStatus;	}	/**	 * @return the memo	 */	public String getMemo() {		return memo;	}	/**	 * @return the result	 */	public String getResult() {		return result;	}}