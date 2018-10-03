package cj.studio.netos.framework;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cj.studio.netos.framework.util.StringUtil;


/**
 * Created by caroceanjofers on 2018/1/19.
 */
//对应服务器上的侦frame结构


/**
 * graph中传输的侦
 * <p>
 * <pre>
 * 它包含：字节、参数和头信息，但不能在sink间传递属性引用，如果需要可使用plug.circirl回路
 * 还有：网络中传输的也是侦，不必考虑侦在网络数据流中的消息边界，这交由netty完成分隔
 * </pre>
 *
 * @author carocean
 */
public class Frame {
    protected Map<String, String> headmap;
    protected Map<String, String> parametermap;
    protected ByteArrayOutputStream content;
    static final String CODE = "utf-8";
    static String QUERY_STRING_REG = "(^|\\?|&)\\s*%s\\s*=\\s*([^&]*)(&|$)";


    public void dispose() {
        headmap.clear();
        parametermap.clear();
        try {
            content.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void print(StringBuffer sb) {
        print("  ","    ","---------------------------------",sb);
    }
    public void print(String segmentindent, String itemindent, String itemspliter, StringBuffer sb) {
        sb.append(String.format("%s\r\n",this));
        sb.append(String.format("%sheader：\r\n",segmentindent));
        if (headmap != null) {
            for (String key : headmap.keySet()) {
                sb.append(String.format("%s%s=%s\r\n", itemindent, key, headmap.get(key)));
            }
        }
        sb.append(String.format("%sparmenters：\r\n",segmentindent));
        if (parametermap != null) {
            for (String key : parametermap.keySet()) {
                sb.append(String.format("%s%s=%s\r\n", itemindent, key, parametermap.get(key)));
            }
        }
        sb.append(String.format("%scontent：\r\n",segmentindent));
        if(content.size()>0) {
            sb.append(String.format("%s%s\r\n", itemindent, new String(content.toByteArray())));
        }
        sb.append(String.format("%s",itemspliter));
    }
    /**
     * 传入侦头
     * <p>
     * <pre>
     * 如：
     *  GET /PATH?param=22 HTTP/1.1
     *  其中querystring就是侦参数的简写形式，它会转变到侦参数中
     *
     * </pre>
     *
     * @param frame_line
     */
    public Frame(String frame_line) {
        init();

        String[] arr = frame_line.split(" ");// 这种方法如果地址参数中含有空格，则会解析错误，因此将来应改为正则
        if (arr.length < 3)
            throw new RuntimeException("侦头行格式错");
        String cmd = arr[0];
        String pro = arr[arr.length - 1];
        if (!pro.contains("/") || pro.indexOf("/") == pro.length() - 1) {
            throw new RuntimeException("侦没指定协议");
        }
        String mid = frame_line
                .substring(cmd.length(), frame_line.length() - pro.length())
                .trim();
        if (StringUtil.isEmpty(mid))
            throw new RuntimeException("侦路径错,如果没有路径，至少指定一个/号");
        arr = new String[]{cmd, mid, pro};
        head("command", arr[0]);
        String uri = arr[1];
        head("url", uri);
        // if (uri.contains("?")) {
        // String parr[] = uri.split("\\?");
        // if (parr.length > 1) {
        // String arr2[] = parr[1].split("&");
        // for (String p : arr2) {
        // String[] arr3 = p.split("=");
        // if (arr3.length > 1)
        // parametermap.put(arr3[0], arr3[1]);
        // else
        // parametermap.put(arr3[0], "");
        // }
        // }
        // }
        head("protocol", arr[2].toUpperCase());
    }

    Frame() {
        init();
    }

    /**
     * 通过侦数据构造侦
     * <p>
     * <pre>
     * heads CRLF
     * CRLF
     * params CRLF
     * CRLF
     * content
     * </pre>
     *
     * @param frameRaw
     */
    public Frame(byte[] frameRaw) {
        init();
        int up = 0;
        int down = 0;
        byte field = 0;// 0=heads;1=params;2=content

        while (down < frameRaw.length) {
            if (field < 2) {//修改了当内容的头几行是连续空行的情况的bug因此使用了field<2
                if (frameRaw[up] == '\r' && (up + 1 < frameRaw.length
                        && frameRaw[up + 1] == '\n')) {// 跳域
                    field++;
                    up += 2;
                    down += 2;
                    continue;
                }
            } else {
                down = frameRaw.length;// 非常变态，bytebuf数组总是在结尾入多一个0，因此其长度总是比写入的长度多1个字节
                byte[] b = new byte[down - up];
                System.arraycopy(frameRaw, up, b, 0, b.length);
                content.write(b, 0, b.length);
                break;
            }
            if (frameRaw[down] == '\r' && (down + 1 < frameRaw.length
                    && frameRaw[down + 1] == '\n')) {// 跳行
                byte[] b = new byte[down - up];
                System.arraycopy(frameRaw, up, b, 0, b.length);
                try {
                    switch (field) {
                        case 0:
                            String kv = new String(b, CODE);
                            int at = kv.indexOf("=");
                            String k = kv.substring(0, at);
                            String v = kv.substring(at + 1, kv.length());
                            if ("protocol".equals(k)) {
                                if (v != null)
                                    v = v.toUpperCase();
                            }
                            headmap.put(k, v);
                            // if ("url".equals(k)
                            // && !StringUtil.isEmpty(queryString())) {
                            // String[] pair = queryString().split("&");
                            // for (String a : pair) {
                            // String[] t = a.split("=");
                            // String s = t.length > 1 ? t[1] : null;
                            // parametermap.put(t[0], s);
                            // }
                            // }
                            break;
                        case 1:
                            kv = new String(b, CODE);
                            at = kv.indexOf("=");
                            k = kv.substring(0, at);
                            v = kv.substring(at + 1, kv.length());
                            parametermap.put(k, "".equals(v) ? null : v);
                            break;
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                down += 2;
                up = down;
                continue;
            }
            down++;
        }
    }


    /*
     * public static void main(String... content) throws CircuitException {
     * Frame f = new Frame("get   /a/b net/1.0"); f.parameter("it", "33");
     * f.content.writeBytes("df\r\n\r\nmm".getBytes());
     * System.out.println(f.rootName()); Frame n =
     * f.fillToFrame(HttpFrame.class);
     *
     * System.out.println(n); System.out.println(((byte) '\"')); // f.head("t1",
     * "t1"); // f.parameter("p1", null); // System.out.println(f.url()); //
     * f.content //
     * .writeBytes("近几个月以来，解放军南京军事学院主要领导发生密集调整，政委、副政委、副院长、训练部部长、政治部主任等职位相继易人" //
     * .getBytes()); // byte[] b = f.toBytes(); // // Frame f2 = new Frame(b);
     * // byte[] b2 = new byte[f2.content.readableBytes()]; //
     * f2.content.readBytes(b2); // boolean is = f2.parameter("p1") == null ?
     * true : false; // System.out.println(new String(b2)); }
     */
    private void init() {
        headmap = new Hashtable<String, String>(8);
        parametermap = new Hashtable<String, String>(4);
        content = createContent(8192);
    }

    protected ByteArrayOutputStream createContent(int initLen) {

        return new ByteArrayOutputStream(initLen);
    }

    public synchronized byte[] toBytes()throws NetException {
        ByteArrayOutputStream b=new ByteArrayOutputStream();
        byte[] crcf = null;
        try {
            crcf = "\r\n".getBytes(CODE);
        } catch (UnsupportedEncodingException e) {
            throw new NetException(e);
        }
        byte[] ctxCopy=content.toByteArray();

        // if(!headmap.containsKey("Content-Length")){
        headmap.put("Content-Length",
                Integer.toString(ctxCopy.length));
        // }
        for (String key : headmap.keySet()) {
            String v = headmap.get(key);
            if (StringUtil.isEmpty(v)) {
                continue;
            }
            String tow = key + "=" + v + "\r\n";
            try {
                byte[] towb=tow.getBytes(CODE);
                b.write(towb,0,towb.length);
            } catch (UnsupportedEncodingException e) {
                throw new NetException(e);
            }
        }
        b.write(crcf,0,crcf.length);
        for (String key : parametermap.keySet()) {
            String v = parametermap.get(key);
            if (/* StringUtil.isEmpty(v) || */containedQueryStrParam(key)) {
                continue;
            }
            String tow = key + "=" + (StringUtil.isEmpty(v) ? "" : v) + "\r\n";
            try {
                byte[] towb=tow.getBytes(CODE);
                b.write(towb,0,towb.length);
            } catch (UnsupportedEncodingException e) {
                throw new NetException(e);
            }
        }
        b.write(crcf,0,crcf.length);
        b.write(ctxCopy,0,ctxCopy.length);// 非常变态，bytebuf数组总是在结尾入多一个0，因此其长度总是比写入的长度多1个字节
        return b.toByteArray();
    }

    public String[] enumHeadName() {
        return headmap.keySet().toArray(new String[0]);
    }

    public String contentType() {
        return head("Content-Type");
    }

    /**
     * min类型
     * <p>
     * <pre>
     * 如frame/bin,frame/json,others
     * </pre>
     *
     * @param type
     */
    public void contentType(String type) {
        head("Content-Type", type);
    }

    public String head(String name) {
        return headmap.get(name);
    }

    public boolean containsHead(String name) {
        return headmap.containsKey(name);
    }

    /**
     * 判断原始地址(非全地址）中是否存在查询串，即包含?号
     * <p>
     * <pre>
     *
     * </pre>
     *
     * @return
     */
    public boolean containsQueryString() {
        return headmap.get("url").indexOf("?") >= 0;
    }

    public boolean containsParameter(String key) {
        if (parametermap.containsKey(key))
            return true;
        return containedQueryStrParam(key);
    }

    private boolean containedQueryStrParam(String key) {
        String q = queryString();
        if (StringUtil.isEmpty(q))
            return false;
        // String[] arr = q.split("&");
        // for (String pair : arr) {
        // String[] e = pair.split("=");
        // if (e[0].equals(key)) {
        // return true;
        // }
        // }
        Pattern p = Pattern.compile(String.format(QUERY_STRING_REG, key));
        Matcher m = p.matcher(url());
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 设置头信息
     * <p>
     * <pre>
     * 注：
     * －有关net回路的请求头设置，请使用NetConstans常量
     * </pre>
     *
     * @param key
     * @param v
     */
    public void head(String key, String v) {
        if (StringUtil.isEmpty(v))
            return;
        if ((key.contains("\r") || key.contains("\n"))) {
            throw new RuntimeException(String.format(
                    "key 不能包含\\r 或 \\n. key is %s, value is %s", key, v));
        }
        if ((v.contains("\r") || v.contains("\n"))) {
            throw new RuntimeException(String.format(
                    "value 不能包含\\r 或 \\n. key is %s, value is %s", key, v));
        }
        if ("protocol".equalsIgnoreCase(key)) {
            v = v.toUpperCase();
        }
        // if ("url".equals(key) && v.indexOf("?") > 0) {
        // String qstr = v.substring(v.indexOf("?") + 1, v.length());
        // if (!StringUtil.isEmpty(qstr)) {
        // String[] qarr = qstr.split("&");
        // for (String kv : qarr) {
        // String k = "";
        // String value = "";
        // if (kv.contains("=")) {
        // int pos = kv.indexOf("=");
        // k = kv.substring(0, pos).trim();
        // value = kv.substring(pos + 1, kv.length());
        // parametermap.put(k, value);
        // }
        // }
        // }
        // String url = v.substring(0, v.indexOf("?"));
        // headmap.put("url", url);
        // return;
        // }
        headmap.put(key, v);
    }

    public void removeHead(String key) {
        headmap.remove(key);
    }

    public String[] enumParameterName() {
        if (headmap.get("url").indexOf("?") < 0) {
            return parametermap.keySet().toArray(new String[0]);
        }
        List<String> keys = new ArrayList<>();
        String[] arr = queryString().split("&");
        for (String pair : arr) {
            if (StringUtil.isEmpty(pair)) {
                continue;
            }
            String[] e = pair.split("=");
            keys.add(e[0]);
        }
        for (String key : parametermap.keySet()) {
            keys.add(key);
        }
        return keys.toArray(new String[0]);
    }

    public String parameter(String name) {
        if (parametermap.containsKey(name))
            return parametermap.get(name);
        Pattern p = Pattern.compile(String.format(QUERY_STRING_REG, name));
        Matcher m = p.matcher(url());
        if (m.find()) {
            return m.group(2).trim();
        }
        return null;
    }

    /**
     * 参数
     * <p>
     * <pre>
     * 注：querystring的参数不能被覆盖，否则报异常
     * </pre>
     *
     * @param key
     * @param v
     */
    public void parameter(String key, String v) {
        if (StringUtil.isEmpty(v))
            return;
        if ((key.contains("\r") || key.contains("\n"))) {
            throw new RuntimeException("不能包含\\r 或 \\n");
        }
        if (v.contains("\r") || v.contains("\n")) {
            throw new RuntimeException("不能包含\\r 或 \\n");
        }
        if (containedQueryStrParam(key)) {
            throw new RuntimeException("不可覆盖querystring参数." + key);
        }
        parametermap.put(key, v);
    }

    /*
     * { "head":{"key1":"v1","key2":"v2"}, "para":{"key1":"v1","key2":"v2"},
     * "content":"" }
     */
    public String toJson() {
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        if (headmap.isEmpty()) {
            sb.append("\"head\":{}");
        } else {
            sb.append("\"head\":{");
        }
        for (String key : headmap.keySet()) {
            String v = headmap.get(key);
            if (StringUtil.isEmpty(v)) {
                v = "";
            }
            sb.append(String.format("\"%s\":\"%s\",", key, v));
        }
        if (!headmap.isEmpty()) {
            sb.append("#$#}");
        }
        if (parametermap.isEmpty()) {
            sb.append(",\"para\":{}");
        } else {
            sb.append(",\"para\":{");
        }
        for (String key : parametermap.keySet()) {
            String v = parametermap.get(key);
            if (StringUtil.isEmpty(v)) {
                v = "";
            }
            sb.append(String.format("\"%s\":\"%s\",", key, v));
        }
        if (!parametermap.isEmpty()) {
            sb.append("#$#}");
        }
        byte[] b=content.toByteArray();
        if (b.length > 0) {

            String str = new String(b);
            str = str.replace("'", "\\'").replace("\"", "\\\"");// 在js中可转义回原型
            sb.append(",\"content\":\"" + str + "\"");
        } else {
            sb.append(",\"content\":\"\"");
        }
        sb.append("}");
        return sb.toString().replace(",#$#", "");
    }

    public static Frame createFrame(String json, Class<? extends Frame> type) {
        try {
            Object o = type.newInstance();
            Frame f = (Frame) o;
            f.fromJson(json);
            return f;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void fromJson(String text) {
        Gson gson = new Gson();
        JsonElement e = gson.fromJson(text, JsonElement.class);
        JsonObject f = e.getAsJsonObject();
        JsonElement heade = f.get("head");
        if (heade != null) {
            JsonObject head = heade.getAsJsonObject();
            for (Map.Entry<String, JsonElement> en : head.entrySet()) {
                headmap.put(en.getKey(), en.getValue() == null ? ""
                        : en.getValue().getAsString());
            }
        }
        JsonElement parae = f.get("para");
        if (heade != null) {
            JsonObject para = parae.getAsJsonObject();
            for (Map.Entry<String, JsonElement> en : para.entrySet()) {
                parametermap.put(en.getKey(), en.getValue() == null ? ""
                        : en.getValue().getAsString());
            }
        }
        JsonElement conte = f.get("content");
        if (conte != null) {
            byte[] b=conte.getAsString().getBytes();
            content.write(b,0,b.length);
        }
    }

    public void removeParameter(String key) {
        parametermap.remove(key);
    }

    /**
     * 协议均为大写
     * <p>
     * <pre>
     *
     * </pre>
     *
     * @return
     */
    public String protocol() {
        return head("protocol");
    }

    /**
     * 协议
     * <p>
     * <pre>
     * 输入将转换为大写
     * </pre>
     *
     * @param protocol
     */
    public void protocol(String protocol) {
        head("protocol", protocol.toUpperCase());
    }

    public ByteArrayOutputStream content() {
        return content;
    }

    /**
     * 返回原地址（包括查询串）
     * <p>
     * <pre>
     * 原地址是构造侦时的初始地址(即便是向侦添加了参数，原地址也不变)
     * </pre>
     *
     * @return
     */
    public String url() {
        return headmap.get("url");
    }

    /**
     * 根路径名，如果url=/则根路径为""
     * <p>
     * <pre>
     *
     * </pre>
     *
     * @return
     */
    public String rootName() {
        String root = rootPath();
        if (root.equals("/")) {
            return "";
        } else {
            return root.substring(1, root.length());
        }
    }

    /**
     * 不带root路径的路径，不包含查询串
     */
    public String relativePath() {
        String path = path();
        path = path.substring(rootPath().length(), path.length());
        if (!path.startsWith("/")) {
            path = String.format("/%s", path);
        }
        return path;
    }

    /**
     * 不带root的原地址，包含查询串
     * <p>
     * <pre>
     *
     * </pre>
     *
     * @return
     */
    public String relativeUrl() {
        String rurl = url();
        rurl = rurl.substring(rootPath().length(), rurl.length());
        if (!rurl.startsWith("/")) {
            rurl = String.format("/%s", rurl);
        }
        return rurl;
    }

    /**
     * url中的根路径
     * <p>
     * <pre>
     * 如果url=/则root根为/
     * 否则root即为第一个/与第二个/（如果有）之间，且root值包括第一个/
     * </pre>
     *
     * @return
     */
    public String rootPath() {
        String path = path();
        if ("/".equals(path))
            return path;
        path = path.startsWith("/") ? path : String.format("/%s", path);

        int nextSp = path.indexOf("/", 1);
        if (nextSp < 0) {
            if (path.indexOf(".") >= 0) {
                return "/";
            } else {
                return path;
            }
        }
        path = path.substring(0, nextSp);
        return path;
    }

    /**
     * 带root路径的地址，无查询串
     * <p>
     * <pre>
     *
     * </pre>
     *
     * @return
     */
    public String path() {
        String p = null;
        String url = url();

        if (url.contains("?")) {
            // String arr[] = url.split("\\?");
            p = url.substring(0, url.indexOf("?"));
        } else {
            p = url;
        }
        return p;
    }
    public boolean containsSharpSymbols(){
        return path().lastIndexOf("#")>0;
    }
    public String withoutSharpSymbolsPath(){
        String p=path();
        int pos=p.lastIndexOf("#");
        if(pos>0){
            p=p.substring(0,pos);
        }
        return p;
    }
    public String sharpSymbolsName(){
        String p=path();
        if(p.endsWith("/")){
            p=p.substring(0,p.length()-1);
        }
        int pos=p.lastIndexOf("#");
        if(pos>0){
            p=p.substring(pos+1,p.length());
            return p;
        }
        return "";
    }
    /**
     * 请求的文件名，不含路径，含扩展名
     * <p>
     * <pre>
     * 注意：如果文件名中不包含.号，视为目录，当是目录时返回的文件名为空串
     * </pre>
     *
     * @return
     */
    public String name() {
        String p = path();
        if (p.endsWith("/"))
            return "";
        p = p.substring(p.lastIndexOf("/") + 1, p.length());
        if (!p.contains(".")) {
            return "";
        }
        return p;
    }

    /**
     * 返回原地址的查询串
     * <p>
     * <pre>
     *
     * </pre>
     *
     * @return
     */
    public String queryString() {
        String q = "";
        String url = url();
        if (url.contains("?")) {
            // String[] arr = url.split("\\?");
            // if (arr.length > 1) {
            // q = arr[1];
            // }
            q = url.substring(url.indexOf("?") + 1, url.length());
        }
        return q;
    }

    /**
     * 找回路径
     * <p>
     * <pre>
     * 即：将所有参数拼到地址后面
     * </pre>
     *
     * @return
     */
    public String retrieveUrl() {
        String q = retrieveQueryString();
        if (StringUtil.isEmpty(q)) {
            return url();
        }
        String url = String.format("%s?%s", path(), q);
        return url;
    }

    /**
     * 找回查询串
     * <p>
     * <pre>
     * 即：将所有参数拼到原查询串后面
     * </pre>
     *
     * @return
     */
    public String retrieveQueryString() {
        String q = queryString();
        if (!StringUtil.isEmpty(q) && q.endsWith("&")) {
            q = q.substring(0, q.length() - 1);
        }
        Set<String> set = parametermap.keySet();
        for (String key : set) {
            String v = parametermap.get(key);

            q = String.format("%s&%s=%s", q, key, v);

        }
        if (q.startsWith("&")) {
            q = q.substring(1, q.length());
        }
        return q;
    }

    /**
     * 找回路径
     * <p>
     * <pre>
     * 即：将所有参数拼到地址后面
     * </pre>
     *
     * @return
     */
    public String retrieveUrlAndEncode(String charset) {
        String q = retrieveQueryStringAndEncode(charset);
        if (StringUtil.isEmpty(q)) {
            return url();
        }
        String url = String.format("%s?%s", path(), q);
        return url;
    }

    /**
     * 找回查询串
     * <p>
     * <pre>
     * 即：将所有参数拼到原查询串后面
     * </pre>
     *
     * @return
     */
    public String retrieveQueryStringAndEncode(String charset) {
        String q = queryString();
        if (!StringUtil.isEmpty(q) && q.endsWith("&")) {
            q = q.substring(0, q.length() - 1);
        }
        Set<String> set = parametermap.keySet();
        for (String key : set) {
            String v = parametermap.get(key);
            try {
                v = URLEncoder.encode(v, charset);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            q = String.format("%s&%s=%s", q, key, v);

        }
        if (q.startsWith("&")) {
            q = q.substring(1, q.length());
        }
        return q;
    }

    /**
     * 找回路径
     * <p>
     * <pre>
     * 即：将所有参数拼到地址后面
     * </pre>
     *
     * @return
     */
    public String deepRetrieveUrlAndEncode(String charset) {
        String q = deepRetrieveQueryStringAndEncode(charset);
        if (StringUtil.isEmpty(q)) {
            return url();
        }
        String url = String.format("%s?%s", path(), q);
        return url;
    }

    /**
     * 找回查询串
     * <br>将深度查找QueryString。即除了在参数集合中找，还分析地址中带的参数并编码
     * <pre>
     * 即：将所有参数拼到原查询串后面
     * </pre>
     *
     * @return
     */
    public String deepRetrieveQueryStringAndEncode(String charset) {
        String q = queryString();
        if (!StringUtil.isEmpty(q) && q.endsWith("&")) {
            q = q.substring(0, q.length() - 1);
        }
        if (!StringUtil.isEmpty(q)) {
            String arr[] = q.split("&");
            String nq = "";
            for (String kv : arr) {
                if (StringUtil.isEmpty(kv)) continue;
                int pos = kv.indexOf("=");
                if (pos < 0) continue;
                String k = kv.substring(0, pos);
                String v = kv.substring(pos + 1, kv.length());
                if (!StringUtil.isEmpty(v)) {
                    try {
                        v = URLEncoder.encode(v, charset);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                nq = String.format("%s&%s=%s", nq, k, v);
            }
            q = nq;
        }
        Set<String> set = parametermap.keySet();
        for (String key : set) {
            String v = parametermap.get(key);
            try {
                v = URLEncoder.encode(v, charset);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            q = String.format("%s&%s=%s", q, key, v);

        }
        if (q.startsWith("&")) {
            q = q.substring(1, q.length());
        }
        return q;
    }

    public String command() {
        return head("command").trim();
    }

    public void command(String cmd) {
        head("command", cmd.trim());
    }

    public String contentChartset() {
        return head("content-chartset");
    }

    public void contentChartset(String chartset) {
        head("content-chartset", chartset);
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", command(), url(), protocol());
    }

    /**
     * 将指定的侦拷贝到当前侦
     * <p>
     * <pre>
     * 浅表拷贝的有：
     *  1.头,不拷贝指令行，其它深表拷贝
     *  2.参数，引用
     *  3.内容，不拷贝，只引用
     *  深表拷贝的有：
     *  1.头，不拷贝指令行，其它深表拷贝
     *  2.参数，深表拷贝
     *  3.内容，拷贝
     * </pre>
     *
     * @param frame
     * @param shallow true浅表拷贝, false深表拷贝（deep copy)。
     */
    public void copyFrom(Frame frame, boolean shallow) {
        if (shallow) {
            this.content = frame.content;
            this.parametermap = frame.parametermap;
            for (String key : frame.headmap.keySet()) {
                if (key.equalsIgnoreCase("protocol")
                        || key.equalsIgnoreCase("url")
                        || key.equalsIgnoreCase("command")) {
                    continue;
                }
                headmap.put(key, frame.headmap.get(key));
            }

        } else {
           byte[] b= frame.content.toByteArray();
            content.reset();
            content.write(b,0,b.length);
            for (String key : frame.headmap.keySet()) {
                if (key.equalsIgnoreCase("protocol")
                        || key.equalsIgnoreCase("url")
                        || key.equalsIgnoreCase("command")) {
                    continue;
                }
                headmap.put(key, frame.headmap.get(key));
            }
            for (String key : frame.parametermap.keySet()) {
                parametermap.put(key, frame.parametermap.get(key));
            }
        }
    }

    /**
     * 将指定的侦拷贝到当前侦<br>
     * 深表拷贝
     * <p>
     * <pre>
     * 深表拷贝的有：
     *  深表拷贝的有：
     *  1.头，深表，不拷贝指令行
     *  2.参数，深表拷贝
     *  3.内容，不拷贝
     * </pre>
     *
     * @param frame
     */
    public void copyFrom(Frame frame) {
        copyFrom(frame, false);
    }

    /**
     * 判断url路么是否是目录
     * <p>
     * <pre>
     *
     * </pre>
     *
     * @return
     */
    public boolean isDirectory() {
        String path = path();
        String fn = path.substring(path.lastIndexOf("/") + 1, path.length());
        return !fn.contains(".");
    }

    /**
     * 获取扩展名，如果没有则返回空串""
     * <p>
     * <pre>
     *
     * </pre>
     *
     * @return
     */
    public String extName() {
        String ruri = relativePath();
        Pattern pat = Pattern.compile("\\w+\\.(\\w+)$");
        Matcher m = pat.matcher(ruri);
        boolean isfile = m.find();
        if (isfile) {
            return m.group(1);
        }
        return "";
    }

    public Frame copy() {
        Frame frame = null;

        frame = new Frame();

        frame.headmap.putAll(this.headmap);
        frame.parametermap.putAll(this.parametermap);
        // if (frame.content.refCnt() > 0)
        // frame.content.release();
        byte[] b= content.toByteArray();
        frame.content.reset();
        frame.content.write(b,0,b.length);
        return frame;
    }

    public Frame copy(Frame frame) {
        frame.headmap.putAll(this.headmap);
        frame.parametermap.putAll(this.parametermap);
        // if (frame.content.refCnt() > 0)
        // frame.content.release();
        byte[] b= content.toByteArray();
        frame.content.reset();
        frame.content.write(b,0,b.length);
        return frame;
    }


    public void url(String url) {
        head("url", url);
    }

    public static void main(String... strings) {
        Frame f = new Frame("get /test/1.html?des=20&type=中国人 http/1.1");
        f.parameter("user", "li");
        f.parameter("pwd", "11");
        System.out.println(f.deepRetrieveUrlAndEncode("utf-8"));
//		String p = URLEncoder.encode("干啥里");
//		Frame f = new Frame(String.format(
//				"get /test/u.s?uid=吃了没& swsid = 55555 & p=%s&name = |&z= peer/1.0",
//				p));
//		f.parameter("cjtoken", "yyyyy");
//		System.out.println(f);
//		System.out.println(f.parameter("cjtoken"));
//		System.out.println(f.parameter("uid"));
//		System.out.println(f.parameter("swsid"));
//		System.out.println(f.parameter("p"));
//		System.out.println(f.parameter("name"));
//		System.out.println(f.containsParameter("name"));
//		System.out.println(f.containsParameter("z"));
//		System.out.println(f.path());
//		System.out.println(f.relativePath());
//		System.out.println(f.queryString());
//		System.out.println(f.rootName());
//		System.out.println(f.rootPath());
//		System.out.println(f.retrieveQueryString());
//		System.out.println(f.retrieveUrl());
//		byte[] b = f.toBytes();
//		Frame f2 = new Frame(b);
//		System.out.println("--------------");
//		System.out.println(f2);
//		System.out.println(f2.parameter("cjtoken"));
//		System.out.println(f2.parameter("uid"));
//		System.out.println(f2.parameter("swsid"));
//		System.out.println(f2.parameter("p"));
//		System.out.println(f2.parameter("name"));
//		System.out.println(f2.containsParameter("name"));
//		System.out.println(f.path());
//		System.out.println(f2.relativePath());
//		System.out.println(f2.queryString());
//		System.out.println(f2.rootName());
//		System.out.println(f2.rootPath());
//		System.out.println(f2.retrieveQueryString());
//		System.out.println(f.retrieveUrl());
//		System.out.println(f2.relativeUrl());
//		String[] keys = f2.enumParameterName();
//		for (String key : keys) {
//			System.out.println("key:" + key);
//		}
//		Frame f3 = new Frame("GET /device?swsid=6666666106&uid=sandy HTTP/1.1");
//		f3.content.writeBytes("fff".getBytes());
//		// f3.parametermap.put("ss", "vv");
//		System.out.println(new String(f3.toBytes()));
//		System.out.println("000000");
//		System.out.print("a\r\n\r\n\r\nb");
//		// System.out.println(f3.rootPath());
//		// Frame f4 = new Frame("GET /22.jpg HTTP/1.1");
//		// System.out.println(f4.rootPath());
    }

}

