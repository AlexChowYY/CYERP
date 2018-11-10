package com.facewarrant.fw.net;


import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Created by maqing on 2017/8/9.
 * Email:2856992713@qq.com
 * 网络请求描述接口
 */
public interface RetrofitRequestInterface {
    /**
     * 注册
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/users/register")
    Call<String> register(@FieldMap Map<String, String> params);

    /**
     * 获取国家及编码
     *
     * @return
     */
    @GET(RequestManager.mInterfacePrefix + "v1/base/getCountries")
    Call<String> getCountries(@QueryMap Map<String, String> params);

    /**
     * 获取验证码
     *
     * @return
     */
    @GET(RequestManager.mInterfacePrefix + "v1/base/smsCode")
    Call<String> getCode(@QueryMap Map<String, String> params);

    /**
     * 获取国外的城市列表
     *
     * @return
     */
    @GET(RequestManager.mInterfacePrefix + "v1/base/getCountriesCities")
    Call<String> getCountriesCities(@QueryMap Map<String, String> params);

    /**
     * 获取省列表
     *
     * @return
     */
    @GET(RequestManager.mInterfacePrefix + "v1/base/provinces")
    Call<String> getProvince(@QueryMap Map<String, String> params);

    /**
     * 获取城市列表
     *
     * @param id
     * @return
     */

    @GET(RequestManager.mInterfacePrefix + "v1/base/cities")
    Call<String> getCities(@QueryMap Map<String, String> params);

    /**
     * 忘记密码
     *
     * @return
     */
    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/users/forgetPwd")
    Call<String> forgetPwd(@FieldMap Map<String, String> params);

    /**
     * 登录
     */
    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/users/login")
    Call<String> login(@FieldMap Map<String, String> params);

    /**
     *
     */
    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/base/smsCode")
    Call<String> checkCode(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/base/getOssProperties")
    Call<String> getOSSData(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/users/changePwd")
    Call<String> changePwd(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/users/changeHeadUrl")
    Call<String> changeHeadUrl(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/users/loginOut")
    Call<String> loginOut(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/base/aboutUs")
    Call<String> getAboutUs(@FieldMap Map<String, String> params);

    /**
     * 语音识别/录音/短视频 时长限制
     */
    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/base/limitTime")
    Call<String> getLimitTime(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/users/feedBack")
    Call<String> feedback(@FieldMap Map<String, String> params);

    /**
     * 修改个人中心背景图片
     */

    @POST(RequestManager.mInterfacePrefix + "v1/users/changeBackgroundPicture")
    Call<String> changePersonalBG(@FieldMap Map<String, String> params);

    /**
     * 获取个人主页信息
     */

    @GET(RequestManager.mInterfacePrefix + "v1/faceLibrary/faceInfo")
    Call<String> getFaceInfo(@QueryMap Map<String, String> params);

    @GET(RequestManager.mInterfacePrefix + "v1/faceLibrary/memberClass")
    Call<String> getHomeType(@QueryMap Map<String, String> params);


    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/users/changePhone")
    Call<String> changePhone(@FieldMap Map<String, String> params);


    @GET(RequestManager.mInterfacePrefix + "v1/faceLibrary")
    Call<String> getHomePagerData(@QueryMap Map<String, String> params);


    @GET(RequestManager.mInterfacePrefix + "v1/faceLibrary/faceReleaseGoods")
    Call<String> getFaceGoods(@QueryMap Map<String, String> params);


    @GET(RequestManager.mInterfacePrefix + "v1/faceLibrary/faceBrand")
    Call<String> getFaceBrand(@QueryMap Map<String, String> params);


    @GET(RequestManager.mInterfacePrefix + "v1/groups/myGroups")
    Call<String> getMyGroups(@QueryMap Map<String, String> params);


    @GET(RequestManager.mInterfacePrefix + "v1/faceLibrary/myFaceLibrary")
    Call<String> getMyFaceLibrary(@QueryMap Map<String, String> params);


    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/faceLibrary/attention")
    Call<String> attention(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/faceLibrary/changeGroupsIndex")
    Call<String> changePosition(@FieldMap Map<String, String> params);


    @GET(RequestManager.mInterfacePrefix + "v1/base/getCountryByRegion")
    Call<String> getCountryByRegion(@QueryMap Map<String, String> params);


    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/groups/facesNotInGroup")
    Call<String> getFaceRecommendList(@FieldMap Map<String, String> params);

    @GET(RequestManager.mInterfacePrefix + "v1/goods/myReleaseGoods")
    Call<String> getMyWarrantItList(@QueryMap Map<String, String> params);


    @GET(RequestManager.mInterfacePrefix + "v1/QA/questionFromMe")
    Call<String> getMyQuestionList(@QueryMap Map<String, String> params);


    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/users/userInfo")
    Call<String> getMyUserInfo(@FieldMap Map<String, String> params);


    @GET(RequestManager.mInterfacePrefix + "v1/messages/msgRecord")
    Call<String> getInfoList(@QueryMap Map<String, String> params);

    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/groups/joinFace")
    Call<String> joinFace(@FieldMap Map<String, String> params);


    @GET(RequestManager.mInterfacePrefix + "v1/faceLibrary/myAttention")
    Call<String> getMyFollowList(@QueryMap Map<String, String> params);


    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/goods/cancelRelease")
    Call<String> cancelRelease(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/goods/collection")
    Call<String> collection(@FieldMap Map<String, String> params);


    @GET(RequestManager.mInterfacePrefix + "v1/goods/collection")
    Call<String> getCollectionList(@QueryMap Map<String, String> params);

    @GET(RequestManager.mInterfacePrefix + "v1/goods")
    Call<String> getWarrantDetail(@QueryMap Map<String, String> params);


    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/goods/favorite")
    Call<String> favorite(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/order/createOrder")
    Call<String> goBuy(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/goods/commentReply")
    Call<String> zanComment(@FieldMap Map<String, String> params);


    @GET(RequestManager.mInterfacePrefix + "v1/goods/comment")
    Call<String> getComments(@QueryMap Map<String, String> params);


    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/goods/comment")
    Call<String> comment(@FieldMap Map<String, String> params);


    @GET(RequestManager.mInterfacePrefix + "v1/goods/comment/reply")
    Call<String> getReplyList(@QueryMap Map<String, String> params);

    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/goods/comment/reply")
    Call<String> reply(@FieldMap Map<String, String> params);


    @GET(RequestManager.mInterfacePrefix + "v1/QA/answerFromMe")
    Call<String> getMyAnswerList(@QueryMap Map<String, String> params);

    @GET(RequestManager.mInterfacePrefix + "v1/find")
    Call<String> getFindList(@QueryMap Map<String, String> params);

    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/groups/create")
    Call<String> createGroup(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/groups/delete")
    Call<String> deleteGroup(@FieldMap Map<String, String> params);

    @GET(RequestManager.mInterfacePrefix + "v1/groups/groupFace")
    Call<String> getFaceMembery(@QueryMap Map<String, String> params);


    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/groups/update")
    Call<String> reviseGroupName(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/groups/deleteGroupFace")
    Call<String> deleteGroupMember(@FieldMap Map<String, String> params);


    @GET(RequestManager.mInterfacePrefix + "v1/faceValue/searchUserBalance")
    Call<String> getLeaveMoney(@QueryMap Map<String, String> params);


    @GET(RequestManager.mInterfacePrefix + "v1/faceValue/userExpendRecord")
    Call<String> getWithdrawRecord(@QueryMap Map<String, String> params);

    @GET(RequestManager.mInterfacePrefix + "v1/users/points")
    Call<String> getPointDetail(@QueryMap Map<String, String> params);


    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/users/exchangeFaceValue")
    Call<String> exchangeFaceValue(@FieldMap Map<String, String> params);


    @GET(RequestManager.mInterfacePrefix + "v1/faceValue/userIncomeRecord")
    Call<String> getIncome(@QueryMap Map<String, String> params);

    /**
     * 查询银行卡账号/查询支付宝账号
     *
     * @param params
     * @return
     */
    @GET(RequestManager.mInterfacePrefix + "v1/faceValue/searchCashAccount")
    Call<String> getCashAccount(@QueryMap Map<String, String> params);

    /**
     * 提现
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/faceValue/userWithdraw")
    Call<String> withdraw(@FieldMap Map<String, String> params);

    /**
     * 提现详情
     *
     * @param params
     * @return
     */
    @GET(RequestManager.mInterfacePrefix + "v1/faceValue/userExpendRecordDetail")
    Call<String> getWithdrawDetail(@QueryMap Map<String, String> params);


    /**
     * 消息列表页提醒
     *
     * @param params
     * @return
     */
    @GET(RequestManager.mInterfacePrefix + "v1/messages")
    Call<String> getMessageTips(@QueryMap Map<String, String> params);


    /**
     * 回答详情
     *
     * @param params
     * @return
     */
    @GET(RequestManager.mInterfacePrefix + "v1/QA/answer")
    Call<String> getAnswerDeatil(@QueryMap Map<String, String> params);


    /**
     * 回答
     */
    @POST(RequestManager.mInterfacePrefix + "v1/QA/answer")
    Call<String> answer(@FieldMap Map<String, String> params);


    /**
     * 删除回答
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/QA/answerFromMe")
    Call<String> deleteMyAnswer(@FieldMap Map<String, String> params);


    /**
     * 删除问题
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/QA/questionFromMe")
    Call<String> deleteMyQuestion(@FieldMap Map<String, String> params);

    /**
     * 删除问题
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/messages/delReadAllMsg")
    Call<String> deleteAllMessage(@FieldMap Map<String, String> params);


    /**
     * 关注加群&&重新分组
     *
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/faceLibrary/attentionGroups")
    Call<String> followJoinGroup(@FieldMap Map<String, String> params);

    /**
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/QA/question")
    Call<String> question(@FieldMap Map<String, String> params);

    /**
     * v1/QA/recommendTags
     */
    @GET(RequestManager.mInterfacePrefix + "v1/QA/recommendTags")
    Call<String> getRecommendTags(@QueryMap Map<String, String> params);


    /**
     * v1/QA/recommendTags
     */
    @GET(RequestManager.mInterfacePrefix + "v1/QA/showFacesAndGroups")
    Call<String> getFacesAndGroups(@QueryMap Map<String, String> params);


    /**
     * face库搜索(满足条件的face)
     */
    @GET(RequestManager.mInterfacePrefix + "v1/faceLibrary/searchMyFaceLibrary")
    Call<String> getSearchMyFaceLibraryList(@QueryMap Map<String, String> params);


    @GET(RequestManager.mInterfacePrefix + "v1/faceLibrary/searchMyFaceLibraryInfo")
    Call<String> getSearchInfo(@QueryMap Map<String, String> params);


    @GET(RequestManager.mInterfacePrefix + "v1/search")
    Call<String> getSearchHistory(@QueryMap Map<String, String> params);


    @GET(RequestManager.mInterfacePrefix + "v1/goods/typeAndGoodsName")
    Call<String> getTypeAndGoodName(@QueryMap Map<String, String> params);


    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/search/clear")
    Call<String> clearSearchHistory(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/base/getVideoSTSToken")
    Call<String> getSTSToken(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/users/signOn")
    Call<String> sign(@FieldMap Map<String, String> params);


    @GET(RequestManager.mInterfacePrefix + "v1/goods/getInfoBeforeRelease")
    Call<String> getUserStanding(@QueryMap Map<String, String> params);


    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/faceLibrary/searchFaces")
    Call<String> searchFaces(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/users/sendInviteMsg")
    Call<String> inviteContact(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/users/getWXUserInfo")
    Call<String> getWXUserInfo(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/users/getAlipayUserInfo")
    Call<String> getAlipayUserInfo(@FieldMap Map<String, String> params);


    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/users/getAlipayAuthInfo")
    Call<String> getAlipayAuthInfo(@FieldMap Map<String, String> params);

    @GET(RequestManager.mInterfacePrefix + "v1/store")
    Call<String> getNearbyStore(@QueryMap Map<String, String> params);


    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/store/navigation")
    Call<String> getNavgation(@FieldMap Map<String, String> params);


    @GET(RequestManager.mInterfacePrefix + "v1/goods/getInfoBeforeRelease")
    Call<String> getInfoBeforeRelease(@QueryMap Map<String, String> params);

    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/goods/release")
    Call<String> release(@FieldMap Map<String, String> params);

    @GET(RequestManager.mInterfacePrefix + "v1/goods/brands")
    Call<String> getAllBrands(@QueryMap Map<String, String> params);


    @GET(RequestManager.mInterfacePrefix + "v1/goods/typeAndGoodsName")
    Call<String> getAllType(@QueryMap Map<String, String> params);


    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/base/getSTSToken")
    Call<String> getPicStsToken(@FieldMap Map<String, String> params);

    /**
     * @param params
     * @return
     */
    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/base/getVideoPlayAuth")
    Call<String> getVideoPlayAuth(@FieldMap Map<String, String> params);


    /**
     * 已读单条信息
     */
    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + "v1/messages/delReadMsg")
    Call<String> readOneMessage(@FieldMap Map<String, String> params);



    /**
     * 检测更新
     */
    @FormUrlEncoded
    @POST(RequestManager.mInterfacePrefix + " v1/base/getAppVersion")
    Call<String>versionUpdate(@FieldMap Map<String, String> params);

}

