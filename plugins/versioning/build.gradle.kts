import org.jetbrains.registerDokkaArtifactPublication

registerDokkaArtifactPublication("versioning-plugin") {
    artifactId = "versioning-plugin"
}

dependencies {
    implementation(project(":plugins:base"))
    implementation(project(":plugins:templating"))

    val coroutines_version: String by project
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutines_version")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.11.1")
    val kotlinx_html_version: String by project
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:$kotlinx_html_version")

    val jsoup_version: String by project
    implementation("org.jsoup:jsoup:$jsoup_version")
    implementation("org.apache.maven:maven-artifact:3.6.3")
}