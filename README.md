[![Release](https://jitpack.io/v/PushDevonics/push-devonics-huawei.svg)](https://jitpack.io/#PushDevonics/push-devonics-huawei)

Attention to use this library, you must update the SDK to version 33

Before you get started, you must register as a Huawei developer 
and complete identity verification on HUAWEI Developers.

Create an app by following instructions in
https://developer.huawei.com/consumer/en/doc/distribution/app/agc-help-createproject-0000001100334664
and
https://developer.huawei.com/consumer/en/doc/distribution/app/agc-help-createapp-0000001146718717

Before releasing an app, you must generate a signing certificate fingerprint locally 
based on the signing certificate and configure it in AppGallery Connect.

Copy the agconnect-services.json file to your app's root directory.
Copy the storeFile file('xxx.jks') file to your app's root directory.

For more information:
https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/android-config-agc-0000001050170137

Add it to you build.gradle(project):

    buildscript {
        repositories {
            google()
            mavenCentral()
            maven { url 'https://jitpack.io' }
            maven { url 'https://developer.huawei.com/repo/' }
}

    dependencies {
        classpath 'com.huawei.agconnect:agcp:1.6.0.300'
    }
}


Add it to you settings.gradle in repositories:

    pluginManagement {
        repositories {
            gradlePluginPortal()
            google()
            mavenCentral()
            maven { url 'https://jitpack.io' }
            maven {url 'https://developer.huawei.com/repo/'}
        }
    }
    dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        repositories {
            google()
            mavenCentral()
            maven { url 'https://jitpack.io' }
            maven {url 'https://developer.huawei.com/repo/'}
        }
    }

Add it to you build.gradle(app):

    plugins {
    id 'com.huawei.agconnect'
}

    android {
        signingConfigs {
            config {
                // Replace xxx with your signing certificate.
                keyAlias 'xxx'
                keyPassword 'xxx'
                storeFile file('xxx.jks')
                storePassword 'xxx'
                v1SigningEnabled true
                v2SigningEnabled true
            }
    `   }
    buildTypes {
        debug {
            signingConfig signingConfigs.config
        }
        release {
            signingConfig signingConfigs.config
        }
    }
    dependencies {
        implementation 'com.github.PushDevonics:push-devonics-huawei:latest-version'
    }
    
Kotlin:

MainActivity:

    private lateinit var pushDevonics: PushDevonics
    
MainActivity in onCreate():

    pushDevonics = PushDevonics(this, "appId")
    lifecycle.addObserver(pushDevonics)
    
    // If you need internalId
    val internalId = pushDevonics.getInternalId()
    
    // If you want add tag type String
    pushDevonics.setTags("key", "value")
    
    // If you need deeplink
    val deepLink = pushDevonics.getDeeplink()
    
Java:

MainActivity:

    private PushDevonics pushDevonics;
    
MainActivity in onCreate():

    pushDevonics = new PushDevonics(this, "appId");
    getLifecycle().addObserver(pushDevonics);
        
    // If you need internalId
    String internalId = pushDevonics.getInternalId();
    
    // If you want add tag type String
    pushDevonics.setTags("key", "value");
    
    // If you need deeplink
    String deeplink = pushDevonics.getDeeplink();
