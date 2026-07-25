package com.trevorism.gcloud

import com.google.gson.Gson
import com.trevorism.http.util.InvalidRequestException
import com.trevorism.https.AppClientSecureHttpClient
import com.trevorism.https.SecureHttpClient

this.metaClass.mixin(io.cucumber.groovy.Hooks)
this.metaClass.mixin(io.cucumber.groovy.EN)

String objectUrl = (System.getenv("ACCEPTANCE_BASE_URL") ?: "https://memory.data.trevorism.com") + "/object"
SecureHttpClient secureHttpClient = new AppClientSecureHttpClient()
Gson gson = new Gson()

int statusCode = 0
def created = null

Given(~/^an object with id "([^"]*)" of type "([^"]*)" exists$/) { String id, String kind ->
    try {
        secureHttpClient.delete("${objectUrl}/${kind}/${id}".toString())
    } catch (InvalidRequestException ignored) {
    }
    secureHttpClient.post("${objectUrl}/${kind}".toString(), gson.toJson([id: id, marker: "acceptance"]))
}

Given(~/^no object with id "([^"]*)" of type "([^"]*)" exists$/) { String id, String kind ->
    try {
        secureHttpClient.delete("${objectUrl}/${kind}/${id}".toString())
    } catch (InvalidRequestException ignored) {
    }
}

When(~/^I create an object with id "([^"]*)" of type "([^"]*)" again$/) { String id, String kind ->
    try {
        secureHttpClient.post("${objectUrl}/${kind}".toString(), gson.toJson([id: id, marker: "acceptance"]))
        statusCode = 200
    } catch (InvalidRequestException e) {
        statusCode = e.statusCode
    }
}

When(~/^I create an object with id "([^"]*)" of type "([^"]*)"$/) { String id, String kind ->
    created = secureHttpClient.post("${objectUrl}/${kind}".toString(), gson.toJson([id: id, marker: "acceptance"]))
}

Then(~/^the create is rejected as a conflict$/) { ->
    assert statusCode == 409
}

Then(~/^the object is created$/) { ->
    assert created
    assert gson.fromJson(created, Map).id
}
