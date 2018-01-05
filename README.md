# Bernini

[ ![Download](https://api.bintray.com/packages/joaobiriba/maven/bernini/images/download.svg) ](https://bintray.com/joaobiriba/maven/bernini/_latestVersion)

Bernini is a library developed in Kotlin making easy to use the [Google Poly API](https://developers.google.com/poly/) in 
your application

This library take its name from [Bernini](https://en.wikipedia.org/wiki/Gian_Lorenzo_Bernini)

<img src="https://upload.wikimedia.org/wikipedia/commons/thumb/d/d5/Gian_Lorenzo_Bernini%2C_self-portrait%2C_c1623.jpg/200px-Gian_Lorenzo_Bernini%2C_self-portrait%2C_c1623.jpg" alt="Bernini"/>

Download
--------

Download the latest JAR or grab via Maven:
```xml
<dependency>
  <groupId>com.laquysoft.bernini</groupId>
  <artifactId>bernini</artifactId>
  <version>0.0.1</version>
  <type>pom</type>
</dependency>
```
or Gradle:
```groovy
compile 'com.laquysoft.bernini:bernini:0.0.1'
```

Features
-----
You can download an OBJ asset and its resources from Poly using them in your applications
just passing its Poly ID

Usage
-----

Create a `Bernini()` with your API_KEY (you can create an API_KEY following these [instructions](https://developers.google.com/poly/develop/api)):
```kotlin
    val bernini = Bernini().withApiKey(API_KEY)
```
Later you can download all you need to display the model you want in Async way just
calling `getModel(ASSET_ID)` with the ASSET_ID you want

```kotlin
 launch {
            val drawOrder = async {
                bernini.getModel(ASSET_ID)
            }
            resourcesList = drawOrder.await()
        }
```
This library use the coroutines experimental Kotlin's feature
## DEMO APP
In the app directory there is an Android application in Kotlin using this library
with [ARCore](https://developers.google.com/ar/)

## TODO
* Cover all the Poly API calls
* Add tests!
* More sample apps
### Development

Want to contribute? Great!
Feel free to open issues at the moment





