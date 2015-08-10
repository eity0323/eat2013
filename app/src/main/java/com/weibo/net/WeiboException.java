/*
 * Copyright 2011 Sina.
 *
 * Licensed under the Apache License and Weibo License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.open.weibo.com
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.weibo.net;


/**
 * Encapsulation a Weibo error, when weibo request can not be implemented successful.
 *
 * @author  ZhangJie (zhangjie2@staff.sina.com.cn)
 */
public class WeiboException extends Exception {

	private static final long serialVersionUID = 475022994858770424L;
	
	
	/*304 Not Modified: 娌℃湁鏁版嵁杩斿洖.
	400 Bad Request: 璇锋眰鏁版嵁涓嶅悎娉曪紝鎴栬€呰秴杩囱姹傞鐜囬檺鍒? 璇︾粏镄勯敊璇唬镰佸涓嬶细
	40028:鍐呴儴鎺ュ彛阌栾(濡傛灉链夎缁嗙殑阌栾淇℃伅锛屼细缁椤嚭镟翠负璇︾粏镄勯敊璇彁绀?
	40033:source_user鎴栬€卼arget_user鐢ㄦ埛涓嶅瓨鍦?
	40031:璋幂敤镄勫井鍗氢笉瀛桦湪
	40036:璋幂敤镄勫井鍗氢笉鏄綋鍓岖敤鎴峰彂甯幂殑寰崥
	40034:涓嶈兘杞彂镊繁镄勫井鍗?
	40038:涓嶅悎娉旷殑寰崥
	40037:涓嶅悎娉旷殑璇勮
	40015:璇ユ浔璇勮涓嶆槸褰揿墠鐧诲綍鐢ㄦ埛鍙戝竷镄勮瘎璁?
	40017:涓嶈兘缁欎笉鏄綘绮変笣镄勪汉鍙戠淇?
	40019:涓嶅悎娉旷殑绉佷俊
	40021:涓嶆槸灞炰簬浣犵殑绉佷俊
	40022:source鍙傛暟(appkey)缂哄け
	40007:镙煎纺涓嶆敮鎸侊紝浠呬粎鏀寔XML鎴朖SON镙煎纺
	40009:锲剧墖阌栾锛岃纭缭浣跨敤multipart涓娄紶浜嗗浘鐗?
	40011:绉佷俊鍙戝竷瓒呰绷涓婇檺
	40012:鍐呭涓虹┖
	40016:寰崥id涓虹┖
	40018:ids鍙傛暟涓虹┖
	40020:璇勮ID涓虹┖
	40023:鐢ㄦ埛涓嶅瓨鍦?
	40024:ids杩囧锛岃鍙傝€倾PI鏂囨。
	40025:涓嶈兘鍙戝竷鐩稿悓镄勫井鍗?
	40026:璇蜂紶阃掓纭殑鐩爣鐢ㄦ埛uid鎴栬€却creen name
	40045:涓嶆敮鎸佺殑锲剧墖绫诲瀷,鏀寔镄勫浘鐗囩被鍨嬫湁JPG,GIF,PNG
	40008:锲剧墖澶у皬阌栾锛屼笂浼犵殑锲剧墖澶у皬涓婇檺涓?M
	40001:鍙傛暟阌栾锛岃鍙傝€倾PI鏂囨。
	40002:涓嶆槸瀵硅薄镓€灞炶€咃紝娌℃湁鎿崭綔鏉冮檺
	40010:绉佷俊涓嶅瓨鍦?
	40013:寰崥澶昵锛岃纭涓嶈秴杩?40涓瓧绗?
	40039:鍦扮悊淇℃伅杈揿叆阌栾
	40040:IP闄愬埗锛屼笉鑳借姹傝璧勬簮
	40041:uid鍙傛暟涓虹┖
	40042:token鍙傛暟涓虹┖
	40043:domain鍙傛暟阌栾
	40044:appkey鍙傛暟缂哄け
	40029:verifier阌栾
	40027:镙囩鍙傛暟涓虹┖
	40032:鍒楄〃鍚嶅お闀匡紝璇风‘淇濊緭鍏ョ殑鏂囨湰涓嶈秴杩?0涓瓧绗?
	40030:鍒楄〃鎻忚堪澶昵锛岃纭缭杈揿叆镄勬枃链笉瓒呰绷70涓瓧绗?
	40035:鍒楄〃涓嶅瓨鍦?
	40053:鏉冮檺涓嶈冻锛屽彧链夊垱寤鸿€呮湁鐩稿叧鏉冮檺
	40054:鍙傛暟阌栾锛岃鍙傝€倾PI鏂囨。
	40059: 鎻掑叆澶辫触锛岃褰曞凡瀛桦湪
	40060锛氭暟鎹簱阌栾锛岃鑱旗郴绯荤粺绠＄悊锻?
	40061锛氩垪琛ㄥ悕鍐茬獊
	40062锛歩d鍒楄〃澶昵浜?
	40063锛歶rls鏄┖镄?
	40064锛歶rls澶浜?
	40065锛歩p鏄┖链?
	40066锛歶rl鏄┖链?
	40067锛歵rend_name鏄┖链?
	40068锛歵rend_id鏄┖链?
	40069锛歶serid鏄┖链?
	40070锛氱涓夋柟搴旗敤璁块棶api鎺ュ彛鏉冮檺鍙楅檺鍒?
	40071锛氩叧绯婚敊璇紝user_id蹇呴』鏄綘鍏虫敞镄勭敤鎴?
	40072锛氭巿鏉冨叧绯诲凡缁忚鍒犻櫎
	40073锛氱洰鍓崭笉鏀寔绉佹湁鍒嗙粍
	40074锛氩垱寤簂ist澶辫触
	40075锛氶渶瑕佺郴缁熺鐞嗗憳镄勬潈闄?
	40076锛氩惈链夐潪娉曡瘝
	40084锛氭彁阅掑け璐ワ紝闇€瑕佹潈闄?
	40082锛氭棤鏁埚垎绫?
	40083锛氭棤鏁堢姸镐佺爜
	40084锛氱洰鍓嶅彧鏀寔绉佹湁鍒嗙粍
	401 Not Authorized: 娌℃湁杩涜韬唤楠岃瘉.
	40101 version_rejected Oauth鐗堟湰鍙烽敊璇?
	40102 parameter_absent Oauth缂哄皯蹇呰镄勫弬鏁?
	40103 parameter_rejected Oauth鍙傛暟琚嫆缁?
	40104 timestamp_refused Oauth镞堕棿鎴充笉姝ｇ‘
	40105 nonce_used Oauth nonce鍙傛暟宸茬粡琚娇鐢?
	40106 signature_method_rejected Oauth绛惧悕绠楁硶涓嶆敮鎸?
	40107 signature_invalid Oauth绛惧悕链间笉鍚堟硶
	40108 consumer_key_unknown! Oauth consumer_key涓嶅瓨鍦?
	40109 consumer_key_refused! Oauth consumer_key涓嶅悎娉?
	40110 token_used! Oauth Token宸茬粡琚娇鐢?
	40111 Oauth Error: token_expired! Oauth Token宸茬粡杩囨湡
	40112 token_revoked! Oauth Token涓嶅悎娉?
	40113 token_rejected! Oauth Token涓嶅悎娉?
	40114 verifier_fail! Oauth Pin镰佽璇佸け璐?
	402 Not Start mblog: 娌℃湁寮€阃氩井鍗?
	403 Forbidden: 娌℃湁鏉冮檺璁块棶瀵瑰簲镄勮祫婧?
	40301 too many lists, see doc for more info 宸叉嫢链夊垪琛ㄤ笂闄?
	40302 auth faild 璁よ瘉澶辫触
	40303 already followed 宸茬粡鍏虫敞姝ょ敤鎴?
	40304 Social graph updates out of rate limit 鍙戝竷寰崥瓒呰绷涓婇檺
	40305 update comment out of rate 鍙戝竷璇勮瓒呰绷涓婇檺
	40306 Username and pwd auth out of rate limit 鐢ㄦ埛鍚嶅瘑镰佽璇佽秴杩囱姹傞檺鍒?
	40307 HTTP METHOD is not suported for this request 璇锋眰镄凥TTP METHOD涓嶆敮鎸?
	40308 Update weibo out of rate limit 鍙戝竷寰崥瓒呰绷涓婇檺
	40309 password error 瀵嗙爜涓嶆纭?
	40314 permission denied! Need a high level appkey 璇ヨ祫婧愰渶瑕乤ppkey鎷ユ湁镟撮佩绾х殑鎺堟潈
	404 Not Found: 璇锋眰镄勮祫婧愪笉瀛桦湪.
	500 Internal Server Error: 链嶅姟鍣ㄥ唴閮ㄩ敊璇?
	502 Bad Gateway: 寰崥鎺ュ彛API鍏抽棴鎴栨鍦ㄥ崌绾?.
	503 Service Unavailable: 链嶅姟绔祫婧愪笉鍙敤.*/
	private int statusCode = -1;
	
	
	
    public WeiboException(String msg) {
        super(msg);
    }

    public WeiboException(Exception cause) {
        super(cause);
    }

    public WeiboException(String msg, int statusCode) {
        super(msg);
        this.statusCode = statusCode;
    }

    public WeiboException(String msg, Exception cause) {
        super(msg, cause);
    }

    public WeiboException(String msg, Exception cause, int statusCode) {
        super(msg, cause);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return this.statusCode;
    }
    
    
	public WeiboException() {
		super(); 
	}

	public WeiboException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public WeiboException(Throwable throwable) {
		super(throwable);
	}

	public WeiboException(int statusCode) {
		super();
		this.statusCode = statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	
}
