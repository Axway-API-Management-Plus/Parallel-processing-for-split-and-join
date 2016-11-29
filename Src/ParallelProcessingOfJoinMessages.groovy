import groovy.transform.Synchronized;
  
import java.io.ByteArrayOutputStream;
import java.util.List;
import com.vordel.trace.Trace;
import org.apache.http.HttpResponse;
  
def invoke(msg) {
    myinvoke(msg);
}
 
@Synchronized
def myinvoke(msg) {
  
    // Joining OK responses, building the overall response and storing it into joinedResponse attribute
    List<HttpResponse> OKresponsesList = msg.get("OKResponsesList");
    StringBuffer joinedResponseBuffer = new StringBuffer();
    for(HttpResponse resp : OKresponsesList) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resp.getEntity().writeTo(baos);
        joinedResponseBuffer.append(new String(baos.toByteArray())).append("\n");
    }
    String joinedResponse = joinedResponseBuffer.toString();
    msg.put("joinedResponse", joinedResponse);

    return true;
}
