package ai.fasion.fabs.vesta.domain.pojo;

/**
 * Function:设备信息
 *
 * @author miluo
 * Date: 2021/2/23 14:51
 * @since JDK 1.8
 */
public class EquipmentInfo {
    /**
     * 磁盘剩余空间
     */
    private String diskFreeSize;

    /**
     * 国家代码
     */
    private String countryCode;

    /**
     * 设备型号
     */
    private String deviceModel;

    /**
     * 运营商可用
     */
    private String useAllowsVOIP;

    /**
     * 运营商
     */
    private String carrier;

    /**
     * 用户起的设备名称
     */
    private String deviceName;

    /**
     * 运行内存
     */
    private String runMemory;

    /**
     * 磁盘已使用
     */
    private String talDiskSpace;

    /**
     * ip
     */
    private String deviceIP;

    /**
     * 移动国家代码
     */
    private String mobileCountryCode;

    /**
     * 可用内存
     */
    private String availableMemory;

    /**
     * 手机容量
     */
    private String capacity;

    /**
     * 厂家设备名称
     */
    private String mobileName;

    /**
     * 系统
     */
    private String deviceOS;

    /**
     * 版本
     */
    private String osVersion;

    /**
     * 移动网络代码
     */
    private String mobileNetworkCode;

    /**
     * app版本
     */
    private String appVersion;

    /**
     * 设备id
     */
    private String deviceID;

    /**
     * 网络(4G、WIFI)
     */
    private String network;

    public String getDiskFreeSize() {
        return diskFreeSize;
    }

    public void setDiskFreeSize(String diskFreeSize) {
        this.diskFreeSize = diskFreeSize;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }


    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getRunMemory() {
        return runMemory;
    }

    public void setRunMemory(String runMemory) {
        this.runMemory = runMemory;
    }

    public String getTalDiskSpace() {
        return talDiskSpace;
    }

    public void setTalDiskSpace(String talDiskSpace) {
        this.talDiskSpace = talDiskSpace;
    }

    public String getMobileCountryCode() {
        return mobileCountryCode;
    }

    public void setMobileCountryCode(String mobileCountryCode) {
        this.mobileCountryCode = mobileCountryCode;
    }

    public String getAvailableMemory() {
        return availableMemory;
    }

    public void setAvailableMemory(String availableMemory) {
        this.availableMemory = availableMemory;
    }

    public String getCapacity() {
        return capacity;
    }

    public void setCapacity(String capacity) {
        this.capacity = capacity;
    }

    public String getMobileName() {
        return mobileName;
    }

    public void setMobileName(String mobileName) {
        this.mobileName = mobileName;
    }

    public String getDeviceOS() {
        return deviceOS;
    }

    public void setDeviceOS(String deviceOS) {
        this.deviceOS = deviceOS;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getMobileNetworkCode() {
        return mobileNetworkCode;
    }

    public void setMobileNetworkCode(String mobileNetworkCode) {
        this.mobileNetworkCode = mobileNetworkCode;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getUseAllowsVOIP() {
        return useAllowsVOIP;
    }

    public void setUseAllowsVOIP(String useAllowsVOIP) {
        this.useAllowsVOIP = useAllowsVOIP;
    }

    public String getDeviceIP() {
        return deviceIP;
    }

    public void setDeviceIP(String deviceIP) {
        this.deviceIP = deviceIP;
    }

    @Override
    public String toString() {
        return "EquipmentInfo{" +
                "diskFreeSize='" + diskFreeSize + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", deviceModel='" + deviceModel + '\'' +
                ", useAllowsVOIP='" + useAllowsVOIP + '\'' +
                ", carrier='" + carrier + '\'' +
                ", deviceName='" + deviceName + '\'' +
                ", runMemory='" + runMemory + '\'' +
                ", talDiskSpace='" + talDiskSpace + '\'' +
                ", deviceIP='" + deviceIP + '\'' +
                ", mobileCountryCode='" + mobileCountryCode + '\'' +
                ", availableMemory='" + availableMemory + '\'' +
                ", capacity='" + capacity + '\'' +
                ", mobileName='" + mobileName + '\'' +
                ", deviceOS='" + deviceOS + '\'' +
                ", osVersion='" + osVersion + '\'' +
                ", mobileNetworkCode='" + mobileNetworkCode + '\'' +
                ", appVersion='" + appVersion + '\'' +
                ", deviceID='" + deviceID + '\'' +
                ", network='" + network + '\'' +
                '}';
    }
}
