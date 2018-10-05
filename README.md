cj.studio.netos.android

安卓架构，支持敏捷开发和持续集成，最好的保持并利用了android的自身特性，避免复杂、侵入式改变，支持：服务容器、IOC、沉浸式状态栏、场景切换、模拟http请求式导航等

1.如图：

![Image text](https://github.com/carocean/cj.studio.android/blob/master/document/img/home.jpeg)

![Image text](https://github.com/carocean/cj.studio.android/blob/master/document/img/popup.jpeg)

![Image text](https://github.com/carocean/cj.studio.android/blob/master/document/img/geoblog.jpeg)

2.支持面向模块开发。
  - Framework项目是核心框架，common项目为其它项目提供全局资源
  - 其它功能各独立项目
  
3.SplashScreen 中检查本地是否有令牌，如果有则直接跳转首页，否则跳到登录
    ```
    @Viewport(name = "/splash")
    public class SplashScreen extends AppCompatActivity {
        @ServiceSite
        IServiceProvider site;
    
    
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.splash_screen);
            INeuron neuron=site.getService("$");
            final IRequester requester=site.getService("$.requester");
    
    //        neuron.cellsap().empty();
            if(neuron.cellsap().checkIdentityIsEmpty()){//到登录界面
                Frame frame=new Frame("navigate /login netos/1.0");
                requester.request(frame);
                finish();//结束之后则可使回退键不到splash界面
            }else{//进入系统到桌面
                ICellsap cellsap=neuron.cellsap();
                neuron.startNetosServiceUseLocal();
                Timer timer=new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
    
                        Frame frame=new Frame("navigate / netos/1.0");
                        frame.head("test","232323");
                        frame.parameter("zz","....");
                        requester.request(frame);
                        finish();//结束之后则可使回退键不到splash界面
                    }
                },1000);
            }
            neuron.inputAxon().add(new ISynapsis() {
                @Override
                public void flow(Frame frame, IAxon axon) {
                    if("/handshake".equals(frame.path())){
                        Log.i("HandshakeViewport",frame.toString());
                        return;
                    }
                    if("/disconnect".equals(frame.path())){
                        Log.i("HandshakeViewport",frame.toString());
                        return;
                    }
                    axon.nextFlow(frame,this);
                }
    
                @Override
                public void dispose() {
    
                }
            });
        }
    }
    ```
    
    - @Viewport 视口，声明Acivity
    @ServiceSite 注入服务站点，能获取到框架所提供的服务，如：
        - 获取神经元
        INeuron neuron=site.getService("$");
        - 获取请求器
        IRequester requester=site.getService("$.requester");
    - 使用请求器跳转到登录视口 
        ``
        Frame frame=new Frame("navigate /login netos/1.0");
        requester.request(frame);
        ``
    - 在输出轴突中添加突触以拦截消息侦，请求包括网络推送而来的消息侦和请求器发送的本地消息侦
        ``neuron.inputAxon().add(new ISynapsis()``
    
4.LoginViewport 登录
                   ```
                   LoginFrom loginFrom=new LoginFrom(mEmail,mPassword,addresslist);
                   neuron.startNetosService(loginFrom);
                   Frame frame=new Frame("navigate / netos/1.0");
                   frame.head("test","232323");
                   frame.parameter("zz","....");
                   IRequester requester=site.getService("$.requester");
                   requester.request(frame);
                   ```
  登录成功后启动netos服务：neuron.startNetosService(loginFrom);
  然后跳转到首页：navigate / netos/1.0
  
5.DesktopViewport 桌面视口。在desktop项目中
  - @Viewport(name = "/",isFullWindow = true) 设置为全屏窗口，
      在设为全屏窗口后，需要在Layout中指定android:fitsSystemWindows="true"，并注意在View的层级上使用
  - @Reciever 消息接收者，并为之指定用于消息接收的回调对象。消息接收者能收取到远程和本地发往本视口的消息
    IReciever reciever;
  - 视口编程规范：
    cj见解：netos可以集成使用ButterKnife等第三方的用于将控件注解为字段，但cj不推荐使用这些第三方库，因为这些库均侵入了android的原生api设计，会造成不同sdk下的不稳定因素，而android提供的findViewId已足够好用，不必重复造些轮子，cj建议通过编程规范从而达到可读性要求和快速开发要求。
    规范：在viewport的onCreate中分为三部分：
    - 第一部分是赋值字段，如DesktopViewport类
    - 第二部分是View的初始化方法声明
    - 第三部分是做其它事件的代码
  - IViewportRegionManager viewportRegionManager = workbench.createRegionManager(this); 任何视口均可包含区域（ViewportRegion)，由区域管理器管理
    - viewportRegionManager.addRegion(new MessagerRegion()); 添加一个区域
    - viewportRegionManager.display("messager",R.id.desktop_display); 显示指定的区域到视口的指定容器
6.MessagerRegion 消息区域。
    - 声明一个区域：
        @ViewportRegion(name = "messager")
        public class MessagerRegion extends Fragment
    - 区域隐形事件，该事件用于在区域显示或隐藏事调整控件属性：
        public void onHiddenChanged(boolean hidden)
7.区域间的切换方式：
    - 通过视口管理器的display方法切换：
        viewportRegionManager.display(name, R.id.desktop_display);
    - 通过requester
        IRequester requester=site.getService("$.requester");
        Frame frame=new Frame("navigate /#messager netos/1.0");
        requester.request(frame);
        在请求地址#号后面是区域名，类似于http请求地址中的#号用于页面内定位一样，区域在概念上是视口的一部分,故用#号表示。以上/对应的是桌面，所以即表示桌面下的区域messager
8.运行时服务：
    概念：由开发者在运行时可以添加到容器的服务称之为运行时服务，设有A、B两个类
    - A类中注入：@ServiceSite private IServiceSite site;
        则在A跳转到B类前调用：site.addService("myService",new MyService());
    - B类中注入：@ServiceSite private IServiceProvider site;
        则：MyService myService=site.getService("mySerivce");
9.请求器侦中的头的键值和参数在目标视口中的获取方式：
    - 参数和头的获取：getIntent().getStringExtra(key);其中的key的写法是:$.head.name或$.parameter.name 
    - 请求地址获取：getIntent().getDataExtra() 
    - 内容获取：intent.getByteArrayExtra("$.context");
                  
        