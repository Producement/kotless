import tanvd.kosogor.proxy.publishJar

group = rootProject.group
version = rootProject.version


dependencies {
    api(project(":dsl:common:lang-common"))

    implementation(kotlin("reflect"))
    implementation("org.reflections", "reflections", "0.9.11")
}

publishJar {
    bintray {
        username = "tanvd"
        repository = "io.kotless"
        info {
            description = "Kotless DSL"
            githubRepo = "https://github.com/JetBrains/kotless"
            vcsUrl = "https://github.com/JetBrains/kotless"
            labels.addAll(listOf("kotlin", "serverless", "web", "devops", "faas", "lambda"))
        }
    }
}

