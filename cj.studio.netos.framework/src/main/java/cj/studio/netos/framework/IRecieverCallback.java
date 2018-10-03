package cj.studio.netos.framework;

public interface IRecieverCallback {
    /**
     * 如果处理该消息涉及到更新界面元素，则需判断Activity是否是运行状态。
     * @param frame
     */
    void onMessage(Frame frame);
}
