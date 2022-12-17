package ai.fasion.fabs.vesta.enums


class PurchaseEnum {

    enum class Status(val label: String) {
        Pending("pending"), Finished("finished"), Closed("closed"), Canceled("canceled"), Refunded("refunded"), Refunding(
            "refunding")
    }

    enum class Type(val label: String, val code: String){
        /*FaceSwap和MattingImage是sku表中sku的值*/
        PointPack("point-pack","point-pack"), FaceSwap("face-swap","XvZpMj02P6"), MattingImage("matting-image" ,"25Ym78Lz1Y") ,PointGift("point-gift","x5q0GLdd55")
    }
}