package ai.fasion.fabs.vesta.service.context;

import ai.fasion.fabs.vesta.domain.pojo.EquipmentInfo;

/**
 * Function: 获取设备信息
 *
 * @author miluo
 * Date: 2021/4/22 13:54
 * @since JDK 1.8
 */
public class EquipmentThreadLocalHolder {
    private static final ThreadLocal<EquipmentInfo> equipmentInfo;

    static {
        equipmentInfo = new ThreadLocal<>();
    }

    public static EquipmentInfo getEquipmentInfo() {
        return EquipmentThreadLocalHolder.equipmentInfo.get();
    }

    public static void setEquipmentInfo(EquipmentInfo equipmentInfo) {
        EquipmentThreadLocalHolder.equipmentInfo.set(equipmentInfo);
    }

    public static void clean() {
        equipmentInfo.remove();
    }

}
