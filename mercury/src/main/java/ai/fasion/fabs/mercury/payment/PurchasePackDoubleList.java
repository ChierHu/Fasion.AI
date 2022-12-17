package ai.fasion.fabs.mercury.payment;

import ai.fasion.fabs.mercury.payment.po.PurchasePackInfoVO;
import ai.fasion.fabs.vesta.expansion.NotFoundException;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Function:
 *
 * @author miluo
 * Date: 2021/9/18 14:56
 * @since JDK 1.8
 */
public class PurchasePackDoubleList extends DoubleList<PurchasePackInfoVO> {
    private final Calendar calendar = new GregorianCalendar();


    public void dynamicAdd(PurchasePackInfoVO o) {
        if (this.getLength() == 0) {
            super.addLast(o);
        } else {
            //得到当前最后节点过期日期
            Node<PurchasePackInfoVO> last = this.last;
            Date lastDate = last.getE().getCreatedAt();
            calendar.setTime(lastDate);
            calendar.add(Calendar.DATE, last.getE().getExpirationPeriod());
            lastDate = calendar.getTime();

            //得到即将要插入的信息的最后日期
            PurchasePackInfoVO current = o;
            Date currentDate = current.getCreatedAt();
            calendar.setTime(currentDate);
            calendar.add(Calendar.DATE, current.getExpirationPeriod());
            currentDate = calendar.getTime();

            //大于0证明，lastDate在currentDate日期的后面，
            if (lastDate.compareTo(currentDate) < 0) {
                super.addLast(o);
            } else {
                Node<PurchasePackInfoVO> tmpNode = this.last;
                do {
                    tmpNode = tmpNode.getPrevious();
                    if (tmpNode == null) {
                        break;
                    }
                    lastDate = tmpNode.getE().getCreatedAt();
                    calendar.setTime(lastDate);
                    calendar.add(Calendar.DATE, tmpNode.getE().getExpirationPeriod());
                    lastDate = calendar.getTime();
                    //小于0，证明lastDate在currentDate日期的前面，需要继续判断
                } while (lastDate.compareTo(currentDate) > 0);
                this.insertNext(tmpNode.getE(), o);


            }

        }
    }

    /**
     * 得到第一个值
     *
     * @return
     */
    public PurchasePackInfoVO getFirst() {
        return (PurchasePackInfoVO) this.first.getE();
    }

    /**
     * 得到最后一个值
     *
     * @return
     */
    public PurchasePackInfoVO getLast() {
        return (PurchasePackInfoVO) this.last.getE();
    }


    /**
     * 得到下一个值
     *
     * @param purchasePackInfoVO
     * @return
     */
    public PurchasePackInfoVO getNext(PurchasePackInfoVO purchasePackInfoVO) {
        Node tmp = this.first;
        while (null != tmp) {
            if (tmp.getE().equals(purchasePackInfoVO)) {
                if (null != tmp.getNext()) {
                    return (PurchasePackInfoVO) tmp.getNext().getE();
                }
            }
            tmp = tmp.getNext();
        }
        return null;
    }

    /**
     * 得到上一个值
     *
     * @param purchasePackInfoVO
     * @return
     */
    public PurchasePackInfoVO getPrevious(PurchasePackInfoVO purchasePackInfoVO) {
        Node tmp = this.last;
        while (null != tmp) {
            if (tmp.getE().equals(purchasePackInfoVO)) {
                if (null != tmp.getPrevious()) {
                    return (PurchasePackInfoVO) tmp.getPrevious().getE();
                }
            }
            tmp = tmp.getPrevious();
        }
        return null;
    }


}
