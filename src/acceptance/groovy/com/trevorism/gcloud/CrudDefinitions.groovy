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

def stored = null
int writtenCount = 0
String listedKinds = null

def store = { String kind, String id, String name ->
    return gson.fromJson(secureHttpClient.post("${objectUrl}/${kind}".toString(), gson.toJson([id: id, name: name])), Map)
}

def readAll = { String kind ->
    return gson.fromJson(secureHttpClient.get("${objectUrl}/${kind}".toString()), List)
}

Given(~/^the kind "([^"]*)" holds no objects$/) { String kind ->
    secureHttpClient.put("${objectUrl}/${kind}".toString(), "[]")
    assert readAll(kind).isEmpty()
}

Given(~/^an object with id "([^"]*)" and name "([^"]*)" is stored in kind "([^"]*)"$/) { String id, String name, String kind ->
    stored = store(kind, id, name)
}

When(~/^I store an object with id "([^"]*)" and name "([^"]*)" in kind "([^"]*)"$/) { String id, String name, String kind ->
    stored = store(kind, id, name)
}

When(~/^I update id "([^"]*)" in kind "([^"]*)" to name "([^"]*)"$/) { String id, String kind, String name ->
    secureHttpClient.put("${objectUrl}/${kind}/${id}".toString(), gson.toJson([name: name]))
}

When(~/^I delete id "([^"]*)" from kind "([^"]*)"$/) { String id, String kind ->
    def deleted = gson.fromJson(secureHttpClient.delete("${objectUrl}/${kind}/${id}".toString()), Map)
    assert id == deleted.id
}

When(~/^I bulk store an object with id "([^"]*)" in kind "([^"]*)"$/) { String id, String kind ->
    writtenCount = gson.fromJson(secureHttpClient.put("${objectUrl}/${kind}".toString(), gson.toJson([[id: id, name: "bulk"]])), Integer)
}

When(~/^I list the stored kinds without authenticating$/) { ->
    listedKinds = new URL("${objectUrl}/").text
}

Then(~/^the stored object has id "([^"]*)"$/) { String id ->
    assert id == stored.id
}

Then(~/^reading id "([^"]*)" from kind "([^"]*)" returns name "([^"]*)"$/) { String id, String kind, String name ->
    def entity = gson.fromJson(secureHttpClient.get("${objectUrl}/${kind}/${id}".toString()), Map)
    assert id == entity.id
    assert name == entity.name
}

Then(~/^id "([^"]*)" is no longer in kind "([^"]*)"$/) { String id, String kind ->
    int statusCode = 0
    try {
        secureHttpClient.get("${objectUrl}/${kind}/${id}".toString())
    } catch (InvalidRequestException e) {
        statusCode = e.statusCode
    }
    assert 404 == statusCode
    assert !readAll(kind).find { it.id == id }
}

Then(~/^kind "([^"]*)" holds (\d+) objects$/) { String kind, int count ->
    assert count == readAll(kind).size()
}

Then(~/^kind "([^"]*)" holds an object with id "([^"]*)"$/) { String kind, String id ->
    assert readAll(kind).find { it.id == id }
}

Then(~/^(\d+) object was written$/) { int count ->
    assert count == writtenCount
}

Then(~/^"([^"]*)" is one of the listed kinds$/) { String kind ->
    assert gson.fromJson(listedKinds, List).contains(kind)
}
