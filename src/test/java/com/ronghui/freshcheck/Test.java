package com.ronghui.freshcheck;

import sun.dc.pr.PRError;

import java.math.BigInteger;
import java.util.*;

/**
 * @author Ansel Zhong
 * @description:
 * @date 2024/5/26 18:13
 * @ProjectName freshcheck
 **/
public class Test {
    public static void main(String[] args) throws CloneNotSupportedException {
//        A a = new A();
//        A a1 = a;
//        a1.x = 200;
//        System.out.println(a.x);
//        A a2 = (A) a.clone();
//        a2.x = 300;
//        System.out.println(a1.x);
//        System.out.println(a2.b.a);


        D d = new D();
    }
}

class A implements Cloneable{
    int x = 100;

    B b = new B();

    public Object clone() throws CloneNotSupportedException{
            return super.clone();
    }

}

class B {
     int a = 10;
}

class C {
    C(){
        System.out.println("hello");
    }
}

class D extends C {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(200);
        list.add(12);
        Collections.sort(list);
        List<Person> list2 = new ArrayList<>();
        // using Comparator
        Comparator<Person> comparator = new Comparator<Person>() {
            @Override
            public int compare(Person o1, Person o2) {
                return o1.getAge() - o2.getAge();
            }
        };

        list2.add(new Person(100));
        list2.add(new Person(1));
        list2.add(new Person(3));
        Collections.sort(list2, comparator); // comparator
        Collections.sort(list2); // comparable
    }
}

class Person implements Comparable<Person>{
    private Integer age;

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getAge() {
        return age;
    }
    Person(int age){
        this.age = age;
    }
    @Override
    public int compareTo(Person o) {
        return this.age - o.age;
    }

    @Override
    public String toString() {
        return "Person{" +
                "age=" + age +
                '}';
    }
}

class Main{
    public static void main(String[] args) {
        ListNode a1 = new ListNode(2);
        ListNode a2 = new ListNode(4);
        ListNode a3 = new ListNode(3);
        ListNode a4 = new ListNode(5);
        ListNode a5 = new ListNode(6);
        ListNode a6 = new ListNode(4);
        a1.next = a2;
        a2.next = a3;

        a4.next = a5;
        a5.next = a6;
        ListNode listNode = Solution.addTwoNumbers(a1, a4);
        while (true) {
            if (listNode == null) {
                break;
            }
            System.out.println(listNode.val);
            listNode = listNode.next;
        }
    }
}
class Solution {
    public static ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode tmp = l1;
        String s1 = "";
        while(true) {
            if(tmp == null) {
                break;
            }
            s1 += tmp.val;
            tmp = tmp.next;
        }
        StringBuilder reversed = new StringBuilder(s1).reverse();
        String newStr1 = reversed.toString();
        BigInteger i1 = new BigInteger(newStr1);

        ListNode tmp2 = l2;
        String s2 = "";
        while(true) {
            if(tmp2 == null) {
                break;
            }
            s2 += tmp2.val;
            tmp2 = tmp2.next;
        }
        StringBuilder reversed2 = new StringBuilder(s2).reverse();
        String newStr2 = reversed2.toString();
        Integer i2 = new Integer(newStr2);

        Integer sum1 = i1 + i2;
        String s3 = sum1 + "";
        StringBuilder reversed3 = new StringBuilder(s3).reverse();
        String newStr3 = reversed3.toString();
        char[] arr = newStr3.toCharArray();
        ListNode head = new ListNode();
        ListNode newNode = head;
        for(int i = 0; i < arr.length; i++) {
            ListNode nextNode = new ListNode();
            newNode.val = Character.getNumericValue(arr[i]);
            if (! (i + 1 == arr.length)) {
                newNode.next = nextNode;
                newNode = nextNode;
            }
        }
        return head;
    }
}



 class ListNode {
   int val;
     ListNode next;
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
 }


