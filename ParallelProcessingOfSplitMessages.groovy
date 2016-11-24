import groovy.transform.Synchronized;
  
import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
  
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
  
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
 
import com.vordel.jaxprovider.libxml.ElementImpl;
import com.vordel.trace.Trace;
  
def invoke(msg) {
    myinvoke(msg);
}
  
@Synchronized
def myinvoke(msg) {
    Object lock = new Object();
  
    List<ElementImpl> msgsList = msg.get("msgsList");
    int msgsSize = msgsList.size();
    List<Thread> threadsList = new ArrayList<Thread>(msgsSize);
    // This is the list for 200 OK responses, that you will want to join afterward
    List<HttpResponse> OKResponsesList = new ArrayList<HttpResponse>(msgsSize);
    // This is the Map (response -> request) for non 200 OK responses, that you won't join and may want to replay afterward
    Map<HttpResponse, String> otherResponsesMap = new HashMap<HttpResponse>(msgsSize);
  
    for(int i=0; i< msgsSize; i++) {
        // Casting the retrieved XML Nodes of type import com.vordel.jaxprovider.libxml.ElementImpl into Strings
        ElementImpl node = (ElementImpl) msgsList.get(i);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(node), new StreamResult(writer));
        String body = writer.toString();

        // Creating and starting  for each part one thread that will invoke the backend
        Thread t = new Thread(new Runnable() {
            public void run() {
                HttpEntity entity = new StringEntity(body, "UTF-8"); 
                HttpPost post = new HttpPost("http://localhost:8080/sendInfos");
                post.addHeader("Content-Type", "text/xml");
                post.setEntity(entity);
                HttpResponse response = new DefaultHttpClient().execute(post);
                synchronized (lock) {
                    if(response.getStatusLine().getStatusCode() == 200) 
                        OKResponsesList.add(response);
                    else
                        otherResponsesMap.add(response);
                }
            }
        }, "t" + i);
        threadsList.add(t);
        t.start();
    }
 
    // Waiting for all threads to finish
    for(Thread t :  threadsList) {
        t.join();
    }
  
    // Storing OKResponsesList and otherResponsesList in msg attributes for further procesing
    msg.put("OKResponsesList", OKResponsesList);
    msg.put("otherResponsesMap", otherResponsesMap);
  
    return true;
}
