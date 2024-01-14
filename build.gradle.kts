plugins {
    id("java")
}



group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation ("org.apache.logging.log4j:log4j-api:2.7")
    implementation("ch.qos.logback:logback-classic:1.2.6")
    implementation("org.mockito:mockito-core:4.3.1")
    // https://mvnrepository.com/artifact/org.awaitility/awaitility
    testImplementation ("org.awaitility:awaitility:3.0.0")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}