package com.idexcel.publishToVactrain;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.regions.DefaultAwsRegionProviderChain;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import com.amazonaws.services.securitytoken.model.GetCallerIdentityRequest;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import org.json.JSONObject;

public class LosUtility {
    private static final String SERVICE_NAME = "entity";
    private static final String EVENT_CATEGORY = "notifications";

    private LosUtility(){}

    public static  String getEnvironmentValue(String env){
        Map<String, String> envMap = new HashMap<>();
        envMap.put("dev", "Dev");
        envMap.put("demo", "Demo");
        envMap.put("test", "Test");
        envMap.put("prod", "Prod");
        return envMap.get(env.toLowerCase());
    }

    public static String getAccountId(){
        return AWSSecurityTokenServiceClientBuilder.standard()
                .build()
                .getCallerIdentity(new GetCallerIdentityRequest())
                .getAccount();
    }

    public static String getDefaultRegion(){
        DefaultAwsRegionProviderChain defaultAwsRegionProviderChain = new DefaultAwsRegionProviderChain();
        return defaultAwsRegionProviderChain.getRegion();
    }

    public static String prepareTopicArn(String env){
        return "arn:aws:sns:" + LosUtility.getDefaultRegion() + ":" + LosUtility.getAccountId() + ":Nds-Cync-Los-" + LosUtility.getEnvironmentValue(env) + "-Vactrain";
    }

    private static String buildMesaage(String eventName, String eventType, String realm, PublishMessage notfObj) {
        // creating the message
        JSONObject event = new JSONObject();
        event.put("producer", SERVICE_NAME);
        event.put("category", EVENT_CATEGORY);
        event.put("name", eventName);
        event.put("type", eventType);
        event.put("realm", realm);
        event.put("timeStamp", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

        JSONObject header =  new JSONObject();
        header.put("version", "1.0.0");

        header.put("environment", notfObj.getEnvironment());
        header.put("event", event);

        JSONObject mainObj = new JSONObject();
        mainObj.put("default", "string");
        mainObj.put("header", header);
        mainObj.put("body", notfObj.toJSON());

        return mainObj.toString();
    }

    private static Map<String, MessageAttributeValue> buildMessageAttr(String eventName, Map<String, MessageAttributeValue> additionalAttr) {
        // Setting the message attributes
        Map<String, MessageAttributeValue> attr = new HashMap<>();

        MessageAttributeValue msgAttr1 = new MessageAttributeValue();
        msgAttr1.setDataType("String");
        msgAttr1.setStringValue(EVENT_CATEGORY);
        attr.put("header.event.category", msgAttr1);

        MessageAttributeValue msgAttr2 = new MessageAttributeValue();
        msgAttr2.setDataType("String");
        msgAttr2.setStringValue(eventName);
        attr.put("header.event.name", msgAttr2);

        if(additionalAttr != null) {
            for (String key : additionalAttr.keySet()) {
                attr.put(key, additionalAttr.get(key));
            }
        }
        return attr;
    }


    public static void publishToVactrain(String eventName, String eventType, String realm, PublishMessage notfObj, Map<String, MessageAttributeValue> additionalAttr) {
        try {
            // Setting the request object
            PublishResult result = AmazonSNSClientBuilder.defaultClient()
                    .publish(new PublishRequest()
                            .withTopicArn(prepareTopicArn(notfObj.getEnvironment()))
                            .withMessage(buildMesaage(eventName, eventType, realm, notfObj))
                            .withMessageAttributes(buildMessageAttr(eventName, additionalAttr)));

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}