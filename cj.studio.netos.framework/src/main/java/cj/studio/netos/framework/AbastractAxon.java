package cj.studio.netos.framework;

public abstract class AbastractAxon implements IAxon {
    LinkEntry head;
    LinkEntry last;
    public AbastractAxon(ISynapsis first, ISynapsis last) {
        head=new LinkEntry(first);
        head.next=new LinkEntry(last);
        this.last=head.next;
    }


    @Override
    public void add(ISynapsis synapsis) {
        LinkEntry entry=getEndConstomerEntry();
        if(entry==null){
            return;
        }
        LinkEntry lastEntry=entry.next;
        entry.next=new LinkEntry(synapsis);
        entry.next.next=lastEntry;
    }

    private LinkEntry getEndConstomerEntry() {
        if(head==null)return null;
        LinkEntry tmp=head;
        do{
            if(last.equals(tmp.next)){
                return tmp;
            }
            tmp=tmp.next;
        }while (tmp.next!=null);
        return null;
    }

    @Override
    public void remove(ISynapsis synapsis) {
        LinkEntry tmp=head;
        do{
            if(synapsis.equals(tmp.next.entry)){
                break;
            }
            tmp=tmp.next;
        }while (tmp.next!=null);
        tmp.next=tmp.next.next;
    }

    @Override
    public void headFlow(Frame frame) {
        nextFlow(frame,null);
    }

    @Override
    public void nextFlow(Frame frame, ISynapsis formthis) {
        if(formthis==null){
            head.entry.flow(frame,this);
            return;
        }
        LinkEntry linkEntry=lookforHead(formthis);
        if(linkEntry==null||linkEntry.next==null)return;
        linkEntry.next.entry.flow(frame,this);
    }

    private LinkEntry lookforHead(ISynapsis formthis) {
        if(head==null)return null;
        LinkEntry tmp=head;
        do{
            if(formthis.equals(tmp.entry)){
                break;
            }
            tmp=tmp.next;
        }while (tmp.next!=null);
        return tmp;
    }

    @Override
    public void dispose() {
        LinkEntry tmp=head;
        while(tmp!=null){
            tmp=tmp.next;
            tmp.entry=null;
            tmp.next=null;
        }
    }

    class LinkEntry{
        LinkEntry next;
        ISynapsis entry;

        public LinkEntry(ISynapsis entry) {
            this.entry=entry;
        }

    }
}
