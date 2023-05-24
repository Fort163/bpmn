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

    public Object getParam(String paramName){
        this.getParam(paramName,false);
    }

    public Object getParam(String paramName,Boolean isFromFile){
        if(isFromFile){
            def param = readFileParam(paramName);
            if(param != null){
                return param;
            }
        }
        def get = new GetMethod(URL_BPMN+'history/process-instance/'+requestHelper.getProcessInstance()+'/variables?size=1000');
        get.addRequestHeader(new Header('Accept', '*/*'))
        get.addRequestHeader(new Header('camundaId', requestHelper.getCamundaId()))
        get.addRequestHeader(new Header('Authorization', tokenHelper.getToken()))
        httpclient.executeMethod(get);
        def byteArray = get.getResponseBody()
        /*def find = parser.parse(byteArray, 'UTF-8').content.findAll({ item -> { item.name == paramName } }).last().value
        */def result = [:]
        def list = parser.parse(byteArray, 'UTF-8').content.findAll({ item -> { item.name == paramName } })
        list.forEach(item -> {
            if(result.createTime == null){
                result = item;
            }
            else {
                if(item.createTime > result.createTime){
                    result = item;
                }
            }
        })
        if(result.value instanceof Map) {
            createFileParam(result.value, paramName);
        }
        return result.value
    }

    public Object getAllParam(){
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

    private Map readFileParam(String paramName){
        def location = this.class.getLocation()
        def fileStr = location.getFile().replace("/target/classes/","/src/main/resources/paramFile/"+paramName+".json")
        if(new File(fileStr).exists()) {
            def jsonSlurper = new JsonSlurper()
            def paramMap = jsonSlurper.parse(new File(fileStr))
            return paramMap;
        }
        else {
            return null
        }
    }

}
