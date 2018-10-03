package cj.studio.netos.framework;

public interface IReciever {
    void accept(IRecieverCallback callback);

    void fire(Frame frame);
}
