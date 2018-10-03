package cj.studio.netos.framework;

/**
 * Created by caroceanjofers on 2018/1/24.
 */
//细胞液，为胞体提供养分
public interface ICellsap {
    String[] remoteAddressList();
    String principal();
    String token();

    void token(String token);

    void principal(String token);

    void remoteAddressList(String[] remoteAddressList);

    void flush();

    void empty();

    boolean checkIdentityIsEmpty();
}
