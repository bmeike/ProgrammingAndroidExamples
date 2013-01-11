
package com.oreilly.demo.android.pa.aidl.service;

interface PathService {
    void setPoint(in String name, in String path);
    String getPoint(in String name);
    void deletePoint(in String name);
}
