@isTest
public with sharing class testingMyTestClass {
    @isTest
    public static void testcase1() {
        MyTestClass my = new MyTestClass();
        System.assert(my.thisIsATest());
    }
}
