package ai.fasion.fabs.mercury.payment

import java.util.*

/**
 * Function: 用来记录用户下单时的状态
 * eg: platform、ip、os、user_snapshot、point_expired_at
 *
 * @author miluo
 * Date: 2021/8/26 10:21
 * @since JDK 1.8
 */
class MetaInfo {

    data class Meta(
        /**
         * UA
         */
        val userAgent: String?,
        /**
         * 用户快照信息
         */
        val userSnapshot: UserSnapshot,
        /**
         * 点数过期时间
         */
        val pointExpiredAt: Date?
    );

    data class UserSnapshot(
        /**
         * 用户手机号
         */
        val phone: String?,
        /**
         * 用户邮箱号
         */
        val email: String?,
        /**
         * 用户别名
         */
        val nickname: String?,
        /**
         * 状态信息
         */
        val status: String?,
        /**
         * 头像图片
         */
        val avatar: String?
    );
}