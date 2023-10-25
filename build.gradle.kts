import com.bmuschko.gradle.docker.tasks.image.DockerBuildImage
import io.micronaut.gradle.docker.MicronautDockerfile
import org.apache.tools.ant.taskdefs.condition.Os

plugins {
    id("groovy")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.micronaut.application") version "4.0.4"
    id("io.micronaut.aot") version "4.0.4"
}

version = "0.1"
group = "io.wangler"

repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("io.micronaut:micronaut-http-validation")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")
    implementation("io.micronaut.serde:micronaut-serde-jackson")
    implementation("io.micronaut.views:micronaut-views-thymeleaf")
    compileOnly("io.micronaut:micronaut-http-client")
    runtimeOnly("ch.qos.logback:logback-classic")
    testImplementation("io.micronaut:micronaut-http-client")
}


application {
    mainClass.set("io.wangler.Application")
}
java {
    sourceCompatibility = JavaVersion.toVersion("17")
    targetCompatibility = JavaVersion.toVersion("17")
}

graalvmNative.toolchainDetection.set(false)
micronaut {
    runtime("netty")
    testRuntime("spock2")
    processing {
        incremental(true)
        annotations("io.wangler.*")
    }
    aot {
    // Please review carefully the optimizations enabled below
    // Check https://micronaut-projects.github.io/micronaut-aot/latest/guide/ for more details
        optimizeServiceLoading.set(false)
        convertYamlToJava.set(false)
        precomputeOperations.set(true)
        cacheEnvironment.set(true)
        optimizeClassLoading.set(true)
        deduceEnvironment.set(true)
        optimizeNetty.set(true)
    }
}

val projectVersion = System.getenv("GITHUB_REF_NAME")?: "dummy"
val imageName = "ghcr.io/saw303/nativeimagepebbleview-thymeleaf"

tasks.named<DockerBuildImage>("dockerBuild") {
    images.add("${imageName}:${projectVersion}")
}

tasks.named<DockerBuildImage>("optimizedDockerBuild") {
    images.add("${imageName}:${projectVersion}-optimized")
}

tasks.named<DockerBuildImage>("dockerBuildNative") {
    images.add("${imageName}:${projectVersion}-native")
}

tasks.named<DockerBuildImage>("optimizedDockerBuildNative") {
    images.add("${imageName}:${projectVersion}-native-optimized")
}

tasks.named<MicronautDockerfile>("dockerfile") {
    baseImage.set("azul/zulu-openjdk:21-jre")
    instruction("""RUN groupadd app && useradd -rm -d /home/app -s /bin/bash -g app -u 1001 app""")
    instruction("""USER app""")
}

tasks.named<MicronautDockerfile>("optimizedDockerfile") {
    baseImage.set("azul/zulu-openjdk:21-jre")
    instruction("""RUN groupadd app && useradd -rm -d /home/app -s /bin/bash -g app -u 1001 app""")
    instruction("""USER app""")
}

tasks.named<io.micronaut.gradle.docker.NativeImageDockerfile>("dockerfileNative") {

    graalArch.set(if (Os.isArch("aarch64")) "aarch64" else "amd64")

    baseImage("ubuntu:23.10")

    instruction("EXPOSE 8080")
    instruction("""RUN groupadd app && useradd -rm -d /home/app -s /bin/bash -g app -u 1001 app""")
    instruction("""USER app""")
}

tasks.named<io.micronaut.gradle.docker.NativeImageDockerfile>("optimizedDockerfileNative") {

    graalArch.set(if (Os.isArch("aarch64")) "aarch64" else "amd64")

    baseImage("ubuntu:23.10")

    instruction("EXPOSE 8080")
    instruction("""RUN groupadd app && useradd -rm -d /home/app -s /bin/bash -g app -u 1001 app""")
    instruction("""USER app""")
}


