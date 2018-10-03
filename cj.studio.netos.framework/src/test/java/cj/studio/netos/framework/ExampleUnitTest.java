package cj.studio.netos.framework;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
    @Test
    public  void TestAxon(){
        IAxon axon=new InternalAxon(new Syn("first"),new Syn("last"));
        axon.add(new Syn("3"));
        Syn syn4=new Syn("4");
        axon.add(syn4);
        axon.add(new Syn("5"));
        axon.add(new Syn("6"));
//        axon.remove(syn4);
        axon.nextFlow(new Frame("get / http/1.1"),null);
    }
    class Syn implements ISynapsis{
        String t;
        public Syn(String t){
            this.t=t;
        }
        @Override
        public void flow(Frame frame, IAxon axon) {
            System.out.println(this.t);
            axon.nextFlow(frame,this);
        }

        @Override
        public void dispose() {

        }
    }
}