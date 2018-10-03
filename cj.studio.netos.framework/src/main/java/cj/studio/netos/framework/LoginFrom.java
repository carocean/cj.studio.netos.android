package cj.studio.netos.framework;

public class LoginFrom {
    String principal;
    String token;
    String[] addressList;
    public LoginFrom(String principal, String token, String[] addressList){
        this.principal = principal;
        this.token=token;
        this.addressList=addressList;
    }


    public String getPrincipal() {
        return principal;
    }

    public String getToken() {
        return token;
    }

    public String[] getAddressList() {
        return addressList;
    }
}
