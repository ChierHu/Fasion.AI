package ai.fasion.fabs.vesta.enums

class SkuEnum {

    enum class Type(val label: String) {
        PointPack("point-pack"), Product("product"), PointGift("point-gift")
    }

    enum class Status(val label: String) {
        Enable("enable"), Disable("disable")
    }
}