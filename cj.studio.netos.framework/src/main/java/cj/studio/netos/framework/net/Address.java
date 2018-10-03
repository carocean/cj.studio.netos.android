package cj.studio.netos.framework.net;

import cj.studio.netos.framework.NetException;

/**
 * Created by caroceanjofers on 2018/1/20.
 */

public class Address {
    String protocol;
    String host;
    int port;
    public Address(){
    }
    public Address(String address)throws NetException {
        int pos=address.indexOf("://");
        if(pos<0){
            throw new NetException("地址协议格式不正确");
        }
        protocol=address.substring(0,pos);
        String remaining=address.substring(pos+3,address.length());
        pos=remaining.lastIndexOf(":");
        if(pos<0){
            port=80;
        }else{
            host=remaining.substring(0,pos);
            remaining=remaining.substring(pos+1,remaining.length());
            port= Integer.parseInt(remaining);
        }

    }



    @Override
    public String toString() {
        return String.format("%s://%s:%s",protocol,host,port);
    }
}
