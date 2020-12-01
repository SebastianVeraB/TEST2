@isTest
public with sharing class testingMyTestClass {
    
    public static void testcase1() {
        MyTestClass my = new MyTestClass();
        System.assert(my.thisIsATest());
    }
}
