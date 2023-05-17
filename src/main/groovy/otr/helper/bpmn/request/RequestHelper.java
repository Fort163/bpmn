package otr.helper.bpmn.request;

import groovy.json.JsonSlurper;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

public class RequestHelper {

    private static final String PROCESS_INSTANCE = "processInstance";
    private static final String CAMUNDA_ID = "camundaId";

    private Map<String,String> data;
    private  JsonSlurper parser;


    public RequestHelper(){
        parser = new JsonSlurper();
        data = (Map) parser.parse(new File(RequestHelper.class.getClassLoader().getResource("process.json").getFile()));
    }

    public String getProcessInstance(){
        return data.get(PROCESS_INSTANCE);
    }

    public String getCamundaId(){
        return data.get(CAMUNDA_ID);
    }

}
