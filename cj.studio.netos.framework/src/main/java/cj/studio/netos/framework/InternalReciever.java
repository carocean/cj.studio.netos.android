package cj.studio.netos.framework;

 class InternalReciever implements IReciever {
    IRecieverCallback callback;
    @Override
    public void accept(IRecieverCallback callback) {
        this.callback=callback;
    }

     @Override
     public void fire(Frame frame) {
         if(callback==null){
             return;
         }
         callback.onMessage(frame);
     }
 }
