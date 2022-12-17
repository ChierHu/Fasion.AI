package ai.fasion.fabs.mercury.payment;

/**
 * Function:双向链表
 *
 * @author miluo
 * Date: 2021/9/18 14:44
 * @since JDK 1.8
 */
public class DoubleList<E> {
    /**
     * 指向第一个元素
     */
    protected Node first;
    /**
     * 指向最后一个元素
     */
    protected Node last;
    /**
     * 链表长度
     */
    protected int length = 0;

    class Node<E> {
        private Node previous;
        private Node next;
        private E e;


        public Node(Node previous, Node next, E e) {
            this.previous = previous;
            this.next = next;
            this.e = e;
        }

        public Node getPrevious() {
            return previous;
        }

        public void setPrevious(Node previous) {
            this.previous = previous;
        }

        public Node getNext() {
            return next;
        }

        public void setNext(Node next) {
            this.next = next;
        }

        public E getE() {
            return e;
        }

        public void setE(E e) {
            this.e = e;
        }
    }

    /***
     * 向头节点添加元素，节点结构对外应该是不可见的，所以这里只传递一个泛型的值e
     */
    public void addFirst(E e) {
        //链表为空判断
        if (first == null) {
            //创建一个新的节点，前驱和后继都为空
            Node node = new Node(null, null, e);
            //将first和last指针指向链表的第一个元素
            this.first = node;
            this.last = node;
            //链表长度自增一，下同
            length++;
        } else {
            //链表不为空创建一个前驱为空，后继为当前first节点的节点，值为传入的参数e
            Node node = new Node(null, first, e);
            //当前first的前驱设置为node
            this.first.previous = node;
            //将first指针指向新节点
            this.first = node;
            length++;
        }
    }

    /***
     *addLast同addFirst
     */
    public void addLast(E e) {
        if (last == null) {
            Node node = new Node(null, null, e);
            this.first = node;
            this.last = node;
            length++;
        } else {
            Node node = new Node(last, null, e);
            this.last.next = node;
            this.last = node;
            length++;
        }
    }

    public void insertPrevious(E baseElement, E value) {
        Node index = this.first;
        while (index != null) {
            if (index.e == baseElement) {
                break;
            }
            index = index.next;
        }
        Node insertValue = new Node(index.previous, index, value);
        index.previous.next = insertValue;
        index.previous = insertValue;
        length++;
    }

    public void insertNext(E baseElement, E value) {
        Node index = this.first;
        while (index != null) {
            if (index.e == baseElement) {
                break;
            }
            index = index.next;
        }
        Node insertValue = new Node(index, index.next, value);
        index.next.previous = insertValue;
        index.next = insertValue;
        length++;
    }

    public void removeElement(E value) {
        Node index = this.first;
        while (index != null) {
            if (index.e == value) {
                break;
            }
            index = index.next;
        }
        assert index != null;
        index.previous.next = index.next;
        index.next.previous = index.previous;
        length--;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Node current = this.first;
        while (current != null) {
            sb.append(current.e).append("->");
            current = current.next;
        }
        return sb.toString();
    }


    public static void main(String[] args) {
        DoubleList<String> list=new DoubleList<>();
        list.addLast("value1");
        list.addLast("value2");
        list.addLast("value3");
        list.addLast("value4");
        list.addFirst("value0");
        list.insertPrevious("value3","insertValue");
        list.insertNext("value3","insertValue2");
        System.out.println(list.toString());
        System.out.println("链表的长度是"+list.getLength());
        list.removeElement("value3");
        System.out.println(list.toString());
        System.out.println("链表的长度是"+list.getLength());
    }

}
