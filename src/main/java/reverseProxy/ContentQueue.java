package reverseProxy;

import java.nio.ByteBuffer;
import java.util.LinkedList;

/**
 * some by shaf on 6/25/17.
 */
public class ContentQueue {
    LinkedList<Object> content = new LinkedList<Object>();

    public void addContent(Object byteBuffer) {
        content.add(byteBuffer);
    }

    public Object getContent() {
        return content.poll();
    }

    public int getSize() {
        return this.content.size();
    }
}
