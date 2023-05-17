package otr.helper.bpmn.request.param

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import groovy.json.JsonSlurper
import org.apache.commons.httpclient.Header
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.GetMethod
import otr.helper.bpmn.request.RequestHelper
import otr.helper.bpmn.security.TokenHelper

class ParamRequest {

    private RequestHelper requestHelper = new RequestHelper();
    private TokenHelper tokenHelper = new TokenHelper();

    def URL_BPMN = 'http://bpmn-api-service.bpms-dev.d.exportcenter.ru/bpmn/api/v1/bpmn/'
    def parser = new JsonSlurper();
    def httpclient = new HttpClient();

    public LinkedHashMap<String,Object> getParam(String paramName){
        def get = new GetMethod(URL_BPMN+'history/process-instance/'+requestHelper.getProcessInstance()+'/variables?size=1000');
        get.addRequestHeader(new Header('Accept', '*/*'))
        get.addRequestHeader(new Header('camundaId', requestHelper.getCamundaId()))
        get.addRequestHeader(new Header('Authorization', tokenHelper.getToken()))
        httpclient.executeMethod(get);
        def byteArray = get.getResponseBody()
        def find = parser.parse(byteArray, 'UTF-8').content.find({ item -> { item.name == paramName } }).value
        if(find instanceof Map) {
            createFileParam(find, paramName);
        }
        return find
    }

    public LinkedHashMap<String,Object> getAllParam(){
        def get = new GetMethod(URL_BPMN+'history/process-instance/'+requestHelper.getProcessInstance()+'/variables?size=1000');
        get.addRequestHeader(new Header('Accept', '*/*'))
        get.addRequestHeader(new Header('camundaId', requestHelper.getCamundaId()))
        get.addRequestHeader(new Header('Authorization', tokenHelper.getToken()))
        httpclient.executeMethod(get);
        def byteArray = get.getResponseBody()
        return parser.parse(byteArray,'UTF-8').content
    }

    private createFileParam(Map map,String paramName){
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        String jsonOutput = gson.toJson(map);
        def location = this.class.getLocation()
        def file = location.getFile().replace("/target/classes/","/src/main/resources/paramFile/"+paramName+".json")
        new File(file).write(jsonOutput,'UTF-8')
    }

}
