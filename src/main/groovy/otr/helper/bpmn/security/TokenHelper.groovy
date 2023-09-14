package otr.helper.bpmn.security

import groovy.json.JsonSlurper
import org.apache.commons.httpclient.Header
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.methods.PostMethod
import otr.helper.bpmn.request.RequestHelper

class TokenHelper {

    private static final URL_DEV = 'http://uidm.uidm-dev.d.exportcenter.ru/sso/oauth2/access_token'
    private static final URL_TEST = 'https://lk.t.exportcenter.ru/sso/oauth2/access_token'
    private String token = null;
    private RequestHelper requestHelper = new RequestHelper();
    private url = null

    TokenHelper() {
        if(requestHelper.getEnvironment().equals("dev")){
            url = URL_DEV;
        }
        else {
            url = URL_TEST;
        }
    }

    public String getToken(){
        if(this.token == null) {
            def grant_type = 'urn:roox:params:oauth:grant-type:m2m'
            def client_secret = 'password'
            def client_id = 'mdm-api-service'

            try {
                def parser = new JsonSlurper();
                HttpClient httpclient = new HttpClient();
                def post = new PostMethod(url)

                post.addRequestHeader(new Header('Accept', 'application/json'))
                post.addRequestHeader(new Header('"Content-Type"', 'application/x-www-form-urlencoded'))
                post.setParameter('client_id', client_id)
                post.setParameter('client_secret', client_secret)
                post.setParameter('realm', '/customer')
                post.setParameter('grant_type', grant_type)
                post.setParameter('service', 'dispatcher')
                httpclient.executeMethod(post);
                def executionResponse = post.getResponseBodyAsString()
                def execution = parser.parseText(executionResponse).getAt('execution')
                post = new PostMethod(url)
                post.addRequestHeader(new Header('Accept', 'application/json'))
                post.addRequestHeader(new Header('"Content-Type"', 'application/x-www-form-urlencoded'))
                post.setParameter('client_id', client_id)
                post.setParameter('client_secret', client_secret)
                post.setParameter('realm', '/customer')
                post.setParameter('grant_type', grant_type)
                post.setParameter('service', 'dispatcher')
                post.setParameter('execution', execution)
                post.setParameter('_eventId', 'next')
                post.setParameter('username', 'bpmn_admin')
                post.setParameter('password', 'password')
                httpclient.executeMethod(post);
                def tokenResponse = post.getResponseBodyAsString()
                def token = parser.parseText(tokenResponse).getAt('access_token')
                println("Token\n" + 'Bearer sso_1.0_' + token)
                this.token = 'Bearer sso_1.0_' + token;
                return 'Bearer sso_1.0_' + token;

            } catch (Exception e) {
                println("ERROR: " + e.toString());
            }
        }
        else {
            return this.token;
        }
    }

}
