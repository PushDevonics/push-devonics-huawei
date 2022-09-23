[![Release](https://jitpack.io/v/PushDevonics/push-devonics-huawei.svg)](https://jitpack.io/#PushDevonics/push-devonics-huawei)

[comment]: <> (Add it to you settings.gradle in repositories:)

[comment]: <> (    repositories {)

[comment]: <> (            google&#40;&#41;)

[comment]: <> (            mavenCentral&#40;&#41;)

[comment]: <> (            maven { url 'https://jitpack.io' })

[comment]: <> (    })

[comment]: <> (and:)

[comment]: <> (    dependencies {)

[comment]: <> (        implementation platform&#40;'com.google.firebase:firebase-bom:28.3.1'&#41;)

[comment]: <> (        implementation 'com.google.firebase:firebase-messaging-ktx')

[comment]: <> (        implementation 'com.github.PushDevonics:push-devonics-android:latest version')

[comment]: <> (    })

[comment]: <> (If you want single Activity:)
    
[comment]: <> (Kotlin:)

[comment]: <> (MainActivity:)

[comment]: <> (    private lateinit var pushDevonics: PushDevonics)
    
[comment]: <> (MainActivity in onCreate&#40;&#41;:)

[comment]: <> (    pushDevonics = PushDevonics&#40;this, "appId"&#41;)

[comment]: <> (    lifecycle.addObserver&#40;pushDevonics&#41;)
    
[comment]: <> (    // If you need internalId)

[comment]: <> (    val internalId = pushDevonics.getInternalId&#40;&#41;)
    
[comment]: <> (    // If you want add tag type String)

[comment]: <> (    pushDevonics.setTags&#40;"key", "value"&#41;)
    
[comment]: <> (    // If you need deeplink)

[comment]: <> (    val deepLink = pushDevonics.getDeeplink&#40;&#41;)
    
[comment]: <> (Java:)

[comment]: <> (MainActivity:)

[comment]: <> (    private PushDevonics pushDevonics;)
    
[comment]: <> (MainActivity in onCreate&#40;&#41;:)

[comment]: <> (    pushDevonics = new PushDevonics&#40;this, "appId"&#41;;)

[comment]: <> (    getLifecycle&#40;&#41;.addObserver&#40;pushDevonics&#41;;)
        
[comment]: <> (    // If you need internalId)

[comment]: <> (    String internalId = pushDevonics.getInternalId&#40;&#41;;)
    
[comment]: <> (    // If you want add tag type String)

[comment]: <> (    pushDevonics.setTags&#40;"key", "value"&#41;;)
    
[comment]: <> (    // If you need deeplink)

[comment]: <> (    String deeplink = pushDevonics.getDeeplink&#40;&#41;;)
        
[comment]: <> (If you want many Activities:)

[comment]: <> (You need to create a class inherited from Application and initiate it like for an Activity)

[comment]: <> (in onCreate&#40;&#41;:)

[comment]: <> (    pushDevonics = new PushDevonics&#40;this, "appId"&#41;;)

[comment]: <> (    registerActivityLifecycleCallbacks&#40;pushDevonics&#41;;)